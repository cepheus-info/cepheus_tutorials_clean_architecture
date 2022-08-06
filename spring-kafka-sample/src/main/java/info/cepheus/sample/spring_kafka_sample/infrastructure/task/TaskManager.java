package info.cepheus.sample.spring_kafka_sample.infrastructure.task;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@KafkaListener(id = "task", topics = {TaskProcess.TaskCompleted, TaskProcess.TaskCompleted + ".DLT", TaskProcess.ChildTaskInitialized, TaskProcess.ChildTaskProgressed})
public class TaskManager {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    static BehaviorSubject<Task> subject = BehaviorSubject.create();

    @Transactional
    public boolean initialize(String transactionId, String organizationId, String taskType) {
        List<Task> incompleteTasks = taskRepository.findIncompleteTaskByOrganizationIdAndTaskType(organizationId, taskType);
        if (incompleteTasks.size() > 0) {
            return false;
        }
        Task task = new Task();
        task.setId(transactionId);
        task.setOrganizationId(organizationId);
        task.setTaskType(taskType);
        task.setTaskStatus(TaskStatus.Initialized);
        taskRepository.saveAndFlush(task);

        log.info("TaskManager task {} Initialized", transactionId);
        return true;
    }

    @Transactional
    public void cancel(String transactionId) {
        Task task = taskRepository.findById(transactionId).orElse(null);
        if (task == null) {
            task = new Task();
            task.setId(transactionId);
        }
        task.setTaskStatus(TaskStatus.Cancelled);
        taskRepository.saveAndFlush(task);

        log.info("TaskManager task {} Canceled", transactionId);
    }

    @Transactional
    public boolean inProgress(String transactionId) {
        Task task = taskRepository.findById(transactionId).orElse(null);
        if (task == null || task.getTaskStatus().equals(TaskStatus.Cancelled)) {
            return false;
        }
        task.setTaskStatus(TaskStatus.InProgress);
        taskRepository.saveAndFlush(task);

        log.info("TaskManager task {} InProgress", transactionId);
        return true;
    }

    @Async
    @Transactional
    public void initializeChildSteps(String transactionId, List<String> taskStepIds) {
        kafkaTemplate.send(TaskProcess.ChildTaskInitialized, transactionId,
                new ChildTaskInitializedMessage(transactionId, taskStepIds));

        log.info("TaskManager task {} initialized childSteps", transactionId);
    }

    @Async
    @Transactional
    public void update(String transactionId, TaskStatus taskStatus) {
        log.info("TaskManager task {} updated", transactionId);
        Task task = taskRepository.findById(transactionId).orElse(null);
        if (task == null) {
            task = new Task();
            task.setId(transactionId);
        }
        task.setTaskStatus(taskStatus);
        taskRepository.save(task);

        if (!Objects.equals(taskStatus, TaskStatus.Completed)) {
            subject.onNext(task);
        } else {
            subject.onNext(task);
        }
    }

    @Async
    @Transactional
    public void completeChildStepIfPresent(String transactionId, List<String> taskStepIds) {
        if (transactionId == null) {
            return;
        }
        completeChildStep(transactionId, taskStepIds);
    }

    @Async
    @Transactional
    public void completeChildStep(@NotNull String transactionId, List<String> taskStepIds) {
        kafkaTemplate.send(TaskProcess.ChildTaskProgressed, transactionId, new ChildTaskProgressedMessage(transactionId, taskStepIds));

        log.info("TaskManager task {} completeChildStep size: {}", transactionId, taskStepIds.size());
    }

    @KafkaHandler
    public void on(ChildTaskInitializedMessage message) {
        log.warn("onChildTaskInitializedMessage {}", message);
    }

    @KafkaHandler
    public void on(ChildTaskProgressedMessage message) {
        log.warn("onChildTaskProgressedMessage {}", message);
    }

    public void acknowledgeWhenCompletion(String transactionId, Acknowledgment acknowledgment) {
        ThreadLocal<Disposable> subscription = ThreadLocal.withInitial(() -> subject.subscribe());
        var subscribe = subject.filter(task -> task.getId().equals(transactionId))
                .subscribe(task -> {
                    if (task.getTaskStatus().equals(TaskStatus.Completed)) {
                        acknowledgment.acknowledge();
                        log.warn("onBatchBasicInfoMessage acknowledgement.acknowledge update");
                        subscription.get().dispose();
                    }
                });

        subscription.set(subscribe);
//
//        // todo: use kafka-streams to track related childTaskCompletion.
//        queryGateway.subscriptionQuery(new TaskQuery(transactionId), ResponseTypes.instanceOf(Task.class), ResponseTypes.instanceOf(Task.class))
//                .updates()
//                .subscribe(v -> {
//                    log.info("onBatchBasicInfoMessage on update");
//                    if (v.getTaskStatus().equals(TaskStatus.Completed)) {
//                        acknowledgment.acknowledge();
//                    }
//                }, (error) -> {
//                    log.error("onBatchBasicInfoMessage on update error");
//                }, () -> {
//                    acknowledgment.acknowledge();
//                    log.warn("onBatchBasicInfoMessage acknowledgement.acknowledge update");
//                });
    }

}

package info.cepheus.sample.spring_kafka_sample.infrastructure.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    @Query(value = "select a from Task as a where a.organizationId = :organizationId and a.taskType = :taskType and a.taskStatus not in ('Completed', 'Canceled')")
    List<Task> findIncompleteTaskByOrganizationIdAndTaskType(@Param("organizationId") String organizationId, @Param("taskType") String taskType);
}

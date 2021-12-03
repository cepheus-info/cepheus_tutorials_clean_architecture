package info.cepheus.clean_architecture_quarkus.domain.person

import info.cepheus.axon.infrastructure.boundary.command.CommandModelAggregate
import info.cepheus.axon.infrastructure.boundary.command.CommandModelAggregateIdentifier
import info.cepheus.axon.infrastructure.boundary.command.CommandModelCommandHandler
import info.cepheus.cepheus_person_coreapi.PersonCreatedEvent
import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import info.cepheus.clean_architecture_quarkus.coreapi.command.CreateSinglePersonCommand
import info.cepheus.clean_architecture_quarkus.coreapi.exception.NullOrEmptyNameException
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateRoot
import org.axonframework.modelling.command.CreationPolicy
import javax.enterprise.context.ApplicationScoped

@CommandModelAggregate(type = "Person")
class PersonAggregate {
    @CommandModelAggregateIdentifier
    lateinit var personId: String

    var name: String? = null

    @CommandModelCommandHandler
    @CreationPolicy(value = AggregateCreationPolicy.CREATE_IF_MISSING)
    fun handle(command: CreateSinglePersonCommand): ExecutionResult<*> {

        Thread.sleep(1000L)
        if (command.name.isNullOrBlank()) {
            throw NullOrEmptyNameException("name is null or empty, id: ${command.personId}")
        }
        AggregateLifecycle.apply(PersonCreatedEvent(command.personId!!, command.name!!))
        val success: ExecutionResult<*> = ExecutionResult.success()
        return ExecutionResult.success(command.personId)
    }

    @EventSourcingHandler
    fun on(event: PersonCreatedEvent) {
        this.personId = event.personId
        this.name = event.name
    }
}
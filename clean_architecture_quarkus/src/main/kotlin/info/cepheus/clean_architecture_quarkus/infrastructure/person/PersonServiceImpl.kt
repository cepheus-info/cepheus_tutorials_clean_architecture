package info.cepheus.clean_architecture_quarkus.infrastructure.person

import info.cepheus.axon.infrastructure.boundary.command.CommandEmitterService
import info.cepheus.clean_architecture_quarkus.application.person.CreatePersonDto
import info.cepheus.clean_architecture_quarkus.application.person.PersonService
import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PersonServiceImpl : PersonService {

    @Inject
    lateinit var commandGateway: CommandEmitterService

    override fun create(person: CreatePersonDto): ExecutionResult<*> {
        return commandGateway.sendAndWaitFor(person.withId(UUID.randomUUID().toString()))
    }

    override fun createAsync(person: CreatePersonDto): CompletableFuture<ExecutionResult<*>> {
        return commandGateway.send(person.withId(UUID.randomUUID().toString()))
    }
}
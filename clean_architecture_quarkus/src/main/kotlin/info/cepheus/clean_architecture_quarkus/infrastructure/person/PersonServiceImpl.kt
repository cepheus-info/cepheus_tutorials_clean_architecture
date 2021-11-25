package info.cepheus.clean_architecture_quarkus.infrastructure.person

import info.cepheus.axon.infrastructure.boundary.command.CommandEmitterService
import info.cepheus.clean_architecture_quarkus.api.person.CreatePersonDto
import info.cepheus.clean_architecture_quarkus.application.person.PersonService
import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import info.cepheus.clean_architecture_quarkus.coreapi.command.CreateSinglePersonCommand
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.beanutils.PropertyUtils
import java.util.concurrent.CompletableFuture
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PersonServiceImpl : PersonService {

    @Inject
    lateinit var commandGateway: CommandEmitterService

    override fun create(person: CreateSinglePersonCommand): ExecutionResult<*> {
        return commandGateway.sendAndWaitFor(person)
    }

    override fun createAsync(person: CreateSinglePersonCommand): CompletableFuture<ExecutionResult<*>> {
        return commandGateway.send(person)
    }
}
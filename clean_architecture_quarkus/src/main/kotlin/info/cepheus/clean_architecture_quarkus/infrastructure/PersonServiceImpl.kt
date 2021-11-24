package info.cepheus.clean_architecture_quarkus.infrastructure

import info.cepheus.axon.infrastructure.boundary.command.CommandEmitterService
import info.cepheus.clean_architecture_quarkus.api.CreatePersonDto
import info.cepheus.clean_architecture_quarkus.application.PersonService
import info.cepheus.clean_architecture_quarkus.coreapi.command.CreateSinglePersonCommand
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PersonServiceImpl : PersonService {

    @Inject
    lateinit var commandGateway: CommandEmitterService

    override fun create(person: Pair<String, CreatePersonDto>) {
        with(commandGateway) {
            val command = CreateSinglePersonCommand()
            command.personId = person.first
            command.name = person.second.name
            this.sendAndWaitFor<String>(command)
        }
    }
}
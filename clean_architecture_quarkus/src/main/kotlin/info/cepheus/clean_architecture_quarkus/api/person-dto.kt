package info.cepheus.clean_architecture_quarkus.api

import info.cepheus.clean_architecture_quarkus.coreapi.command.CreateSinglePersonCommand

data class CreatePersonDto(
        var name: String? = null
) {
    fun toCreateSinglePersonCommand(id: String): CreateSinglePersonCommand {
        val command = CreateSinglePersonCommand()
        command.personId = id
        command.name = this.name
        return command
    }
}
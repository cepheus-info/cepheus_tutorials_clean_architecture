package info.cepheus.clean_architecture_quarkus.api.person

import info.cepheus.clean_architecture_quarkus.coreapi.command.CreateSinglePersonCommand
import org.apache.commons.beanutils.PropertyUtils

data class CreatePersonDto(
        var name: String? = null
) {
    fun withId(id: String): CreateSinglePersonCommand {
        val command = CreateSinglePersonCommand()
        PropertyUtils.copyProperties(command, this)
        command.personId = id
        return command
    }
}
package info.cepheus.clean_architecture_quarkus.coreapi.command

import info.cepheus.axon.infrastructure.boundary.command.CommandTargetAggregateIdentifier

data class CreateSinglePersonCommand(
        @CommandTargetAggregateIdentifier
        var personId: String?,
        var name: String?
) {
    constructor() : this(null, null)
}

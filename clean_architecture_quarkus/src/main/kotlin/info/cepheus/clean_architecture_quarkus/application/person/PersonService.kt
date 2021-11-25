package info.cepheus.clean_architecture_quarkus.application.person

import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import info.cepheus.clean_architecture_quarkus.coreapi.command.CreateSinglePersonCommand
import java.util.concurrent.CompletableFuture

interface PersonService {
    fun create(person: CreateSinglePersonCommand): ExecutionResult<*>

    fun createAsync(person: CreateSinglePersonCommand): CompletableFuture<ExecutionResult<*>>
}
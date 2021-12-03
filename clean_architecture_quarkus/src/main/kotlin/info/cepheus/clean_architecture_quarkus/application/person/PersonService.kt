package info.cepheus.clean_architecture_quarkus.application.person

import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import java.util.concurrent.CompletableFuture

interface PersonService {
    fun create(person: CreatePersonDto): ExecutionResult<*>

    fun createAsync(person: CreatePersonDto): CompletableFuture<ExecutionResult<*>>
}
package info.cepheus.clean_architecture_quarkus.api.person

import info.cepheus.clean_architecture_quarkus.application.person.CreatePersonDto
import info.cepheus.clean_architecture_quarkus.application.person.PersonService
import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import java.net.URI
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/api/person")
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {

    @Inject
    lateinit var personService: PersonService

    @ConfigProperty(name = "quarkus.oauth2.client-id")
    lateinit var clientId: String

    /**
     * Note that in JAX/RS 2, we use @Suspended to return AsyncResponse
     */
    @DelicateCoroutinesApi
    @POST
    fun create(@RequestBody person: CreatePersonDto, @Suspended asyncResponse: AsyncResponse) {
        val id = UUID.randomUUID().toString()

        // Note: define inline coroutineExceptionHandler here to resume asyncResponse
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            LOGGER.log(Level.SEVERE, exception.message)
            val result = Response.serverError().entity(ExecutionResult.failed()).build()
            // write to response
            asyncResponse.resume(result)
        }

        // use coroutine to make use of await & suspend function.
        GlobalScope.launch(coroutineExceptionHandler) {
            val executionResult = personService.createAsync(person).await()

            val result = when (executionResult.status) {
                ExecutionStatus.SUCCESS -> {
                    val personId = executionResult.payload
                    val r = object {
                        val identifier = personId
                        val clientId = this@PersonResource.clientId
                    }
                    Response.created(URI.create("/api/person/${personId}")).entity(r).build()
                }
                ExecutionStatus.FAILED -> {
                    Response.serverError().entity(executionResult).build()
                }
            }
            // write to response
            asyncResponse.resume(result)
        }
    }

    @POST
    @Path("callback")
    fun createCallback(@RequestBody person: CreatePersonDto, @Suspended asyncResponse: AsyncResponse) {
        personService.createAsync(person).thenAccept {
            val result = when (it.status) {
                ExecutionStatus.SUCCESS -> {
                    val personId = it.payload
                    val r = object {
                        val identifier = personId
                    }
                    Response.created(URI.create("/api/person/${personId}")).entity(r).build()
                }
                ExecutionStatus.FAILED -> {
                    Response.serverError().entity(it).build()
                }
            }
            // write to response
            asyncResponse.resume(result)
        }.handle { res, ex ->
            LOGGER.log(Level.SEVERE, ex.message)
            val result = Response.serverError().entity(ExecutionResult.failed()).build()
            // write to response
            asyncResponse.resume(result)
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(PersonResource::class.java.name)
    }
}
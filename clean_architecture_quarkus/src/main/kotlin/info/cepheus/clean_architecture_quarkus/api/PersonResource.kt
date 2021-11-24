package info.cepheus.clean_architecture_quarkus.api

import info.cepheus.clean_architecture_quarkus.application.PersonService
import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import java.net.URI
import java.util.*
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/api/person")
@Produces(MediaType.APPLICATION_JSON)
class PersonResource {

    @Inject
    lateinit var personService: PersonService

    @POST
    fun create(@RequestBody person: CreatePersonDto): Response? {
        val id = UUID.randomUUID().toString()

        personService.create(id to person)

        val result = ExecutionResult.success(object {
            val identifier = id
        })
        return Response.created(URI.create("/api/person/${id}")).entity(result).build()
    }

}
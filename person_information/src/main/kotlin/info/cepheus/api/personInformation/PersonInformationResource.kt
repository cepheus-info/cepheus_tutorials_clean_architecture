package info.cepheus.api.personInformation

import info.cepheus.application.PersonServiceProxy
import info.cepheus.coreapi.ExecutionResult
import info.cepheus.query.personInformation.PersonInformation
import info.cepheus.query.personInformation.PersonInformationRepository
import info.cepheus.query.personInformation.PersonInformationRepository.PersonInformationDto
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.*
import java.util.logging.Level
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
class PersonInformationResource {

    @Inject
    lateinit var personInformationRepository: PersonInformationRepository

    @Inject
    @RestClient
    lateinit var personServiceProxy: PersonServiceProxy

    @GET
    fun list(): List<PersonInformation> {
        return personInformationRepository.listAll()
    }

    @GET
    @Path("{id}")
    fun findById(@PathParam("id") id: String): PersonInformation? {
        return personInformationRepository.findById(UUID.fromString(id))
    }

    @GET
    @Path("/salary-total-gt0")
    fun findBySalaryTotalGt0(): List<PersonInformationDto> {
        return personInformationRepository.findSalaryGt0Native()
    }

    @GET
    @Path("/salary-total-gt0-proxy")
    fun findBySalaryTotalGt0Proxy(@Suspended asyncResponse: AsyncResponse) {
        personServiceProxy.getSalaryTotalGt0().toCompletableFuture().thenAcceptAsync {
            asyncResponse.resume(it)
        }.handle { res, ex ->
            val result = Response.serverError().entity(ExecutionResult.failed()).build()
            // write to response
            asyncResponse.resume(result)
        }
    }

}
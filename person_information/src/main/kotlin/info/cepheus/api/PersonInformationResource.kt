package info.cepheus.api

import info.cepheus.query.personInformation.PersonInformation
import info.cepheus.query.personInformation.PersonInformationRepository
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/hello")
class PersonInformationResource {

    @Inject
    lateinit var personInformationRepository: PersonInformationRepository

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun list(): List<PersonInformation> {
        return personInformationRepository.listAll()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findById(@PathParam("id") id: String): PersonInformation? {
        return personInformationRepository.findById(id)
    }

}
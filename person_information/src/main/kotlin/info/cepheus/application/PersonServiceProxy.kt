package info.cepheus.application

import info.cepheus.query.personInformation.PersonInformationRepository
import info.cepheus.query.personInformation.PersonInformationRepository.PersonInformationDto
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.jboss.resteasy.spi.touri.URITemplate
import java.util.concurrent.CompletionStage
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path(value = "/person")
@RegisterRestClient
interface PersonServiceProxy {
    @GET
    @Path("/salary-total-gt0")
    fun getSalaryTotalGt0(): CompletionStage<List<PersonInformationDto>>
}
package info.cepheus.query.personInformation

import info.cepheus.cepheus_person_coreapi.PersonCreatedEvent
import org.axonframework.eventhandling.EventHandler
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PersonInformationProjection {

    @Inject
    lateinit var personInformationRepository: PersonInformationRepository

    @EventHandler
    fun on(event: PersonCreatedEvent) {
        personInformationRepository.run {
            val personInformation = PersonInformation()
            personInformation.id = event.personId
            personInformation.name = event.name
            persist(personInformation)
        }
    }
}
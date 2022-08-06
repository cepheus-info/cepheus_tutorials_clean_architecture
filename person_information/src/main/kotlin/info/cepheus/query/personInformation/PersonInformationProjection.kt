package info.cepheus.query.personInformation

import info.cepheus.cepheus_person_coreapi.PersonCreatedEvent
import info.cepheus.infrastructure.CurrentTenant
import io.quarkus.arc.Unremovable
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.yaml.snakeyaml.introspector.PropertyUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

/**
 * Note that @Unremovable is important that we registered all handlers manually,
 * which were removed beans if not set this Annotation
 */
@Unremovable
@ApplicationScoped
@ProcessingGroup("info.cepheus.query.person_information")
class PersonInformationProjection {

    /**
     * Note that @Transactional is important as the Context might not be activated if not set.
     * Note multi-tenancy might not work if we did not set CurrentTenant here,
     * by default the RequestContext is needed which not present in Message-Broker manner.
     */
    @EventHandler
    @Transactional
    fun on(event: PersonCreatedEvent) {
        CurrentTenant.use { // multi-tenancy
            it.tenantId = CurrentTenant.defaultTenantId

            PersonInformation().run {
                this.id = UUID.fromString(event.personId)
                this.name = event.name
                // persist & clear
                this.persist()
            }
        }
    }
}
package info.cepheus.query.personInformation

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
class PersonInformationRepository : PanacheRepositoryBase<PersonInformation, String> {
}
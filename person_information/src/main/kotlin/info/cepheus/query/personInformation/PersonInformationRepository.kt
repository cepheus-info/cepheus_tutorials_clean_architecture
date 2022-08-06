package info.cepheus.query.personInformation

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName
import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Tuple
import javax.transaction.Transactional

@ApplicationScoped
class PersonInformationRepository : PanacheRepositoryBase<PersonInformation, UUID> {

    class PersonInformationDto(
        @ProjectedFieldName("p.name") var name: String?,
        @ProjectedFieldName("w.salaryTotal") var salaryTotal: Double?
    )

    fun findSalaryGt0(): List<PersonInformationDto> {
        return find(query = "from PersonInformation p")
            .list()
            .map { p -> PersonInformationDto(p.name, p.wageInformation.salaryTotal) }
    }

    fun findSalaryGt0Native(): List<PersonInformationDto> {
        return find(query = "from PersonInformation p join WageInformation w on p.id = w.id where w.salaryTotal > 0")
            .project(PersonInformationDto::class.java)
            .list()
    }
}
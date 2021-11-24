package info.cepheus.query.personInformation

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PersonInformation : PanacheEntityBase {
    @Id
    lateinit var id: String

    var name: String? = null
}
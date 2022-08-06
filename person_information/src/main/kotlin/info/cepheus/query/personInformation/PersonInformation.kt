package info.cepheus.query.personInformation

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import org.hibernate.annotations.Comment
import java.util.*
import javax.persistence.*
import org.hibernate.annotations.Table as HibernateTable

@Entity
@Table(name = "t_person_information")
@HibernateTable(comment = "person_information人员基础信息", appliesTo = "t_person_information")
open class PersonInformation : PanacheEntityBase {
    @Id
    @Comment("人员Id")
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    open lateinit var id: UUID

    @Comment("人员姓名")
    @Column(name = "name", columnDefinition = "varchar(255)")
    open var name: String? = null

    @OneToOne(targetEntity = WageInformation::class, mappedBy = "personInformation")
    open lateinit var wageInformation: WageInformation
}
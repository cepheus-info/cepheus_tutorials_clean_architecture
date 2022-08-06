package info.cepheus.query.personInformation

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import org.hibernate.annotations.Comment
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "t_wage_information")
@org.hibernate.annotations.Table(comment = "wage_information人员工资信息", appliesTo = "t_wage_information")
open class WageInformation : PanacheEntityBase {

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    open lateinit var id: UUID

    @Comment(value = "salary total")
    @Column(name = "salary_total", columnDefinition = "decimal(20, 2)", nullable = false)
    open var salaryTotal: Double = 0.0

    @MapsId
    @OneToOne(targetEntity = PersonInformation::class)
    @JoinColumn(name = "id")
    open lateinit var personInformation: PersonInformation

}
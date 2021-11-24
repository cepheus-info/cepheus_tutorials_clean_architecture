package info.cepheus.clean_architecture_quarkus.query

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import javax.persistence.Entity
import javax.persistence.MappedSuperclass

@Entity
class CodeBase : PanacheEntity() {

}
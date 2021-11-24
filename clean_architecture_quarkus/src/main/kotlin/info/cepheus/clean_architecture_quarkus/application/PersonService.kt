package info.cepheus.clean_architecture_quarkus.application

import info.cepheus.clean_architecture_quarkus.api.CreatePersonDto

interface PersonService {
    fun create(person: Pair<String, CreatePersonDto>)
}
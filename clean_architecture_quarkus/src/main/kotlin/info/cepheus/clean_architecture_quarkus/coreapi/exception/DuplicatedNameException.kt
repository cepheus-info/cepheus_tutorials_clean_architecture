package info.cepheus.clean_architecture_quarkus.coreapi.exception

import info.cepheus.clean_architecture_quarkus.coreapi.exception.CheckedBusinessErrorCode.DUPLICATED_NAME

data class DuplicatedNameException(val name: String?, val payload: String? = null) : CheckedBusinessException(
        errorMessage = "Given name: [$name] is duplicated",
        errorCode = DUPLICATED_NAME.name
)
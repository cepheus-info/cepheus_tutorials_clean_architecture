package info.cepheus.clean_architecture_quarkus.coreapi.exception

import info.cepheus.clean_architecture_quarkus.coreapi.exception.CheckedBusinessErrorCode.NULL_OR_EMPTY_NAME

data class NullOrEmptyNameException(val reason: String?) : CheckedBusinessException(
        errorMessage = "Given $reason name is null/empty",
        errorCode = NULL_OR_EMPTY_NAME.name
)

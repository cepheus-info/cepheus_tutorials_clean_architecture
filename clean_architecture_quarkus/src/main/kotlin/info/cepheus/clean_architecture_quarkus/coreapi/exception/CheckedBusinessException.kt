package info.cepheus.clean_architecture_quarkus.coreapi.exception

// CheckedBusinessException is non-transient and should not be treated as RuntimeException
open class CheckedBusinessException(
        val errorMessage: String,
        val errorCode: String,
) : Exception(errorMessage)
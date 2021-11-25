package info.cepheus.clean_architecture_quarkus.coreapi.exception

data class NullOrEmptyNameException(val reason: String?) : CheckedBusinessException(
        String.format("Given %s name is null/empty", reason),
        CheckedBusinessErrorCode.NULL_OR_EMPTY_NAME
)

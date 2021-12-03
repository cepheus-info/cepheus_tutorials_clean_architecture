package info.cepheus.clean_architecture_quarkus.coreapi.exception

data class AlwaysTrueException(val reason: String?) : CheckedBusinessException(
        String.format("Given expression %s always true", reason),
        CheckedBusinessErrorCode.ALWAYS_TRUE.name
)
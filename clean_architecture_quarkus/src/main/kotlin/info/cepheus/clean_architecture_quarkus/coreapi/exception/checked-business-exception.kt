package info.cepheus.clean_architecture_quarkus.coreapi.exception

enum class CheckedBusinessErrorCode(s: String) {
    ALWAYS_TRUE("always true"),
    NULL_OR_EMPTY_NAME("null or empty name"),
    DATA_ACCESS("data access"),
    DUPLICATED_NAME("duplicated name"),
    UNKNOWN("unknown error"),
    SYSTEM_ERROR("system error");

    val value = s;
}

data class CheckedBusinessError(
        val name: String,
        val code: CheckedBusinessErrorCode,
        val message: String
)

// CheckedBusinessException is non-transient and should not be treated as RuntimeException
open class CheckedBusinessException(
        val errorMessage: String,
        val errorCode: CheckedBusinessErrorCode
) : Exception(errorMessage)

data class AlwaysTrueException(val reason: String?) : CheckedBusinessException(
        String.format("Given expression %s always true", reason),
        CheckedBusinessErrorCode.ALWAYS_TRUE
)
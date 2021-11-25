package info.cepheus.clean_architecture_quarkus.coreapi.exception

data class DuplicatedNameException(val name: String?, val payload: String? = null) : CheckedBusinessException(
        String.format("Given name: [%s] is duplicated", name),
        CheckedBusinessErrorCode.DUPLICATED_NAME
)
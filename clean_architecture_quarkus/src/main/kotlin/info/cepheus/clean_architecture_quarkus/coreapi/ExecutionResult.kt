package info.cepheus.clean_architecture_quarkus.coreapi


class ExecutionResult<T>(val status: ExecutionStatus,
                         val payload: T? = null
) {
    companion object {

        @JvmStatic
        fun success(): ExecutionResult<String> {
            return ExecutionResult(ExecutionStatus.SUCCESS)
        }

        /**
         * A shortcut for creating an ok `ResponseResult` with the given body
         */
        @JvmStatic
        fun <T> success(payload: T?): ExecutionResult<T> {
            return ExecutionResult(ExecutionStatus.SUCCESS, payload)
        }

        @JvmStatic
        fun failed(): ExecutionResult<String> {
            return ExecutionResult(ExecutionStatus.FAILED)
        }
    }
}

enum class ExecutionStatus {
    SUCCESS, FAILED
}

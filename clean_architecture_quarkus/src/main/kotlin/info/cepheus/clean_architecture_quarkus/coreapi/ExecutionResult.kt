package info.cepheus.clean_architecture_quarkus.coreapi

class ExecutionResult<T>(
        val status: ExecutionStatus,
        val payload: T? = null,
) {
    companion object {
        /**
         * A shortcut for creating a success `ExecutionResult` with empty body
         */
        @JvmStatic
        fun success(): ExecutionResult<Any> {
            return ExecutionResult(ExecutionStatus.SUCCESS)
        }

        /**
         * A shortcut for creating a success `ExecutionResult` with the given body
         */
        @JvmStatic
        fun <T> success(payload: T?): ExecutionResult<T> {
            return ExecutionResult(ExecutionStatus.SUCCESS, payload)
        }

        /**
         * A shortcut for creating a failed `ExecutionResult` with empty body
         */
        @JvmStatic
        fun failed(): ExecutionResult<Any> {
            return ExecutionResult(ExecutionStatus.FAILED)
        }

        /**
         * A shortcut for creating a failed `ExecutionResult` with the given body
         */
        @JvmStatic
        fun <T> failed(payload: T?): ExecutionResult<T> {
            return ExecutionResult(ExecutionStatus.FAILED, payload)
        }
    }
}

enum class ExecutionStatus {
    SUCCESS, FAILED
}

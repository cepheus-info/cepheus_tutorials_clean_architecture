package info.cepheus.clean_architecture_quarkus.api

import info.cepheus.clean_architecture_quarkus.coreapi.ExecutionResult
import java.util.logging.Logger
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class DefaultExceptionMapper : ExceptionMapper<Exception> {
    /**
     * Map an exception to a [javax.ws.rs.core.Response]. Returning
     * `null` results in a [javax.ws.rs.core.Response.Status.NO_CONTENT]
     * response. Throwing a runtime exception results in a
     * [javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR] response.
     *
     * @param exception the exception to map to a response.
     * @return a response mapped from the supplied exception.
     */
    override fun toResponse(exception: Exception): Response {
        LOGGER.fine("Exception handled, ${exception.message}")
        return Response.serverError().entity(ExecutionResult.failed()).build()
    }

    companion object {
        private val LOGGER = Logger.getLogger(DefaultExceptionMapper::class.java.name)
    }
}

package info.cepheus.clean_architecture_quarkus.api

import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.logging.Level
import java.util.logging.Logger
import javax.ws.rs.ext.Provider
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@Provider
class DefaultCoroutineExceptionHandler : CoroutineExceptionHandler,
        AbstractCoroutineContextElement(CoroutineExceptionHandler.Key) {
    /**
     * Handles uncaught [exception] in the given [context]. It is invoked
     * if coroutine has an uncaught exception.
     */
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        LOGGER.log(Level.SEVERE, exception.message)
    }

    companion object {
        private val LOGGER = Logger.getLogger(DefaultExceptionMapper::class.java.name)
    }
}
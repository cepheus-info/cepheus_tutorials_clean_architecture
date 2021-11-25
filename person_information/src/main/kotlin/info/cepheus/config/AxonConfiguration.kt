package info.cepheus.config

import info.cepheus.axon.infrastructure.boundary.query.QueryProcessor
import info.cepheus.cepheus_axon_quarkus_auto_configuration.AxonAdapterConfiguration
import info.cepheus.cepheus_axon_quarkus_auto_configuration.AxonStartupBuilder
import io.quarkus.runtime.Startup
import org.axonframework.config.Configuration
import org.axonframework.config.Configurer
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore
import org.axonframework.eventhandling.tokenstore.jdbc.TokenSchema
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import java.sql.Connection
import java.sql.SQLException
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Typed
import javax.inject.Inject
import javax.inject.Named
import javax.sql.DataSource
import javax.validation.ValidatorFactory

@Typed
@ApplicationScoped
@Startup
class AxonConfiguration {

    @Inject
    @Named(DATABASE_BASE_SCHEMA)
    lateinit var dataSource: DataSource

    @Throws(SQLException::class)
    private fun getConnection(): Connection {
        return dataSource.connection
    }

    @Inject
    lateinit var validatorFactory: ValidatorFactory

    @Inject
    lateinit var axonStartupBuilder: AxonStartupBuilder

    @Inject
    lateinit var axonAdapterConfiguration: AxonAdapterConfiguration

    /**
     * startup
     */
    @PostConstruct
    fun startUp() {
        axonStartupBuilder.build(this.javaClass.classLoader)
                .apply { // configureEventProcessing
                    val epc = this.eventProcessing()
                    epc.registerSubscribingEventProcessor(QueryProcessor.SUBSCRIBING.name)
                    epc.registerTrackingEventProcessorConfiguration { config -> trackingEventProcessorConfig(config) }
                    epc.registerTrackingEventProcessor(QueryProcessor.TRACKING.name)
                    epc.registerTokenStore { config -> jdbcTokenStore(config) }
                    epc.assignProcessingGroup { processingGroup -> logDefaultAssignment(processingGroup) }
                }
                .buildConfiguration()
                .also { // enableBeanValidationForCommandMessage
                    it.commandBus().registerDispatchInterceptor(BeanValidationInterceptor(validatorFactory))
                }
                .also { // enableAxonAdapterConfiguration
                    axonAdapterConfiguration.enableAdapter(it)
                }
                .start()
    }

    private fun logDefaultAssignment(processingGroup: String?): String {
        LOGGER.fine { "Default assignment for processingGroup <$processingGroup>" }
        return if (processingGroup != null && processingGroup.trim { it <= ' ' }.isNotEmpty()) {
            processingGroup
        } else {
            QueryProcessor.TRACKING.name
        }
    }

    // Note Query-Side
    private fun jdbcTokenStore(config: Configuration): JdbcTokenStore {
        val schema = TokenSchema.builder()
                .setTokenTable("$DATABASE_BASE_SCHEMA.$DATABASE_TABLE_TOKEN")
                .setProcessorNameColumn(COLUMN_PROCESSOR_NAME)
                .setTokenTypeColumn(COLUMN_TOKEN_TYPE)
                .build()
        return JdbcTokenStore.builder()
                .connectionProvider { getConnection() }
                // TODO: serialize content to jsonb or bytea should be considered carefully.
                .contentType(ByteArray::class.java)
                .serializer(config.serializer())
                .schema(schema).build()
    }

    // Note Query-Side
    private fun trackingEventProcessorConfig(config: Configuration): TrackingEventProcessorConfiguration {
        // Note: CDI @RequestScoped and other scopes are not available for manually started threads.
        // Therefore, only @ApplicationScoped and @Dependent can be used for tracking event handler.
        // If any other scope is needed, have a look at "deltaspike-cdictrl-api".
        return TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
    }

    companion object {
        private val LOGGER = Logger.getLogger(AxonConfiguration::class.java.name)

        // Note: maintain all token_entries in base schema.
        private const val DATABASE_BASE_SCHEMA = "base"
        private const val DATABASE_TABLE_TOKEN = "token_entry"
        private const val COLUMN_PROCESSOR_NAME = "processor_name"
        private const val COLUMN_TOKEN_TYPE = "token_type"
    }
}


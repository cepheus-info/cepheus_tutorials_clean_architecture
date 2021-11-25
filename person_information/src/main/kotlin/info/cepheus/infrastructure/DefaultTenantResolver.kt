package info.cepheus.infrastructure

import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver
import io.vertx.ext.web.RoutingContext
import javax.enterprise.context.RequestScoped
import javax.inject.Inject

@RequestScoped
//@PersistenceUnitExtension // does not work currently
open class DefaultTenantResolver : TenantResolver {
    @Inject
    lateinit var context: RoutingContext

    override fun getDefaultTenantId(): String {
        return CurrentTenant.defaultTenantId
    }

    override fun resolveTenantId(): String {
        if (CurrentTenant.isNotEmpty()) {
            return CurrentTenant.tenantId!!
        }
        val path = context.request().path()
        return if (path.startsWith("/cd")) {
            "cd"
        } else {
            defaultTenantId
        }
    }
}
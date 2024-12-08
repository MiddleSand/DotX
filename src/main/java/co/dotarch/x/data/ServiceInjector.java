package co.dotarch.x.data;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ServiceInjector {
    public static AbstractDatabaseCore getDatabaseCore(AbstractDatabaseCore def) {
        RegisteredServiceProvider<? extends AbstractDatabaseCore> registeredServiceProvider =
                Bukkit.getServicesManager().getRegistration(AbstractDatabaseCore.class);
        if (registeredServiceProvider == null) {
            return def;
        } else {
            return registeredServiceProvider.getProvider();
        }
    }
}

package fr.customentity.investment.injection;

import com.google.inject.AbstractModule;
import fr.customentity.investment.InvestmentPlugin;

public class PluginModule extends AbstractModule {

    private InvestmentPlugin plugin;

    public PluginModule(InvestmentPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    protected void configure() {
        this.bind(InvestmentPlugin.class).toInstance(this.plugin);
    }
}

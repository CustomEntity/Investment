package fr.customentity.investment.injection;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.data.player.InvestPlayer;

public class PluginModule extends AbstractModule {

    private InvestmentPlugin plugin;

    public PluginModule(InvestmentPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    protected void configure() {
        this.bind(InvestmentPlugin.class).toInstance(this.plugin);

        install(new FactoryModuleBuilder()
                .build(InvestPlayer.InvestPlayerFactory.class));
    }
}

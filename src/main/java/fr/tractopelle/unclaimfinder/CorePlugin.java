package fr.tractopelle.unclaimfinder;

import fr.tractopelle.unclaimfinder.commands.command.UnclaimFinderCommand;
import fr.tractopelle.unclaimfinder.config.Config;
import fr.tractopelle.unclaimfinder.listeners.PlayerListener;
import fr.tractopelle.unclaimfinder.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CorePlugin extends JavaPlugin {

    private Config configuration;
    private final Logger log = new Logger(this.getDescription().getFullName());

    @Override
    public void onEnable() {

        this.init();

    }

    private void init() {

        log.info("=======================================", Logger.LogType.SUCCESS);
        log.info(" Plugin initialization in progress...", Logger.LogType.SUCCESS);
        log.info(" Author: Tractopelle#4020", Logger.LogType.SUCCESS);
        log.info("=======================================", Logger.LogType.SUCCESS);

        registerListeners();

        registerCommands();

        this.configuration = new Config(this, "config");

    }

    private void registerCommands() {

        new UnclaimFinderCommand(this);

    }

    private void registerListeners() {

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);

    }

    public Config getConfiguration() { return configuration; }

}

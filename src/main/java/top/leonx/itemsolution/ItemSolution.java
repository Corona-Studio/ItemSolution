package top.leonx.itemsolution;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class ItemSolution extends JavaPlugin {
    private static ItemSolution instance;
    private static ItemSolutionEventHandler itemSolutionEventHandler;
    public static ConfigManager config = new ConfigManager();
    public static Logger logger;
    FileConfiguration fileConfig = getConfig();
    public static ItemSolution getInstance() {
        return instance;
    }

    public static ItemSolutionEventHandler getClearItemEventHandler() {
        return itemSolutionEventHandler;
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        ConfigManager.addDefaults(fileConfig);
        fileConfig.options().copyDefaults(true);
        saveConfig();

        itemSolutionEventHandler = new ItemSolutionEventHandler();
        getServer().getPluginManager().registerEvents(itemSolutionEventHandler, this);
        Objects.requireNonNull(getCommand("itemsolution")).setExecutor(new ItemSolutionCommand());
        refreshConfig();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void refreshConfig(){
        fileConfig = getConfig();
        config.readFromFileConfig(fileConfig);
    }
}

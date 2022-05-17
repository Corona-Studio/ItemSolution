package top.leonx.itemsolution;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class ItemSolution extends JavaPlugin {
    private static ItemSolution instance;
    private static ItemSolutionEventHandler itemSolutionEventHandler;
    public static ItemSolutionConfig config = new ItemSolutionConfig();
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
        fileConfig.addDefault("clear-interval", 60);
        fileConfig.addDefault("item-age-lowerLimit", 120);
        fileConfig.addDefault("clear-item-not-on-ground", false);
        fileConfig.addDefault("clear-item-with-custom-name", false);
        fileConfig.addDefault("world-blacklist", new ArrayList<String>());
        fileConfig.addDefault("use-whitelist", false);
        fileConfig.addDefault("whitelist", new ArrayList<String>());
        fileConfig.addDefault("use-blacklist", false);
        fileConfig.addDefault("blacklist", new ArrayList<String>());
        fileConfig.addDefault("send-message-to-player", true);
        fileConfig.addDefault("message-when-start", "§6[ClearItem] §r§7Start clean items in %s.");
        fileConfig.addDefault("message-when-finish", "§6[ClearItem] §r§7%d items has been cleaned.");
        fileConfig.addDefault("send-count-down", true);
        fileConfig.addDefault("count-down.first.message", "Dropped items will be cleaned after %d seconds.");
        fileConfig.addDefault("count-down.first.interval", 5);
        fileConfig.addDefault("count-down.first.start-time", 30);
        fileConfig.addDefault("count-down.first.end-time", 10);
        fileConfig.addDefault("count-down.second.message", "Dropped items will be cleaned after %d seconds!!!");
        fileConfig.addDefault("count-down.second.interval", 2);
        fileConfig.addDefault("count-down.second.start-time", 10);
        fileConfig.addDefault("count-down.second.end-time", 2);
        fileConfig.addDefault("use-world-override", false);
        fileConfig.addDefault("world-override-setting", new ArrayList<String>());
        fileConfig.options().copyDefaults(true);
        saveConfig();

        itemSolutionEventHandler = new ItemSolutionEventHandler();
        getServer().getPluginManager().registerEvents(itemSolutionEventHandler, this);
        Objects.requireNonNull(getCommand("clearitem")).setExecutor(new ItemSolutionCommand());
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

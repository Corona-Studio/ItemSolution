package top.leonx.itemsolution;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class ConfigManager {
    public static Setting globalSetting;
    public static HashMap<String, Setting> worldOverrides = new HashMap<>();
    public HashSet<String> worldBlacklist = new HashSet<>();
    boolean useWorldOverride = false;

    public Setting getSetting(String worldName) {
        if (useWorldOverride && worldOverrides.containsKey(worldName)) return worldOverrides.get(worldName);
        return globalSetting;
    }

    public static void addDefaults(FileConfiguration fileConfig) {
        fileConfig.addDefault("global.check-interval", 60);
        fileConfig.addDefault("global.item-age-lower-limit", 60);
        fileConfig.addDefault("global.item-amount-trigger", 120);
        fileConfig.addDefault("global.sort-by-age", false);
        fileConfig.addDefault("global.clear-item-not-on-ground", false);
        fileConfig.addDefault("global.clear-item-with-custom-name", false);
        fileConfig.addDefault("global.world-blacklist", new ArrayList<String>());
        fileConfig.addDefault("global.use-whitelist", false);
        fileConfig.addDefault("global.whitelist", new ArrayList<String>());
        fileConfig.addDefault("global.use-blacklist", false);
        fileConfig.addDefault("global.blacklist", new ArrayList<String>());
        fileConfig.addDefault("global.send-message-to-player", true);
        fileConfig.addDefault("global.message-when-start", "[ItemSolution] Start clean items in %world%.");
        fileConfig.addDefault("global.message-when-finish", "[ItemSolution] %count% items has been cleaned.");
        fileConfig.addDefault("global.send-count-down", true);
        fileConfig.addDefault("global.count-down.first.message",
                              "[ItemSolution] Dropped items will be cleaned after %time% " + "seconds.");
        fileConfig.addDefault("global.count-down.first.interval", 5);
        fileConfig.addDefault("global.count-down.first.start-time", 30);
        fileConfig.addDefault("global.count-down.first.end-time", 10);
        fileConfig.addDefault("global.count-down.second.message",
                              "[ItemSolution] Dropped items will be cleaned after %time% " + "seconds!!!");
        fileConfig.addDefault("global.count-down.second.interval", 2);
        fileConfig.addDefault("global.count-down.second.start-time", 10);
        fileConfig.addDefault("global.count-down.second.end-time", 2);
        fileConfig.addDefault("use-world-override", false);
        fileConfig.addDefault("world-overrides.world.clear-interval", 60);
        fileConfig.addDefault("world-overrides.world.item-age-lower-limit", 120);
        fileConfig.addDefault("world-overrides.world.clear-item-not-on-ground", false);
        fileConfig.addDefault("world-overrides.world.clear-item-with-custom-name", false);
        fileConfig.addDefault("world-overrides.world.use-whitelist", false);
        fileConfig.addDefault("world-overrides.world.whitelist", new ArrayList<String>());
        fileConfig.addDefault("world-overrides.world.use-blacklist", false);
        fileConfig.addDefault("world-overrides.world.blacklist", new ArrayList<String>());
        fileConfig.addDefault("world-overrides.world.send-message-to-player", true);
        fileConfig.addDefault("world-overrides.world.message-when-start",
                              "[ItemSolution] Start clean items in %world%.");
        fileConfig.addDefault("world-overrides.world.message-when-finish",
                              "[ItemSolution] %count% items has been cleaned.");
        fileConfig.addDefault("world-overrides.world.send-count-down", true);
        fileConfig.addDefault("world-overrides.world.count-down.first.message",
                              "[ItemSolution] Dropped items will be cleaned after" + " %time% seconds.");
        fileConfig.addDefault("world-overrides.world.count-down.first.interval", 5);
        fileConfig.addDefault("world-overrides.world.count-down.first.start-time", 30);
        fileConfig.addDefault("world-overrides.world.count-down.first.end-time", 10);
        fileConfig.addDefault("world-overrides.world.count-down.second.message",
                              "[ItemSolution] Dropped items will be cleaned " + "after %time% seconds!!!");
        fileConfig.addDefault("world-overrides.world.count-down.second.interval", 2);
        fileConfig.addDefault("world-overrides.world.count-down.second.start-time", 10);
        fileConfig.addDefault("world-overrides.world.count-down.second.end-time", 2);

        var commits = new ArrayList<String>();
        commits.add("The default setting for all worlds");
        commits.add("");
        commits.add("The interval between two cleanups, in seconds");
        fileConfig.setComments("cleanup-interval", commits);
        commits = new ArrayList<>();
        commits.add("The item entity age bigger than this value will be not cleaned, in seconds");
        fileConfig.setComments("item-age-lower-limit", commits);
        commits = new ArrayList<>();
        commits.add("If true, the item entity will be cleaned when it is not on the ground");
        fileConfig.setComments("clear-item-not-on-ground", commits);
        commits = new ArrayList<>();
        commits.add("If true, the item entity will be cleaned when it has a custom name");
        fileConfig.setComments("clear-item-with-custom-name", commits);
        commits = new ArrayList<>();
        commits.add("The worlds in this list will not be cleaned");
        fileConfig.setComments("world-blacklist", commits);
        commits = new ArrayList<>();
        commits.add("If true, the items in whitelist will not be cleaned");
        fileConfig.setComments("use-whitelist", commits);
        commits = new ArrayList<>();
        commits.add("If true, only the items in blacklist will be cleaned");
        fileConfig.setComments("use-blacklist", commits);
        commits = new ArrayList<>();
        commits.add("If true, the plugin will send a message to player when start to clean items");
        fileConfig.setComments("send-message-to-player", commits);
        commits = new ArrayList<>();
        commits.add("The message will be sent to player when start to clean items");
        fileConfig.setComments("message-when-start", commits);
        commits = new ArrayList<>();
        commits.add("The message will be sent to player when finish to clean items");
        fileConfig.setComments("message-when-finish", commits);
        commits = new ArrayList<>();
        commits.add("If true, the plugin will send a countdown message to player before start to clean items");
        fileConfig.setComments("send-count-down", commits);
        commits = new ArrayList<>();
        commits.add("The first countdown message will be sent to player");
        fileConfig.setComments("count-down.first.message", commits);
        commits = new ArrayList<>();
        commits.add("The interval between two countdown messages");
        fileConfig.setComments("count-down.first.interval", commits);
        commits = new ArrayList<>();
        commits.add("The second countdown message will be sent to player");
        fileConfig.setComments("count-down.second.message", commits);
        commits = new ArrayList<>();
        commits.add("The interval between two countdown messages");
        fileConfig.setComments("count-down.second.interval", commits);
        commits = new ArrayList<>();
        commits.add("How many seconds in advance to start sending countdown messages");
        fileConfig.setComments("count-down.first.start-time", commits);
        commits = new ArrayList<>();
        commits.add("How many seconds in advance to stop sending countdown messages");
        fileConfig.setComments("count-down.first.end-time", commits);

        commits = new ArrayList<>();
        commits.add("If true, the settings in world-override will be used");
        commits.add("The settings in world-override will override the settings in global settings");
        fileConfig.setComments("use-world-override", commits);

        fileConfig.options().parseComments(true);
    }

    public void readFromFileConfig(FileConfiguration config) {
        globalSetting = new Setting();
        globalSetting.whiteListMaterials.clear();
        globalSetting.blackListMaterials.clear();

        globalSetting.checkInterval = config.getInt("global.check-interval", 60);
        globalSetting.itemAgeLowerLimit = config.getInt("global.item-age-lower-limit", 120);
        globalSetting.itemAmountTrigger = config.getInt("global.item-amount-trigger", 100);
        globalSetting.clearItemNotOnGround = config.getBoolean("global.clear-item-not-on-ground", false);
        globalSetting.clearItemWithCustomName = config.getBoolean("global.clear-item-with-custom-name", false);
        globalSetting.useWhitelist = config.getBoolean("global.use-whitelist", false);
        var whitelist = config.getStringList("global.whitelist");
        globalSetting.useBlacklist = config.getBoolean("global.use-blacklist", false);
        var blacklist = config.getStringList("global.blacklist");

        globalSetting.whiteListMaterials = new HashSet<>(
                whitelist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());
        globalSetting.blackListMaterials = new HashSet<>(
                blacklist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());

        globalSetting.sendMessageToPlayer = config.getBoolean("global.send-message-to-player", false);
        globalSetting.messageWhenStart = config.getString("global.message-when-start",
                                                          "[ItemSolution] Start cleaning up the dropped item in %world%!");
        globalSetting.messageWhenFinish = config.getString("global.message-when-finish",
                                                           "[ItemSolution] %count% items has been cleared!");
        globalSetting.sendCountDown = config.getBoolean("global.send-count-down", false);

        globalSetting.cdFirstMessage = config.getString("global.count-down.first.message",
                                                        "[ItemSolution] Dropped items will be cleaned after %time% seconds!");
        globalSetting.cdSecondMessage = config.getString("global.count-down.second.message",
                                                         "[ItemSolution] Dropped items will be cleaned after %time% seconds!!!");
        globalSetting.cdFirstInterval = config.getInt("global.count-down.first.interval", 5);
        globalSetting.cdSecondInterval = config.getInt("global.count-down.second.interval", 2);
        globalSetting.cdFirstStartTime = config.getInt("global.count-down.first.start-time", 30);
        globalSetting.cdSecondStartTime = config.getInt("global.count-down.second.start-time", 8);
        globalSetting.cdFirstEndTime = config.getInt("global.count-down.first.end-time", 10);
        globalSetting.cdSecondEndTime = config.getInt("global.count-down.second.end-time", 2);
        worldBlacklist = new HashSet<>(config.getStringList("global.world-blacklist"));

        useWorldOverride = config.getBoolean("use-world-override", false);
        worldOverrides.clear();
        Objects.requireNonNull(config.getConfigurationSection("world-overrides")).getKeys(false).forEach(worldName -> {
            worldOverrides.put(worldName, readWorldOverride(config, worldName));
            ItemSolution.logger.info("Loaded world override for world " + worldName);
        });
    }

    Setting readWorldOverride(FileConfiguration config, String worldName) {
        Setting setting = new Setting();
        setting.whiteListMaterials.clear();
        setting.blackListMaterials.clear();
        String prefix = "world-overrides." + worldName + ".";
        setting.checkInterval = config.getInt(prefix + "check-interval", globalSetting.checkInterval);
        setting.itemAgeLowerLimit = config.getInt(prefix + "item-age-lower-limit", globalSetting.itemAgeLowerLimit);
        setting.itemAmountTrigger = config.getInt(prefix + "item-amount-trigger", globalSetting.itemAmountTrigger);
        setting.clearItemNotOnGround = config.getBoolean(prefix + "clear-item-not-on-ground",
                                                         globalSetting.clearItemNotOnGround);
        setting.clearItemWithCustomName = config.getBoolean(prefix + "clear-item-with-custom-name",
                                                            globalSetting.clearItemWithCustomName);
        setting.useWhitelist = config.getBoolean(prefix + "use-whitelist", globalSetting.useWhitelist);
        var whitelist = config.getStringList(prefix + "whitelist");
        setting.useBlacklist = config.getBoolean(prefix + "use-blacklist", globalSetting.useBlacklist);
        var blacklist = config.getStringList(prefix + "blacklist");

        setting.whiteListMaterials = new HashSet<>(
                whitelist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());
        setting.blackListMaterials = new HashSet<>(
                blacklist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());

        setting.sendMessageToPlayer = config.getBoolean(prefix + "send-message-to-player",
                                                        globalSetting.sendMessageToPlayer);
        setting.messageWhenStart = config.getString(prefix + "message-when-start", globalSetting.messageWhenStart);
        setting.messageWhenFinish = config.getString(prefix + "message-when-finish", globalSetting.messageWhenFinish);
        setting.sendCountDown = config.getBoolean(prefix + "send-count-down", globalSetting.sendCountDown);

        setting.cdFirstMessage = config.getString(prefix + "count-down.first.message", globalSetting.cdFirstMessage);
        setting.cdSecondMessage = config.getString(prefix + "count-down.second.message", globalSetting.cdSecondMessage);
        setting.cdFirstInterval = config.getInt(prefix + "count-down.first.interval", globalSetting.cdFirstInterval);
        setting.cdSecondInterval = config.getInt(prefix + "count-down.second.interval", globalSetting.cdSecondInterval);
        setting.cdFirstStartTime = config.getInt(prefix + "count-down.first.start-time",
                                                 globalSetting.cdFirstStartTime);
        setting.cdSecondStartTime = config.getInt(prefix + "count-down.second.start-time",
                                                  globalSetting.cdSecondStartTime);
        setting.cdFirstEndTime = config.getInt(prefix + "count-down.first.end-time", globalSetting.cdFirstEndTime);
        setting.cdSecondEndTime = config.getInt(prefix + "count-down.second.end-time", globalSetting.cdSecondEndTime);
        return setting;
    }

    public static class Setting {
        public int checkInterval;
        public int itemAgeLowerLimit;
        public int itemAmountTrigger;
        public boolean clearItemNotOnGround;
        public boolean clearItemWithCustomName;
        public boolean useWhitelist;
        public boolean useBlacklist;

        public boolean sendMessageToPlayer;
        public String messageWhenStart;
        public String messageWhenFinish;
        public boolean sendCountDown;
        public String cdFirstMessage;
        public String cdSecondMessage;
        public int cdFirstInterval;
        public int cdSecondInterval;
        public int cdFirstStartTime;
        public int cdSecondStartTime;
        public int cdFirstEndTime;
        public int cdSecondEndTime;

        public HashSet<Material> whiteListMaterials = new HashSet<>();
        public HashSet<Material> blackListMaterials = new HashSet<>();
    }
}

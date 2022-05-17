package top.leonx.itemsolution;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class ItemSolutionConfig {
    public static Setting globalSetting;
    public static HashMap<String,Setting> worldOverrides = new HashMap<>();
    public HashSet<String> worldBlacklist = new HashSet<>();
    boolean useWorldOverride = false;
    public Setting getSetting(String worldName){
        if(useWorldOverride && worldOverrides.containsKey(worldName))
            return worldOverrides.get(worldName);
        return globalSetting;
    }
    public void readFromFileConfig(FileConfiguration config){
        globalSetting = new Setting();
        globalSetting.whiteListMaterials.clear();
        globalSetting.blackListMaterials.clear();

        globalSetting.clearInterval = config.getInt("clear-interval", 60);
        globalSetting.itemAgeLowerLimit = config.getInt("item-age-lower-limit", 120);
        globalSetting.clearItemNotOnGround = config.getBoolean("clear-item-not-on-ground", false);
        globalSetting.clearItemWithCustomName = config.getBoolean("clear-item-with-custom-name", false);
        globalSetting.useWhitelist = config.getBoolean("use-whitelist", false);
        var whitelist = config.getStringList("whitelist");
        globalSetting.useBlacklist = config.getBoolean("use-blacklist", false);
        var blacklist = config.getStringList("blacklist");

        globalSetting.whiteListMaterials= new HashSet<>(whitelist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());
        globalSetting.blackListMaterials= new HashSet<>(blacklist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());

        globalSetting.sendMessageToPlayer = config.getBoolean("send-message-to-player", false);
        globalSetting.messageWhenStart = config.getString("message-when-start", "Start cleaning up the dropped item in %world%!");
        globalSetting.messageWhenFinish = config.getString("message-when-finish", "%count% items has been cleared!");
        globalSetting.sendCountDown = config.getBoolean("send-count-down", false);

        globalSetting.cdFirstMessage = config.getString("count-down.first.message", "Dropped items will be cleaned after %time% seconds!");
        globalSetting.cdSecondMessage = config.getString("count-down.second.message", "Dropped items will be cleaned after %time% seconds!!!");
        globalSetting.cdFirstInterval = config.getInt("count-down.first.interval", 5);
        globalSetting.cdSecondInterval = config.getInt("count-down.second.interval", 2);
        globalSetting.cdFirstStartTime = config.getInt("count-down.first.start-time", 30);
        globalSetting.cdSecondStartTime = config.getInt("count-down.second.start-time", 8);
        globalSetting.cdFirstEndTime = config.getInt("count-down.first.end-time", 10);
        globalSetting.cdSecondEndTime = config.getInt("count-down.second.end-time", 2);
        worldBlacklist=new HashSet<>(config.getStringList("world-blacklist"));

        useWorldOverride = config.getBoolean("use-world-override", false);
        var worldOverridesNames = new HashSet<>(config.getStringList("world-overrides"));
        worldOverrides.clear();
        for (String name : worldOverridesNames) {
            worldOverrides.put(name,readWorldOverride(config,name));
        }
    }

    Setting readWorldOverride(FileConfiguration config,String worldName){
        Setting setting=new Setting();
        setting.whiteListMaterials.clear();
        setting.blackListMaterials.clear();
        String prefix="world-overrides."+worldName+".";
        setting.clearInterval = config.getInt(prefix+"clear-interval", globalSetting.clearInterval);
        setting.itemAgeLowerLimit = config.getInt(prefix+"item-age-lower-limit", globalSetting.itemAgeLowerLimit);
        setting.clearItemNotOnGround = config.getBoolean(prefix+"clear-item-not-on-ground", globalSetting.clearItemNotOnGround);
        setting.clearItemWithCustomName = config.getBoolean(prefix+"clear-item-with-custom-name", globalSetting.clearItemWithCustomName);
        setting.useWhitelist = config.getBoolean(prefix+"use-whitelist", globalSetting.useWhitelist);
        var whitelist = config.getStringList(prefix+"whitelist");
        setting.useBlacklist = config.getBoolean(prefix+"use-blacklist", globalSetting.useBlacklist);
        var blacklist = config.getStringList(prefix+"blacklist");

        setting.whiteListMaterials= new HashSet<>(whitelist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());
        setting.blackListMaterials= new HashSet<>(blacklist.stream().map(Material::getMaterial).filter(Objects::nonNull).toList());

        setting.sendMessageToPlayer = config.getBoolean(prefix+"send-message-to-player", globalSetting.sendMessageToPlayer);
        setting.messageWhenStart = config.getString(prefix+"message-when-start", globalSetting.messageWhenStart);
        setting.messageWhenFinish = config.getString(prefix+"message-when-finish", globalSetting.messageWhenFinish);
        setting.sendCountDown = config.getBoolean(prefix+"send-count-down", globalSetting.sendCountDown);

        setting.cdFirstMessage = config.getString(prefix+"count-down.first.message", globalSetting.cdFirstMessage);
        setting.cdSecondMessage = config.getString(prefix+"count-down.second.message", globalSetting.cdSecondMessage);
        setting.cdFirstInterval = config.getInt(prefix+"count-down.first.interval", globalSetting.cdFirstInterval);
        setting.cdSecondInterval = config.getInt(prefix+"count-down.second.interval", globalSetting.cdSecondInterval);
        setting.cdFirstStartTime = config.getInt(prefix+"count-down.first.start-time", globalSetting.cdFirstStartTime);
        setting.cdSecondStartTime = config.getInt(prefix+"count-down.second.start-time", globalSetting.cdSecondStartTime);
        setting.cdFirstEndTime = config.getInt(prefix+"count-down.first.end-time", globalSetting.cdFirstEndTime);
        setting.cdSecondEndTime = config.getInt(prefix+"count-down.second.end-time", globalSetting.cdSecondEndTime);
        return setting;
    }

    public static class Setting{
        public int clearInterval;
        public int itemAgeLowerLimit;
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

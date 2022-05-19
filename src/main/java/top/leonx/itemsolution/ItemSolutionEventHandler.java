package top.leonx.itemsolution;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@SuppressWarnings("unused")
public class ItemSolutionEventHandler implements Listener {
    private final HashMap<World, BukkitTask> worldCheckTasks = new HashMap<>();
    private final HashMap<World, BukkitTask> itemClearTasks = new HashMap<>();
    @EventHandler
    public void onServerLoaded(ServerLoadEvent event){
        var world= Bukkit.getServer().getWorld("world");
        var world_nether= Bukkit.getServer().getWorld("world_nether");
        var world_the_end= Bukkit.getServer().getWorld("world_the_end");

        if(worldCheckTasks.containsKey(world))
            worldCheckTasks.get(world).cancel();
        if(worldCheckTasks.containsKey(world_nether))
            worldCheckTasks.get(world_nether).cancel();
        if(worldCheckTasks.containsKey(world_the_end))
            worldCheckTasks.get(world_the_end).cancel();

        StartCheckTask(world).ifPresent(bukkitTask -> worldCheckTasks.put(world, bukkitTask));
        StartCheckTask(world_nether).ifPresent(bukkitTask -> worldCheckTasks.put(world_nether, bukkitTask));
        StartCheckTask(world_the_end).ifPresent(bukkitTask -> worldCheckTasks.put(world_the_end, bukkitTask));
    }
    @EventHandler
    public void onWorldLoaded(WorldLoadEvent event) {
        World world = event.getWorld();
        // Scan every 1 minute
        var task = StartCheckTask(world);
        if(task.isPresent()) {
            if (worldCheckTasks.containsKey(world)) {
                worldCheckTasks.get(world).cancel();
            }
            worldCheckTasks.put(world, task.get());
        }
    }

    @EventHandler
    public void onWorldUnloaded(WorldUnloadEvent event) {
        World world = event.getWorld();
        if(worldCheckTasks.containsKey(world)){
            worldCheckTasks.get(world).cancel();
            worldCheckTasks.remove(world);
        }
    }

    private Optional<BukkitTask> StartCheckTask(World world){
        if(world==null){
            return Optional.empty();
        }
        if (ItemSolution.config.worldBlacklist.contains(world.getName())) {
            return Optional.empty();
        }
        var settings = ItemSolution.config.getSetting(world.getName());
        BukkitScheduler scheduler = Bukkit.getScheduler();
        ItemSolution.getInstance().getLogger().info("Starting clear task for world: " + world.getName());
        return Optional.of(scheduler.runTaskTimer(ItemSolution.getInstance(), new CheckItemRunnable(world),
                settings.checkInterval*20L, settings.checkInterval*20L));
    }
    public static boolean CanBeCleaned(Item item,ConfigManager.Setting setting){
        if(item.getTicksLived() < setting.itemAgeLowerLimit*20L)
            return false;

        // Does not clean up items that are not on the ground
        if (!setting.clearItemNotOnGround && !item.isOnGround())
            return false;

        // Use a blacklist, and it's not in the blacklist, skip it
        if(setting.useBlacklist && !setting.blackListMaterials.contains(item.getItemStack().getType())){
            return false;
        }

        // Use whitelist, and it's in the whitelist, skip it
        if(setting.useWhitelist && setting.whiteListMaterials.contains(item.getItemStack().getType())){
            return false;
        }

        if(!setting.clearItemWithCustomName
                && item.getItemStack().hasItemMeta()
                && Objects.requireNonNull(item.getItemStack().getItemMeta()).hasDisplayName()){
            return false;
        }

        return true;
    }
    public static void DoClearWorldItem(World world){
        AtomicLong count = new AtomicLong();
        final ConfigManager.Setting setting = ItemSolution.config.getSetting(world.getName());
        final var startMessage = ChatColor.RED + setting.messageWhenStart.replaceAll("%world%", world.getName());
        world.getPlayers().forEach(player -> player.sendMessage(startMessage));

        world.getEntities().forEach(entity -> {
            if (entity instanceof Item item) {
                if(CanBeCleaned(item,setting)){
                    item.remove();
                    ItemSolution.logger.finest(String.format("Item %s removed", item.getItemStack().getType()));
                    count.getAndIncrement();
                }
            }
        });
        ItemSolution.getInstance().getLogger().info(String.format("%d dropped items removed in world %s", count.get(), world.getName()));
        var finishMessage = ChatColor.RED +
                setting.messageWhenFinish.replaceAll("%count%",String.valueOf(count.get()));
        world.getPlayers().forEach(player -> player.sendMessage(finishMessage));
    }

    /**
     * Start the clear task, and save the task to the map
     * The task will count down the time, and when the time is up, start cleaning
     * The task will be cancelled and remove from the map when the task is finished
     * @param world The world to be cleaned
     */
    public void StartClearTask(World world){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        var task = scheduler.runTaskTimer(ItemSolution.getInstance(), new ClearItemRunnable(world),
                                          0L, 20L);

        itemClearTasks.put(world, task);
    }

    private final class CheckItemRunnable implements Runnable {
        private final World world;

        CheckItemRunnable(World world) {
            this.world = world;
        }

        @Override
        public void run() {
            var setting = ItemSolution.config.getSetting(world.getName());
            // Count the number of items that can be cleaned in the world
            long count = world.getEntities().stream().filter(entity -> entity instanceof Item && CanBeCleaned((Item) entity,setting)).count();
            ItemSolution.logger.info(String.format("%d dropped items can be cleaned in world %s", count, world.getName()));
            // If there are enough items that can be cleaned, start the clean task
            if(count > setting.itemAmountTrigger){
                if (itemClearTasks.containsKey(world)) { // If the task is already running, skip
                    return;
                }
                ItemSolution.logger.info(String.format("Starting clean task for world %s", world.getName()));
                StartClearTask(world);
            }
        }
    }
    private final class ClearItemRunnable implements Runnable {
        private final World world;

        private ClearItemRunnable(World world) {
            this.world = world;
        }

        private int timeSinceRun = 0;
        @Override
        public void run() {
            timeSinceRun +=1;
            ConfigManager.Setting setting = ItemSolution.config.getSetting(world.getName());

            // Compute the time left
            // Because there are two count down messages, we need to get the earlier one's start time to compute the time left
            int remaining = Math.max(setting.cdFirstStartTime,setting.cdFirstEndTime) - timeSinceRun;

            ItemSolution.logger.finest("Running ClearItem task for world: %d seconds".formatted(remaining));
            int cdFirstStart = setting.cdFirstStartTime;
            int cdFirstEnd = setting.cdFirstEndTime;
            int cdFirstInterval = setting.cdFirstInterval;

            // Within the time of the first countdown message, send the first count-down message
            if(remaining <= cdFirstStart && remaining >= cdFirstEnd
                &&(remaining - cdFirstEnd)% cdFirstInterval == 0){
                var message = ChatColor.RED + setting.cdFirstMessage.replaceAll("%time%", String.valueOf(remaining));
                world.getPlayers().forEach(player -> player.sendMessage(message));
            }

            // Within the time of the second countdown message, send the second count-down message
            int cdSecondStart = setting.cdSecondStartTime;
            int cdSecondEnd = setting.cdSecondEndTime;
            int cdSecondInterval = setting.cdSecondInterval;
            if(remaining <= cdSecondStart && remaining >= cdSecondEnd
                    &&(remaining - cdSecondEnd) % cdSecondInterval == 0){
                var message = ChatColor.RED + setting.cdSecondMessage.replaceAll("%time%", String.valueOf(remaining));
                world.getPlayers().forEach(player -> player.sendMessage(message));
            }

            if(remaining<= 0){
                // If the time is up, do the clean
                DoClearWorldItem(world);
                BukkitScheduler scheduler = Bukkit.getScheduler();
                itemClearTasks.get(world).cancel(); // cancel self
                itemClearTasks.remove(world); // remove from map
            }
        }
    }
}

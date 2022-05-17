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
    private final HashMap<World, BukkitTask> worldClearTasks = new HashMap<>();

    @EventHandler
    public void onServerLoaded(ServerLoadEvent event){
        var world= Bukkit.getServer().getWorld("world");
        var world_nether= Bukkit.getServer().getWorld("world_nether");
        var world_the_end= Bukkit.getServer().getWorld("world_the_end");

        if(worldClearTasks.containsKey(world))
            worldClearTasks.get(world).cancel();
        if(worldClearTasks.containsKey(world_nether))
            worldClearTasks.get(world_nether).cancel();
        if(worldClearTasks.containsKey(world_the_end))
            worldClearTasks.get(world_the_end).cancel();

        StartClearTask(world).ifPresent(bukkitTask -> worldClearTasks.put(world, bukkitTask));
        StartClearTask(world_nether).ifPresent(bukkitTask -> worldClearTasks.put(world_nether, bukkitTask));
        StartClearTask(world_the_end).ifPresent(bukkitTask -> worldClearTasks.put(world_the_end, bukkitTask));
    }
    @EventHandler
    public void onWorldLoaded(WorldLoadEvent event) {
        World world = event.getWorld();
        // Scan every 1 minute
        var task = StartClearTask(world);
        if(task.isPresent()) {
            if (worldClearTasks.containsKey(world)) {
                worldClearTasks.get(world).cancel();
            }
            worldClearTasks.put(world, task.get());
        }
    }

    @EventHandler
    public void onWorldUnloaded(WorldUnloadEvent event) {
        World world = event.getWorld();
        if(worldClearTasks.containsKey(world)){
            worldClearTasks.get(world).cancel();
            worldClearTasks.remove(world);
        }
    }

    private Optional<BukkitTask> StartClearTask(World world){
        if(world==null){
            return Optional.empty();
        }
        if (ItemSolution.config.worldBlacklist.contains(world.getName())) {
            return Optional.empty();
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        ItemSolution.getInstance().getLogger().info("Starting clear task for world: " + world.getName());
        return Optional.of(scheduler.runTaskTimer(ItemSolution.getInstance(), new ClearItemRunnable(world), 20L, 20L));
    }

    public static void DoClearWorldItem(World world){
        ItemSolution.getInstance().getLogger().info("Scanning item entity in world: " + world.getName());
        AtomicLong count = new AtomicLong();
        final ItemSolutionConfig.Setting setting = ItemSolution.config.getSetting(world.getName());
        final var startMessage = ChatColor.RED + setting.messageWhenStart.replaceAll("%world%", world.getName());
        world.getPlayers().forEach(player -> player.sendMessage(startMessage));

        world.getEntities().forEach(entity -> {
            if (entity instanceof Item item) {
                if(item.getTicksLived() < setting.itemAgeLowerLimit*20L)
                    return;

                // Does not clean up items that are not on the ground
                if (!setting.clearItemNotOnGround && !item.isOnGround())
                    return;

                // Use a blacklist and it's in the blacklist
                if(setting.useBlacklist && !setting.blackListMaterials.contains(item.getItemStack().getType())){
                    return;
                }

                // Use whitelist and it's not in the whitelist
                if(setting.useWhitelist && setting.whiteListMaterials.contains(item.getItemStack().getType())){
                    return;
                }

                if(!setting.clearItemWithCustomName
                        && item.getItemStack().hasItemMeta()
                        && Objects.requireNonNull(item.getItemStack().getItemMeta()).hasDisplayName()){
                    return;
                }

                item.remove();
                ItemSolution.getInstance().getLogger().finest(String.format("Item %s removed", item.getItemStack().getType()));
                count.getAndIncrement();
            }
        });
        ItemSolution.getInstance().getLogger().info(String.format("%d items removed in world %s", count.get(), world.getName()));
        var finishMessage = ChatColor.RED +
                setting.messageWhenFinish.replaceAll("%count%",String.valueOf(count.get()));
        world.getPlayers().forEach(player -> player.sendMessage(finishMessage));
    }

    private static final class ClearItemRunnable implements Runnable {
        private final World world;

        private ClearItemRunnable(World world) {
            this.world = world;
        }

        private int sinceLastRun = 0;
        @Override
        public void run() {
            sinceLastRun +=1;
            ItemSolutionConfig.Setting setting = ItemSolution.config.getSetting(world.getName());
            int remaining = setting.clearInterval - sinceLastRun;
            ItemSolution.logger.finest("Running ClearItem task for world: %d seconds".formatted(remaining));
            int cdFirstStart = setting.cdFirstStartTime;
            int cdFirstEnd = setting.cdFirstEndTime;
            int cdFirstInterval = setting.cdFirstInterval;
            if(remaining <= cdFirstStart && remaining >= cdFirstEnd
                &&(remaining - cdFirstEnd)% cdFirstInterval == 0){
                var message = ChatColor.RED + setting.cdFirstMessage.replaceAll("%time%", String.valueOf(remaining));
                world.getPlayers().forEach(player -> player.sendMessage(message));
            }

            int cdSecondStart = setting.cdSecondStartTime;
            int cdSecondEnd = setting.cdSecondEndTime;
            int cdSecondInterval = setting.cdSecondInterval;
            if(remaining <= cdSecondStart && remaining >= cdSecondEnd
                    &&(remaining - cdSecondEnd) % cdSecondInterval == 0){
                var message = ChatColor.RED + setting.cdSecondMessage.replaceAll("%time%", String.valueOf(remaining));
                world.getPlayers().forEach(player -> player.sendMessage(message));
            }

            if(remaining<= 0){
                DoClearWorldItem(world);
                sinceLastRun=0;
            }
        }
    }
}

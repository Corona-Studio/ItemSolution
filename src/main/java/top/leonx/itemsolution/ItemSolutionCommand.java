package top.leonx.itemsolution;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemSolutionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==1){
            if(args[0].equals("reload")){
                ItemSolution.getInstance().reloadConfig();
                ItemSolution.getInstance().refreshConfig();
                sender.sendMessage("Config reloaded");
                return true;
            }
            if(args[0].equals("clear") && sender instanceof Player player && sender.hasPermission("clearitem.clear")){
                World world = player.getWorld();
                ItemSolutionEventHandler.DoClearWorldItem(world);
                sender.sendMessage("Cleared");
            }
        }
        return false;
    }
}

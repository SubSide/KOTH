package subside.plugins.koth.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.KothHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.Loot;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandReload implements AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        for(Player player : Bukkit.getOnlinePlayers()){
            String title = player.getOpenInventory().getTitle();
            for(Loot loot : KothHandler.getInstance().getLoots()){
                if(loot.getInventory().getTitle().equalsIgnoreCase(title)){
                    player.closeInventory();
                }
            }
            
//            for(Koth koth : KothHandler.getInstance().getAvailableKoths()){
//                if(Loot.getKothLootTitle(koth.getName()).equalsIgnoreCase(title)){
//                    player.closeInventory();
//                }
//            }
        }
        
        KothHandler.getInstance().stopAllKoths();
        
        Bukkit.getScheduler().runTaskLater(KothPlugin.getPlugin(), new Runnable(){
            @Override
            public void run() {
                KothPlugin.getPlugin().init();
            }
        }, 1);
        throw new CommandMessageException(Lang.COMMAND_RELOAD_RELOAD);
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.RELOAD;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"reload"};
    }

}

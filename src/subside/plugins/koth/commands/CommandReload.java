package subside.plugins.koth.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandReload implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        for(Player player : Bukkit.getOnlinePlayers()){
            String title = player.getOpenInventory().getTitle();
            for(Loot loot : KothHandler.getLoots()){
                if(loot.getInventory().getTitle().equalsIgnoreCase(title)){
                    player.closeInventory();
                }
            }
            
            for(Koth koth : KothHandler.getAvailableKoths()){
                if(Loot.getKothLootTitle(koth.getName()).equalsIgnoreCase(title)){
                    player.closeInventory();
                }
            }
        }
        
        
        KothPlugin.getPlugin().init();
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

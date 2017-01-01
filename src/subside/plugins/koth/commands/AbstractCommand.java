package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import subside.plugins.koth.utils.IPerm;

public abstract class AbstractCommand {
    protected JavaPlugin plugin;
    
    public AbstractCommand(JavaPlugin plugin){
        this.plugin = plugin;
    }
    
    public abstract void run(CommandSender sender, String[] args);

    public abstract IPerm getPermission();
    public abstract String[] getCommands();
    
    public abstract String getUsage();
    public abstract String getDescription();
}
package subside.plugins.koth.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.utils.IPerm;

public abstract class AbstractCommand {
    protected CommandCategory category;
    
    public AbstractCommand(CommandCategory category){
        this.category = category;
    }
    
    public abstract void run(CommandSender sender, String[] args);

    public abstract IPerm getPermission();
    public abstract String[] getCommands();
    
    public abstract String getUsage();
    public abstract String getDescription();
    
    /** Convinience method for getting the KothPlugin
     * 
     * @return the KothPlugin
     */
    public KothPlugin getPlugin(){
        return category.getCommandHandler().getPlugin();
    }
    
    public String[] splitArgs(String[] args, int offset){
        return Arrays.copyOfRange(args, offset, args.length);
    }
}
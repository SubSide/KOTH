package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import subside.plugins.koth.AbstractModule;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.Utils;

public class CommandHandler extends AbstractModule implements CommandExecutor {

    private List<AbstractCommand> commands;
    private AbstractCommand fallback;

    public CommandHandler(KothPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad(){
        commands = new ArrayList<>();
        commands.add(new CommandLoot());
        commands.add(new CommandStart());
        commands.add(new CommandCreate());
        commands.add(new CommandReload());
        commands.add(new CommandEdit());
        commands.add(new CommandList());
        commands.add(new CommandStop());
        commands.add(new CommandEnd());
        commands.add(new CommandSchedule());
        commands.add(new CommandRemove());
        commands.add(new CommandVersion());
        commands.add(new CommandAsMember());
        commands.add(new CommandInfo());
        commands.add(new CommandTp());
        commands.add(new CommandMode());
        commands.add(new CommandChange());
        commands.add(new CommandIgnore());
        commands.add(new CommandNext());
        commands.add(new CommandDatatable());
        
        fallback = new CommandHelp();
        commands.add(fallback);
    }
    
    @Override
    public void onEnable(){
        // Register the class to the command
        plugin.getCommand("koth").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        try {
            if(args.length < 1){
                if(fallback.getPermission().has(sender)){
                    fallback.run(sender, args);
                } else {
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_NO_PERMISSION);
                }
                return true;
            }
            
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            for (AbstractCommand command : commands) {
                for (String com : command.getCommands()) {
                    if (!com.equalsIgnoreCase(args[0])) {
                        continue;
                    }
    
                    if (command.getPermission().has(sender)) {
                        command.run(sender, newArgs);
                        return true;
                    }
    
                }
            }
    
            if(fallback.getPermission().has(sender)){
                fallback.run(sender, newArgs);
            } else {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_NO_PERMISSION);
            }
        } catch(CommandMessageException e){
            Utils.sendMessage(sender, true, (Object[])e.getMsg());
        }

        return true;
    }

}

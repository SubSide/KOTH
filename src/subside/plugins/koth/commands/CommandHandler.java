package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.MessageBuilder;

public class CommandHandler implements CommandExecutor {

    private List<ICommand> commands;
    private ICommand fallback;

    public CommandHandler(KothPlugin plugin) {
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
        commands.add(new CommandInfo());

        fallback = new CommandHelp();
        commands.add(fallback);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        try {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            for (ICommand command : commands) {
                for (String com : command.getCommands()) {
                    if (!com.equalsIgnoreCase(cmd.getName())) {
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
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_NO_PERMISSION).build());
            }
        } catch(CommandMessageException e){
            for(String msg : e.getMsg()){
                sender.sendMessage(msg);
            }
        }

        return true;
    }

}

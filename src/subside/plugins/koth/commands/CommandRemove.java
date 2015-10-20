package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandRemove implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth remove <name>");
        }
        
        KothHandler.removeKoth(args[0]);
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_KOTH_REMOVED).koth(args[0]));

    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.REMOVE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "remove"
        };
    }

}

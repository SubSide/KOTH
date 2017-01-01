package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandStop implements AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            KothHandler.getInstance().stopKoth(args[0]);
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TERMINATE_SPECIFIC_KOTH).koth(args[0]));
        } else {
            KothHandler.getInstance().stopAllKoths();
            throw new CommandMessageException(Lang.COMMAND_TERMINATE_ALL_KOTHS);
        }
    }

    @Override
    public IPerm getPermission() {
        return Perm.Control.STOP;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"stop"};
    }

}

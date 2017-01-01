package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandIgnore implements AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if(Utils.toggleIgnoring((Player)sender)){
            throw new CommandMessageException(Lang.COMMAND_IGNORE_START);
        } else {
            throw new CommandMessageException(Lang.COMMAND_IGNORE_STOP);
        }
    }

    @Override
    public IPerm getPermission() {
        return Perm.IGNORE;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"ignore", "stfu", "shutup"};
    }

}

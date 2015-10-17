package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandReload implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        KothPlugin.getPlugin().init();
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_RELOAD_RELOAD).build());
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

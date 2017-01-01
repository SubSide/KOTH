package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandAsMember implements AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        CommandHelp.asMember(sender, args);
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.HELP;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"asmember"};
    }

}

package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandStart implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth start <name> [time] [maxRunTime] [lootAmount]");
        }
        String koth = args[0];
        int runTime = 15;
        int maxRunTime = -1;
        int amount = ConfigHandler.getCfgHandler().getLootAmount();
        if (args.length > 1) {
            try {
                runTime = Integer.parseInt(args[1]);

                if (args.length > 2) {
                    maxRunTime = Integer.parseInt(args[2]);
                }
                
                if (args.length > 3) {
                    amount = Integer.parseInt(args[3]);
                }
            }
            catch (NumberFormatException e) {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth start <name> [time] [maxRunTime] [lootAmount]");
            }
        }
        KothHandler.startKoth(koth, runTime * 60, maxRunTime, amount, null, false);

    }

    @Override
    public IPerm getPermission() {
        return Perm.Control.START;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "start"
        };
    }

}

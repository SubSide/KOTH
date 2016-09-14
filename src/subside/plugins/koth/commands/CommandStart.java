package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.StartParams;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandStart implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth start <name> [time] [maxRunTime] [lootAmount] [entitytype]");
        }
        StartParams params = new StartParams(args[0]);
        if (args.length > 1) {
            try {
                params.setCaptureTime(Utils.convertTime(args[1]));

                if (args.length > 2) {
                    params.setMaxRunTime(Integer.parseInt(args[2]));
                }
                
                if (args.length > 3) {
                    params.setLootAmount(Integer.parseInt(args[3]));
                }
                
                if(args.length > 4){
                    params.setEntityType(args[4]);
                }
            }
            catch (NumberFormatException e) {
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth start <name> [time] [maxRunTime] [lootAmount]");
            }
        }
        KothHandler.getInstance().startKoth(params);

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

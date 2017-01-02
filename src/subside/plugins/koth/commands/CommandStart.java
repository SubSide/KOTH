package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.KothException;
import subside.plugins.koth.gamemodes.StartParams;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandStart extends AbstractCommand {

    public CommandStart(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth start <name> [time] [maxRunTime] [lootAmount] [entitytype]");
        }
        StartParams params = new StartParams(getPlugin().getKothHandler(), args[0]);
        try {
            if (args.length > 1) {
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
            getPlugin().getKothHandler().startKoth(params);
        } catch (NumberFormatException e) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth start <name> [time] [maxRunTime] [lootAmount]");
        } catch (KothException e) {
            throw new CommandMessageException(e.getMsg());
        }

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
    
    @Override
    public String getUsage() {
        return "/koth start <koth>";
    }

    @Override
    public String getDescription() {
        return "Starts a certain koth";
    }

}

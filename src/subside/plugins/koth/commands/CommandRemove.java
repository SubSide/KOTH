package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandRemove extends AbstractCommand {

    public CommandRemove(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth remove <name>");
        }
        
        getPlugin().getKothHandler().removeKoth(args[0]);
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_KOTH_REMOVED).koth(getPlugin().getKothHandler(), args[0]));

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
    
    @Override
    public String getUsage() {
        return "/koth remove <koth>";
    }

    @Override
    public String getDescription() {
        return "removes an existing koth";
    }

}

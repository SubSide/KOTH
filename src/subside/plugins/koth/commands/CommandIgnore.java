package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandIgnore extends AbstractCommand {

    public CommandIgnore(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if(!(sender instanceof Player))
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_ONLYFROMINGAME);
        
        if(Utils.toggleIgnoring(getPlugin(), (Player)sender)){
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

    @Override
    public String getUsage() {
        return "/koth ignore";
    }

    @Override
    public String getDescription() {
        return "Ignore KoTH's messages";
    }

}

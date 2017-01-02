package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothPlugin.LoadingState;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandReload extends AbstractCommand {

    public CommandReload(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        getPlugin().trigger(LoadingState.DISABLE);
        getPlugin().setupModules();
        getPlugin().trigger(LoadingState.LOAD);
        getPlugin().trigger(LoadingState.ENABLE);
        throw new CommandMessageException(Lang.COMMAND_RELOAD_RELOAD);
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.RELOAD;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"reload"};
    }
    
    @Override
    public String getUsage() {
        return "/koth reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin";
    }

}

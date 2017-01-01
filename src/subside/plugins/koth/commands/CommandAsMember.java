package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.Perm;

public class CommandAsMember extends AbstractCommand {

    public CommandAsMember(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        category.getCommandHandler().helpAsMember(sender);
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.HELP;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"asmember"};
    }

    @Override
    public String getUsage() {
        return "/koth asmember";
    }

    @Override
    public String getDescription() {
        return "Shows the help menu as a normal player";
    }

}

package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandList extends AbstractCommand {

    public CommandList(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        new MessageBuilder(Lang.COMMAND_LISTS_LIST_TITLE).buildAndSend(sender);
        for (Koth koth : getPlugin().getKothHandler().getAvailableKoths()) {
            new MessageBuilder(Lang.COMMAND_LISTS_LIST_ENTRY).koth(koth).buildAndSend(sender);
        }
    }

    @Override
    public Perm getPermission() {
        return Perm.LIST;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"list"};
    }

    @Override
    public String getUsage() {
        return "/koth list";
    }

    @Override
    public String getDescription() {
        return "Shows all available koths";
    }

}

package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.KothHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandList implements AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        new MessageBuilder(Lang.COMMAND_LISTS_LIST_TITLE).buildAndSend(sender);
        for (Koth koth : KothHandler.getInstance().getAvailableKoths()) {
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

}

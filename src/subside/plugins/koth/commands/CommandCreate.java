package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandCreate implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_ONLYFROMINGAME);
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth create <name>");
        }
        if (KothHandler.getKoth(args[0]) != null) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_KOTH_ALREADYEXISTS).koth(args[0]));
        }

        Selection sel = KothPlugin.getWorldEdit().getSelection(player);
        if (sel == null) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_WESELECT);
        }
        
        KothHandler.createKoth(args[0], sel.getMinimumPoint(), sel.getMaximumPoint());
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_KOTH_CREATED).koth(args[0]));
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.CREATE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "create"
        };
    }

}

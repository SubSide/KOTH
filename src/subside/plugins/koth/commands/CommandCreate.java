package subside.plugins.koth.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import subside.plugins.koth.areas.Area;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandCreate extends AbstractCommand {

    public CommandCreate(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_ONLYFROMINGAME);
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth create <name>");
        }
        if (getPlugin().getKothHandler().getKoth(args[0]) != null) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_KOTH_ALREADYEXISTS).koth(getPlugin().getKothHandler(), args[0]));
        }

        Selection sel = ((WorldEditPlugin) getPlugin().getServer().getPluginManager().getPlugin("WorldEdit")).getSelection(player);
        if (sel == null) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_WESELECT);
        }
        
        // Create Koth
        Koth koth = new Koth(getPlugin().getKothHandler(), args[0]);
        koth.getAreas().add(new Area(koth.getName(), sel.getMinimumPoint(), sel.getMaximumPoint()));
        getPlugin().getKothHandler().addKoth(koth); // Add it to the list
        
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_KOTH_CREATED).koth(getPlugin().getKothHandler(), args[0]));
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
    
    @Override
    public String getUsage() {
        return "/koth create <koth>";
    }

    @Override
    public String getDescription() {
        return "creates a new koth";
    }

}

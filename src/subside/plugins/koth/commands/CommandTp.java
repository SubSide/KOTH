package subside.plugins.koth.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.Area;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.exceptions.AreaNotExistException;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class CommandTp implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_ONLYFROMINGAME);
        }
        
        if(args.length < 1){
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth tp <koth> [area]");
        }

        Koth koth = KothHandler.getInstance().getKoth(args[0]);
        if (koth == null) {
            throw new KothNotExistException(args[0]);
        }
        Location loc = koth.getMiddle();
        if(loc == null){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_TELEPORT_NOAREAS).koth(koth));
        }
        
        
        if(args.length > 1){
            Area area = koth.getArea(args[1]);
            if(area == null){
                throw new AreaNotExistException(args[1]);
            }
            loc = area.getMiddle();
            
            new MessageBuilder(Lang.COMMAND_TELEPORT_TELEPORTING_AREA).koth(koth).area(area).buildAndSend(sender);
        } else {
            new MessageBuilder(Lang.COMMAND_TELEPORT_TELEPORTING).koth(koth).buildAndSend(sender);
        }
        
        ((Player)sender).teleport(loc.getWorld().getHighestBlockAt(loc).getLocation().clone().add(0.5,0.5,0.5).setDirection(((Player)sender).getLocation().getDirection()));
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.TP;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"tp"};
    }

}

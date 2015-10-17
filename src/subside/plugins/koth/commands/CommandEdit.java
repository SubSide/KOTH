package subside.plugins.koth.commands;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.Area;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.exceptions.AreaAlreadyExistException;
import subside.plugins.koth.exceptions.AreaNotExistException;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandEdit implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).build());
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            Utils.sendMsg(player, 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area").commandInfo("Area commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot").commandInfo("Loot commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> rename <name>").commandInfo("Rename a koth").build());
            return;
        }
        Koth koth = KothHandler.getKoth(args[0]);
        if (koth == null) {
            throw new KothNotExistException();
        }

        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);
        if (args[0].equalsIgnoreCase("area")) {
            area(sender, newArgs, koth);
        } else if(args[0].equalsIgnoreCase("loot")){
            loot(sender, newArgs, koth);
        } else if(args[0].equalsIgnoreCase("rename")){
            name(sender, newArgs, koth);
        } else {
            Utils.sendMsg(player, 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area").commandInfo("Area commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot").commandInfo("Loot commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> rename <name>").commandInfo("Rename a koth").build());
        }

    }

    private void name(CommandSender sender, String[] args, Koth koth){
        if(args.length < 1){
            new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth edit <koth> name <name>").build());
        }
        koth.setName(args[0]);
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_EDITOR_NAME_CHANGE).build());
    }
    
    private void area(CommandSender sender, String[] args, Koth koth) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                Selection selection = KothPlugin.getWorldEdit().getSelection((Player) sender);
                if (selection != null) {
                    if(args.length < 2){
                        new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth edit <koth> area create <name>").build());
                    }
                    Location min = selection.getMinimumPoint();
                    Location max = selection.getMaximumPoint();
                    if(koth.getArea(args[1]) != null){
                        throw new AreaAlreadyExistException(args[1]);
                    }
                        
                    Area area = new Area(args[1], min, max);
                    koth.getAreas().add(area);
                    KothLoader.save();
                    throw new CommandMessageException(Lang.COMMAND_EDITOR_AREA_ADDED);
                } else {
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_WESELECT);
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                new MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_TITLE).buildAndSend(sender);
                for (Area area : koth.getAreas()) {
                    new MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_ENTRY).area(area.getName()).buildAndSend(sender);
                }
            } else if (args[0].equalsIgnoreCase("edit")) {
                Selection selection = KothPlugin.getWorldEdit().getSelection((Player) sender);
                if (selection == null) {
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_WESELECT);
                }
                if(args.length < 2){
                    new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth edit <koth> area edit <name>").build());
                }
                Location min = selection.getMinimumPoint();
                Location max = selection.getMaximumPoint();
                Area area = koth.getArea(args[1]);
                if(area == null){
                    throw new AreaNotExistException(args[1]);
                }
                        
                area.setArea(min, max);
                KothLoader.save();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_AREA_EDITED);
            } else if (args[0].equalsIgnoreCase("remove")){
                Area area = koth.getArea(args[1]);
                if(area == null){
                    throw new AreaNotExistException(args[1]);
                }
                koth.getAreas().remove(area);
                KothLoader.save();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_AREA_DELETED);
            }
        }
        Utils.sendMsg(sender, 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Area commands").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area create <name>").commandInfo("create an area").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area edit <area>").commandInfo("re-sets an area").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area list").commandInfo("shows the area list").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area remove <area>").commandInfo("removes an area").build());
    }
    
    @SuppressWarnings("deprecation")
    private void loot(CommandSender sender, String[] args, Koth koth){
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("setpos")){
                Block block = ((Player)sender).getTargetBlock(null, 8);
                if(block == null){
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_EDITOR_LOOT_SETNOBLOCK).build());
                }
                koth.setLootPos(block.getLocation());
                KothLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_EDITOR_LOOT_POSITION_SET).build());
            } else if(args[0].equalsIgnoreCase("link")){
                if(args.length < 2){
                    new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE+"/koth edit <koth> loot link <loot>").build());
                }
                koth.setLoot(args[1]);
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_EDITOR_LOOT_LINK).build());
            }
        }

        Utils.sendMsg(sender, 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("loot commands").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot setpos").commandInfo("sets the position to the block looking at").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot link <loot>").commandInfo("links a loot chest").build());
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.EDIT;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "edit"
        };
    }

}

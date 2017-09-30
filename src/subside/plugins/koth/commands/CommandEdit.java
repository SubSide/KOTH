package subside.plugins.koth.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import subside.plugins.koth.areas.Area;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.AreaAlreadyExistException;
import subside.plugins.koth.exceptions.AreaNotExistException;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandEdit extends AbstractCommand {

    public CommandEdit(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_ONLYFROMINGAME);
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            Utils.sendMessage(player, true,
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area").commandInfo("Area commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot").commandInfo("Loot commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> rename <name>").commandInfo("Rename a koth").build());
            return;
        }
        Koth koth = getPlugin().getKothHandler().getKoth(args[0]);
        if (koth == null) {
            throw new KothNotExistException(getPlugin().getKothHandler(), args[0]);
        }

        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);
        if (args[1].equalsIgnoreCase("area")) {
            area(sender, newArgs, koth);
        } else if(args[1].equalsIgnoreCase("loot")){
            loot(sender, newArgs, koth);
        } else if(args[1].equalsIgnoreCase("rename")){
            name(sender, newArgs, koth);
        } else {
            Utils.sendMessage(player, true,
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area").commandInfo("Area commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot").commandInfo("Loot commands").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> rename <name>").commandInfo("Rename a koth").build());
        }

    }

    private void name(CommandSender sender, String[] args, Koth koth){
        if(args.length < 1){
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> rename <name>");
        }
        koth.setName(args[0]);
        getPlugin().getKothHandler().saveKoths();
        throw new CommandMessageException(Lang.COMMAND_EDITOR_NAME_CHANGE);
    }
    
    private void area(CommandSender sender, String[] args, Koth koth) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                Selection selection = ((WorldEditPlugin) getPlugin().getServer().getPluginManager().getPlugin("WorldEdit")).getSelection((Player) sender);
                if (selection != null) {
                    if(args.length < 2){
                        throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> area create <name>");
                    }
                    Location min = selection.getMinimumPoint();
                    Location max = selection.getMaximumPoint();
                    if(koth.getArea(args[1]) != null){
                        throw new AreaAlreadyExistException(args[1]);
                    }
                        
                    Area area = new Area(args[1], min, max);
                    koth.getAreas().add(area);
                    getPlugin().getKothHandler().saveKoths();
                    throw new CommandMessageException(Lang.COMMAND_EDITOR_AREA_ADDED);
                } else {
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_WESELECT);
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                new MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_TITLE).buildAndSend(sender);
                for (Area area : koth.getAreas()) {
                    new MessageBuilder(Lang.COMMAND_LISTS_EDITOR_AREA_ENTRY).area(area).buildAndSend(sender);
                }
                return;
            } else if (args[0].equalsIgnoreCase("edit")) {
                Selection selection = ((WorldEditPlugin) getPlugin().getServer().getPluginManager().getPlugin("WorldEdit")).getSelection((Player) sender);
                if (selection == null) {
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_WESELECT);
                }
                if(args.length < 2){
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> area edit <name>");
                }
                Location min = selection.getMinimumPoint();
                Location max = selection.getMaximumPoint();
                Area area = koth.getArea(args[1]);
                if(area == null){
                    throw new AreaNotExistException(args[1]);
                }
                        
                area.setArea(min, max);
                getPlugin().getKothHandler().saveKoths();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_AREA_EDITED);
            } else if (args[0].equalsIgnoreCase("remove")){
                if(args.length < 2){
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> area edit <name>");
                }
                Area area = koth.getArea(args[1]);
                if(area == null){
                    throw new AreaNotExistException(args[1]);
                }
                koth.getAreas().remove(area);
                getPlugin().getKothHandler().saveKoths();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_AREA_DELETED);
            }
        }
        Utils.sendMessage(sender, true,
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Area commands").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area create <name>").commandInfo("create an area").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area edit <area>").commandInfo("re-sets an area").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area list").commandInfo("shows the area list").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> area remove <area>").commandInfo("removes an area").build());
    }

    private void loot(CommandSender sender, String[] args, Koth koth){
        Player player = (Player)sender;
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("setpos")){
                Block block;
                try {
                    Method method;
                    try {
                        method = LivingEntity.class.getDeclaredMethod("getTargetBlock", Set.class, int.class);
                    } catch(NoSuchMethodException e){
                        method = LivingEntity.class.getDeclaredMethod("getTargetBlock", HashSet.class, int.class);
                    }
                    block = (Block)method.invoke(player, null, 8);
                } catch(NoSuchMethodException e){
                    getPlugin().getLogger().severe("Cannot find the getTargetBlock function!");
                    e.printStackTrace();
                    return;
                } catch(IllegalAccessException | InvocationTargetException e){
                    getPlugin().getLogger().severe("Cannot access the getTargetBlock function!");
                    e.printStackTrace();
                    return;
                }

                if(block == null){
                    throw new CommandMessageException(Lang.COMMAND_EDITOR_LOOT_SETNOBLOCK);
                }
                koth.setLootPos(block.getLocation());
                getPlugin().getKothHandler().saveKoths();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_LOOT_POSITION_SET);
            } else if(args[0].equalsIgnoreCase("link")){
                if(args.length < 2){
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> loot link <loot>");
                }
                koth.setLoot(args[1]);
                getPlugin().getKothHandler().saveKoths();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_LOOT_LINK);
            } else if(args[0].equalsIgnoreCase("second")){
                if(args.length < 2){
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> loot second (none|north|east|south|west)");
                }
                try {
                    koth.setSecondLootDirection(Koth.LootDirection.valueOf(args[1].toUpperCase()));
                } catch(Exception e){
                    throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth edit <koth> loot second (none|north|east|south|west)");
                }

                getPlugin().getKothHandler().saveKoths();
                throw new CommandMessageException(Lang.COMMAND_EDITOR_LOOT_SECOND_CHEST);
            }
        }

        Utils.sendMessage(sender, true,
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("loot commands").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot setpos").commandInfo("sets the position to the block looking at").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot link <loot>").commandInfo("links a loot chest").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth edit <koth> loot second (none|north|east|south|west)").commandInfo("set where second chest will spawn").build()
        );
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
    
    @Override
    public String getUsage() {
        return "/koth edit <koth>";
    }

    @Override
    public String getDescription() {
        return "Edits an existing koth";
    }

}

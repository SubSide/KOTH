package subside.plugins.koth.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.LootAlreadyExistException;
import subside.plugins.koth.exceptions.LootNotExistException;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandLoot implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_ONLYFROMINGAME);
        }

        Player player = (Player) sender;
        if (!Perm.Admin.LOOT.has(sender)){
            asMember(sender, args);
            return;
        }
        
        if (args.length < 1) {
            Utils.sendMessage(player, true,
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Loot editor").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot create <loot>").commandInfo("Create loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot edit <loot>").commandInfo("Edit loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot remove <loot>").commandInfo("Remove loot chest").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot>").commandInfo("Access loot commands").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot list").commandInfo("List loot chests").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot asmember").commandInfo("Shows what members would see").build());
            return;
        }

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        if (args[0].equalsIgnoreCase("create")) {
            create(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("edit")){
            edit(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("list")){
            list(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("remove")){
            remove(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("rename")){
            rename(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("asmember")){
            asMember(sender, newArgs);
        } else if(args[0].equalsIgnoreCase("cmd")){
            commands(sender, newArgs);
        } else {
            Utils.sendMessage(player, true,
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Loot editor").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot create <loot>").commandInfo("Create loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot edit <loot>").commandInfo("Edit loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot remove <loot>").commandInfo("Remove loot chest").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot rename <loot> <newname>").commandInfo("Remove loot chest").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot>").commandInfo("Access loot commands").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot list").commandInfo("List loot chests").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot asmember").commandInfo("Shows what members would see").build());
        }
    }
    
    private void rename(CommandSender sender, String[] args){
        if(args.length < 2){
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth loot rename <loot> <newname>");
        }
        
        Loot loot = KothHandler.getInstance().getLoot(args[0]);
        if(loot == null){
            throw new LootNotExistException(args[0]);
        }
        
        loot.setName(args[1]);
        throw new CommandMessageException(Lang.COMMAND_LOOT_RENAME);
    }
    
    private void asMember(CommandSender sender, String[] args){
        Player player = (Player)sender;
        RunningKoth rKoth = KothHandler.getInstance().getRunningKoth();
        String loot;
        if(rKoth == null || rKoth.getKoth() == null){
            Schedule sched = ScheduleHandler.getInstance().getNextEvent();
            if(sched == null){
                return;
            }
            if(sched.getLootChest() != null){
                loot = sched.getLootChest();
            } else {
                Koth koth = KothHandler.getInstance().getKoth(sched.getKoth());
                if(koth == null){
                    if(!ConfigHandler.getCfgHandler().getLoot().getDefaultLoot().equalsIgnoreCase("")){
                        loot = ConfigHandler.getCfgHandler().getLoot().getDefaultLoot();
                    } else {
                        return;
                    }
                } else if(koth.getLoot() != null){
                    loot = koth.getLoot();
                } else {
                    loot = ConfigHandler.getCfgHandler().getLoot().getDefaultLoot();
                }
            }
            
        } else {
            if(rKoth.getLootChest() == null){
                if(rKoth.getKoth().getLoot() == null){
                    loot = ConfigHandler.getCfgHandler().getLoot().getDefaultLoot();
                } else {
                    loot = rKoth.getKoth().getLoot();
                }
            } else {
                loot = rKoth.getLootChest();
            }
        }
        if(loot != null){
            Loot lt = KothHandler.getInstance().getLoot(loot);
            if(lt != null){
                player.openInventory(lt.getInventory());
            }
        }
    }
    
    
    private void create(CommandSender sender, String[] args){
        if(args.length < 1){
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth loot create <loot>");
        }
        
        Loot loot = KothHandler.getInstance().getLoot(args[0]);
        if(loot != null){
            throw new LootAlreadyExistException(args[0]);
        }
        
        KothHandler.getInstance().getLoots().add(new Loot(args[0]));
        LootLoader.save();
        throw new CommandMessageException(Lang.COMMAND_LOOT_CREATE);
    }
    
    private void edit(CommandSender sender, String[] args){
        if(args.length < 1){
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth loot edit <loot> (commands)");
        }
        
        Loot loot = KothHandler.getInstance().getLoot(args[0]);
        if(loot == null){
            throw new LootNotExistException(args[0]);
        }
        
        ((Player)sender).openInventory(loot.getInventory());
        
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_OPENING).loot(loot.getName()));
    }
    
    private void commands(CommandSender sender, String[] args){
        if(!ConfigHandler.getCfgHandler().getLoot().isCmdIngame()){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CMD_INGAME_DISABLED));
        }
        
        if(!sender.isOp() && ConfigHandler.getCfgHandler().getLoot().isCmdNeedOp()){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CMD_OPONLY));
        }
        
        if(args.length < 2){
            Utils.sendMessage(sender, true,
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Loot editor").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> add <command>").commandInfo("Add a command").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> list").commandInfo("Show a list of commands").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> remove <id>").commandInfo("Remove a command").build());
            if(ConfigHandler.getCfgHandler().getLoot().isCmdEnabled()){
                Utils.sendMessage(sender, true, "&cNote: commands are disabled in the config!");
            }
        return;
        }
        String lootName = args[0];

        Loot loot = KothHandler.getInstance().getLoot(lootName);
        if(loot == null){
            throw new CommandMessageException(new MessageBuilder(Lang.LOOT_ERROR_NOTEXIST).loot(lootName));
        }
                
        if(args[1].equalsIgnoreCase("add")){
            if(args.length < 3)
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth loot cmd <loot> add <command>");
            
            loot.getCommands().add(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
            LootLoader.save();
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CMD_CREATED).loot(lootName));
        } else if(args[1].equalsIgnoreCase("remove")){
            if(args.length < 2)
                throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth loot cmd <loot> remove <id>");
            try {
                int id = Integer.parseInt(args[2]);
                loot.getCommands().remove(id);
                LootLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CMD_REMOVED).loot(lootName).id(id));
            } catch(NumberFormatException e){
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_CMD_NOTANUMBER));
            }
        } else if(args[1].equalsIgnoreCase("list")){
            new MessageBuilder(Lang.COMMAND_LISTS_LOOT_CMD_TITLE).buildAndSend(sender);
            List<String> cmds = loot.getCommands();
            for (String cmd : cmds) {
                new MessageBuilder(Lang.COMMAND_LISTS_LOOT_CMD_ENTRY).id(cmds.indexOf(cmd)).command(cmd).buildAndSend(sender);
            }
        } else {
            Utils.sendMessage(sender, true,
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Loot editor").build(),
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> add").commandInfo("Create loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> list").commandInfo("Edit loot chest").build(),  
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> remove <id>").commandInfo("Remove loot chest").build());
            if(ConfigHandler.getCfgHandler().getLoot().isCmdEnabled()){
                Utils.sendMessage(sender, true, "&cNote: commands are disabled in the config!");
            }
            return;
        }
    }
    
    private void list(CommandSender sender, String[] args){
        new MessageBuilder(Lang.COMMAND_LISTS_LOOT_TITLE).buildAndSend(sender);
        for (Loot loot : KothHandler.getInstance().getLoots()) {
            new MessageBuilder(Lang.COMMAND_LISTS_LOOT_ENTRY).loot(loot.getName()).buildAndSend(sender);
        }
    }

    private void remove(CommandSender sender, String[] args){
        if(args.length < 1){
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0]+"/koth loot remove <loot>");
        }
        
        Loot loot = KothHandler.getInstance().getLoot(args[0]);
        if(loot == null){
            throw new LootNotExistException(args[0]);
        }
        
        KothHandler.getInstance().getLoots().remove(loot);
        LootLoader.save();
        
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_LOOT_REMOVE).loot(loot.getName()));
    }
    
    @Override
    public IPerm getPermission() {
        return Perm.LOOT;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "loot"
        };
    }

}

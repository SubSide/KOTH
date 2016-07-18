package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.Area;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.exceptions.LootNotExistException;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandInfo implements ICommand {

    @Override
    public void run(CommandSender sender, String[] args) {
        if (Perm.Admin.INFO.has(sender)) {
            if (args.length < 2) {
                Utils.sendMsg(sender, 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info koth <koth>").commandInfo("Info about a koth").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info loot <loot>").commandInfo("Info about a loot chest").build(), 
                    new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info schedule <schedule>").commandInfo("Info about a schedule").build());
                return;
            }

            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            if(args[0].equalsIgnoreCase("koth")) {
                kothInfo(sender, newArgs);
            } else if(args[0].equalsIgnoreCase("loot")){
                lootInfo(sender, newArgs);
            } else if(args[0].equalsIgnoreCase("schedule")){
                scheduleInfo(sender, newArgs);
            } else {
                Utils.sendMsg(sender, 
                        new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(), 
                        new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info koth <koth>").commandInfo("Info about a koth").build(), 
                        new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info loot <loot>").commandInfo("Info about a loot chest").build(), 
                        new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info schedule <ID>").commandInfo("Info about a schedule").build());
            }
            
        } else if (Perm.VERSION.has(sender)) {
            List<String> list = new ArrayList<>();
            list.add(" ");
            list.addAll(new MessageBuilder("&8========> &2INFO &8<========").buildArray());
            list.addAll(new MessageBuilder("&2Author: &aSubSide").buildArray());
            list.addAll(new MessageBuilder("&2Version: &a" + KothPlugin.getPlugin().getDescription().getVersion()).buildArray());
            list.addAll(new MessageBuilder("&2Site: &ahttp://bit.ly/1Pyxu2N").buildArray());
            sender.sendMessage(list.toArray(new String[list.size()]));
        } else {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_NO_PERMISSION);
        }
    }
    
    public void kothInfo(CommandSender sender, String[] args){
        Koth koth = KothHandler.getInstance().getKoth(args[0]);
        if (koth == null) {
            throw new KothNotExistException(args[0]);
        }
        
        String C1 = "&2";
        String C2 = "&a";
        if(Lang.COMMAND_INFO_COLORS.length > 1){
            C1 = Lang.COMMAND_INFO_COLORS[0];
            C2 = Lang.COMMAND_INFO_COLORS[1];
        }
        

        Location loc = koth.getMiddle();
        Location lootLoc= koth.getLootPos();
        
        String name = koth.getName();
        String lastWinner = (koth.getLastWinner()!=null)?(koth.getLastWinner().getName()):("None");
        lastWinner = (lastWinner != null && !lastWinner.equalsIgnoreCase(""))?lastWinner:"None";
        String location = "("+loc.getWorld().getName()+", "+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ()+")";
        
        String linkedLoot = (koth.getLoot() != null)?koth.getLoot():"None";
        
        String lootLocation = "(?, ?, ?)";
        if(lootLoc != null)
            lootLocation = "("+lootLoc.getWorld().getName()+", "+lootLoc.getBlockX()+", "+lootLoc.getBlockY()+", "+lootLoc.getBlockZ()+")";
        
        String areas = "";
        for(Area area : koth.getAreas()){
            areas += area.getName()+", ";
        }
        if(areas.length() > 2){
            areas = areas.substring(0, areas.length()-2);
        } else {
            areas = "None";
        }
        
        
        String linkedSchedules = "";
        for(int x = 0; x < ScheduleHandler.getInstance().getSchedules().size(); x++){
            Schedule schedule = ScheduleHandler.getInstance().getSchedules().get(x);
            if(koth.getName().equalsIgnoreCase(schedule.getKoth())){
                linkedSchedules += "#"+x+", ";
            }
        }
        
        if(linkedSchedules.length() > 2){
            linkedSchedules = linkedSchedules.substring(0, linkedSchedules.length()-2);
        } else {
            linkedSchedules = "None";
        }
        
        
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_INFO_TITLE_KOTH).koth(koth).buildArray());
        list.addAll(new MessageBuilder(C1+"Name: "+C2+name).buildArray());
        list.addAll(new MessageBuilder(C1+"Last winner: "+C2+lastWinner).buildArray());
        list.addAll(new MessageBuilder(C1+"Location: "+C2+location).buildArray());
        
        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Linked loot: "+C2+linkedLoot).buildArray());
        list.addAll(new MessageBuilder(C1+"Loot position: "+C2+lootLocation).buildArray());
        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Areas:").buildArray());
        list.addAll(new MessageBuilder(C2+areas).buildArray());
        
        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Schedules linked:").buildArray());
        list.addAll(new MessageBuilder(C2+linkedSchedules).buildArray());
        
        
        sender.sendMessage(list.toArray(new String[list.size()]));
        
    }
    
    public void lootInfo(CommandSender sender, String[] args){
        Loot loot = KothHandler.getInstance().getLoot(args[0]);
        if (loot == null) {
            throw new LootNotExistException(args[0]);
        }
        
        String C1 = "&2";
        String C2 = "&a";
        if(Lang.COMMAND_INFO_COLORS.length > 1){
            C1 = Lang.COMMAND_INFO_COLORS[0];
            C2 = Lang.COMMAND_INFO_COLORS[1];
        }
        
        String name = loot.getName();
        
        int amountItems = 0;
        for (ItemStack stack : loot.getInventory().getContents()) {
            if (stack != null) {
                amountItems++;
            }
        }


        String linkedKoths = "";
        for(Koth koth : KothHandler.getInstance().getAvailableKoths()){
            if(loot.getName().equalsIgnoreCase(koth.getLoot())){
                linkedKoths += koth.getName()+", ";
            }
        }
        
        if(linkedKoths.length() > 2){
            linkedKoths = linkedKoths.substring(0, linkedKoths.length()-2);
        } else {
            linkedKoths = "None";
        }

        
        
        String linkedSchedules = "";
        for(int x = 0; x < ScheduleHandler.getInstance().getSchedules().size(); x++){
            Schedule schedule = ScheduleHandler.getInstance().getSchedules().get(x);
            if(loot.getName().equalsIgnoreCase(schedule.getLootChest())){
                linkedSchedules += "#"+x+", ";
            }
        }
        
        if(linkedSchedules.length() > 2){
            linkedSchedules = linkedSchedules.substring(0, linkedSchedules.length()-2);
        } else {
            linkedSchedules = "None";
        }
        
        
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_INFO_TITLE_LOOT).loot(name).buildArray());
        list.addAll(new MessageBuilder(C1+"Name: "+C2+name).buildArray());
        list.addAll(new MessageBuilder(C1+"Contains: "+C2+amountItems+" filled slots").buildArray());

        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Koths linked:").buildArray());
        list.addAll(new MessageBuilder(C2+linkedKoths).buildArray());
        
        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Schedules linked:").buildArray());
        list.addAll(new MessageBuilder(C2+linkedSchedules).buildArray());
        sender.sendMessage(list.toArray(new String[list.size()]));
        
    }
    

    
    public void scheduleInfo(CommandSender sender, String[] args){
        Schedule sched;
        try {
            sched = ScheduleHandler.getInstance().getSchedules().get(Integer.parseInt(args[0]));
        } catch(NumberFormatException e){
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTANUMBER);
        } catch(IndexOutOfBoundsException f){
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
        }
        
        String C1 = "&2";
        String C2 = "&a";
        if(Lang.COMMAND_INFO_COLORS.length > 1){
            C1 = Lang.COMMAND_INFO_COLORS[0];
            C2 = Lang.COMMAND_INFO_COLORS[1];
        }
        
        String id = "#"+args[0];
        String day = sched.getDay().getDay();
        String time = sched.getTime();
        String linkedKoth = sched.getKoth();

        String lootChest = (sched.getLootChest()==null)?("None (Inherited)"):(sched.getLootChest());
        String lootAmount = sched.getLootAmount()+" items";
        
        String captureTime = sched.getCaptureTime()+" minutes";
        String maxRunTime = sched.getMaxRunTime()+" minutes";
        
        if(sched.getMaxRunTime() == -1){
            maxRunTime = "Unlimited";
        }
        
        
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_INFO_TITLE_SCHEDULE).id(Integer.parseInt(args[0])).buildArray());
        list.addAll(new MessageBuilder(C1+"ID: "+C2+id).buildArray());
        list.addAll(new MessageBuilder(C1+"Day: "+C2+day).buildArray());
        list.addAll(new MessageBuilder(C1+"Time: "+C2+time).buildArray());
        list.addAll(new MessageBuilder(C1+"KoTH: "+C2+linkedKoth).buildArray());

        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Using loot: "+C2+lootChest).buildArray());
        list.addAll(new MessageBuilder(C1+"With: "+lootAmount+" items").buildArray());
        
        list.add(" ");
        list.addAll(new MessageBuilder(C1+"Capture time: "+C2+captureTime).buildArray());
        list.addAll(new MessageBuilder(C1+"Max runtime: "+C2+maxRunTime).buildArray());
        sender.sendMessage(list.toArray(new String[list.size()]));
        
    }

    @Override
    public IPerm getPermission() {
        return Perm.ALLOW.ALLOW;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "info"
        };
    }

}

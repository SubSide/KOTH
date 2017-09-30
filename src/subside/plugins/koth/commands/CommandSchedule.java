package subside.plugins.koth.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import subside.plugins.koth.commands.CommandHandler.CommandCategory;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.scheduler.Day;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandSchedule extends AbstractCommand {

    public CommandSchedule(CommandCategory category) {
        super(category);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!Perm.Admin.SCHEDULE.has(sender)) {
            asMember(sender, args);
            return;
        }

        if (args.length > 0) {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            if (args[0].equalsIgnoreCase("create")) {
                pre_create(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("remove")) {
                remove(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("list")) {
                admin_list(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("edit")) {
                edit(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("asmember")) {
                asMember(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("clear")) {
                clear(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("debug")) {
                debug(sender, newArgs);
            } else {
                help(sender, newArgs);
            }
        } else {
            help(sender, args);
        }
    }
    
    private void debug (CommandSender sender, String[] args){
        List<Schedule> schedules = getPlugin().getScheduleHandler().getSchedules();
        
        for (Schedule schedule : schedules) {
            long time = (schedule.getNextEvent()- System.currentTimeMillis())/1000;
            sender.sendMessage(ChatColor.DARK_GREEN+"KoTH: "+ChatColor.GREEN+schedule.getKoth()+ChatColor.DARK_GREEN+" ID: "+ChatColor.GREEN+schedules.indexOf(schedule)+ChatColor.DARK_GREEN+" startsin: "+ChatColor.GREEN+time+"secs");
        }
    }
    
    private void clear (CommandSender sender, String[] args) {
        getPlugin().getScheduleHandler().getSchedules().clear();
        getPlugin().getScheduleHandler().saveSchedules();
        throw new CommandMessageException(Lang.COMMAND_SCHEDULE_CLEARED);
    }

    private void asMember(CommandSender sender, String[] args) {
        Day dayFilter = Day.getCurrentDay();
        boolean showSingleDay = getPlugin().getConfigHandler().getGlobal().isCurrentDayOnly();

        if(args.length > 0){
            if(args[0].equalsIgnoreCase("today")) {
                dayFilter = Day.getCurrentDay();
            } else {
                dayFilter = Day.getDay(args[0]);
            }
            showSingleDay = true;
        }

        List<Schedule> schedules = getPlugin().getScheduleHandler().getSchedules();
        List<String> list = new ArrayList<>();
       
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_CURRENTDATETIME).date(Utils.parseCurrentDate(getPlugin())).buildArray());
        for (Day day : Day.values()) {
            // All filtering, like day arguments and such
            if(showSingleDay && day != dayFilter)
                continue;
            
            List<String> subList = new ArrayList<>();
            for (Schedule sched : schedules) {
                if (sched.getDay() == day) {
                    subList.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_ENTRY).day(day.getDay()).lootAmount(sched.getLootAmount()).koth(getPlugin().getKothHandler(), sched.getKoth()).timeTillNext(sched).time(sched.getTime()).captureTime(sched.getCaptureTime()).buildArray());
                }
            }
            if (subList.size() > 0) {
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_DAY).day(day.getDay()).buildArray());
                list.addAll(subList);
            } else if(showSingleDay){
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_DAY).day(day.getDay()).buildArray());
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_NOENTRYFOUND).day(day.getDay()).buildArray());
            }
        }
        if (schedules.size() < 1) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_EMPTY);
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
        if(Lang.COMMAND_SCHEDULE_LIST_BOTTOM.length > 0 && !Lang.COMMAND_SCHEDULE_LIST_BOTTOM[0].equalsIgnoreCase("")){
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_BOTTOM));
        }
    }

    private void pre_create(CommandSender sender, String[] args){
        if (args.length < 3) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule create <koth> <day|(daily)> <time> [capturetime] [maxruntime] [lootamount] [lootchest]"));
        }
        
        String[] days = args[1].split(",");
        if(args[1].equalsIgnoreCase("daily")){
            days = Arrays.stream(Day.values()).map(Day::getDay).toArray(size -> new String[Day.values().length]);
            
        }
        String[] times = args[2].split(",");
        
        for(String day : days){
            for(String time : times){
                try {
                    create(sender, args, day, time);
                } catch(CommandMessageException e){
                    for(String msg : e.getMsg()){
                        Utils.sendMessage(sender, true, msg);
                    }
                }
            }
        }
    }
    
    private void create(CommandSender sender, String[] args, String pDay, String pTime) {
        if (args.length < 2) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule create <koth> <day|(daily)> <time> [capturetime] [maxruntime] [lootamount] [lootchest]"));
        }
        
        Day day = Day.getDay(pDay);
        if (day == null) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOVALIDDAY));
        }
        Schedule schedule = new Schedule(getPlugin().getScheduleHandler(), args[0], day, pTime);
        
        int captureTime = 15;
        int maxRunTime = -1;
        int lootAmount = getPlugin().getConfigHandler().getLoot().getLootAmount();
        String lootChest = null;
        String entityType = null;
        try {
            if (args.length > 3) {
                captureTime = Integer.parseInt(args[3]);
            }

            if (args.length > 4) {
                maxRunTime = Integer.parseInt(args[4]);
            }

            if (args.length > 5) {
                lootAmount = Integer.parseInt(args[5]);
            }
        }
        catch (Exception e) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER));
        }
        
        if (args.length > 6) {
            if(!args[6].equalsIgnoreCase("0")){
                lootChest = args[6];
            }
        }
        
        if(args.length > 7){
            if(!args[7].equalsIgnoreCase("0")){
                entityType = args[7];
            }
        }
        
        schedule.setMaxRunTime(maxRunTime);
        schedule.setCaptureTime(captureTime);
        schedule.setLootAmount(lootAmount);
        schedule.setLootChest(lootChest);
        schedule.setEntityType(entityType);

        getPlugin().getScheduleHandler().getSchedules().add(schedule);
        getPlugin().getScheduleHandler().saveSchedules();
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_CREATED).koth(getPlugin().getKothHandler(), args[0]).lootAmount(lootAmount).day(day.getDay()).time(pTime).captureTime(captureTime));

    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule remove <ID>");
        }
        
        try {
            String kth = getPlugin().getScheduleHandler().removeId(Integer.parseInt(args[0]));
            if (kth == null) {
                throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
            }
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVED).koth(getPlugin().getKothHandler(), kth));
        }
        catch (NumberFormatException e) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_REMOVENOID);
        }
        catch (IndexOutOfBoundsException e) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
        }
    }

    private void admin_list(CommandSender sender, String[] args) {
        List<Schedule> schedules = getPlugin().getScheduleHandler().getSchedules();
        List<String> list = new ArrayList<>();
        
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME).date(Utils.parseCurrentDate(getPlugin())).buildArray());
        for (Day day : Day.values()) {
            List<String> subList = new ArrayList<>();
            for (Schedule sched : schedules) {
                if (sched.getDay() == day) {
                    subList.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_ENTRY).id(schedules.indexOf(sched)).day(day.getDay()).maxTime(sched.getMaxRunTime() * 60).koth(getPlugin().getKothHandler(), sched.getKoth()).time(sched.getTime()).captureTime(sched.getCaptureTime()).buildArray());
                }
            }
            if (subList.size() > 0) {
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_DAY).day(day.getDay()).buildArray());
                list.addAll(subList);
            }
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
        if (schedules.size() < 1) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_ADMIN_EMPTY);
        }
    }

    private void edit(CommandSender sender, String[] args) {
        if (args.length > 2) {
            int id;
            Schedule schedule;
            try {
                id = Integer.parseInt(args[0]);
                schedule = getPlugin().getScheduleHandler().getSchedules().get(id);
            }
            catch (Exception e) {
                throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
            }

            if (args[1].equalsIgnoreCase("koth")) { // change koth
                schedule.setKoth(args[2]);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_KOTH).koth(getPlugin().getKothHandler(), args[2]).id(id));
            } else if (args[1].equalsIgnoreCase("capturetime")) { // change capturetime
                int captureTime;
                try {
                    captureTime = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER).id(id));
                }
                schedule.setCaptureTime(captureTime);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_CAPTURETIME).id(id));
            } else if (args[1].equalsIgnoreCase("day")) { // change day
                Day day = Day.getDay(args[2]);
                if (day == null) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NOVALIDDAY).id(id));
                }
                schedule.setDay(day);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_DAY).id(id).day(day.getDay()));
            } else if (args[1].equalsIgnoreCase("time")) { // change time
                schedule.setTime(args[2]);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_TIME).id(id).time(args[2]));
            } else if (args[1].equalsIgnoreCase("maxruntime")) { // change
                                                                 // maxruntime
                int maxRunTime;
                try {
                    maxRunTime = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER).id(id));
                }
                schedule.setMaxRunTime(maxRunTime);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_MAXRUNTIME).id(id));
            } else if (args[1].equalsIgnoreCase("lootamount")) { // change loot
                                                                 // amount
                int lootAmount;
                try {
                    lootAmount = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER).id(id));
                }
                schedule.setLootAmount(lootAmount);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOTAMOUNT).id(id));
            } else if (args[1].equalsIgnoreCase("loot")) { // change loot chest
                if (args[2].equalsIgnoreCase("0")) {
                    schedule.setLootChest(null);
                } else {
                    schedule.setLootChest(args[2]);
                }
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOT).id(id));
            } else if (args[1].equalsIgnoreCase("entitytype")) { // change time
                schedule.setEntityType(args[2]);
                getPlugin().getScheduleHandler().saveSchedules();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_ENTITYTYPE).id(id));
            }
        }
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Scheddule editor").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> koth <kothname>").commandInfo("change the koth").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> day <day>").commandInfo("change the day").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> time <time>").commandInfo("change the time (e.g. 4:32AM)").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> capturetime <capturetime>").commandInfo("change the capturetime").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> maxruntime <maxruntime>").commandInfo("change the maxruntime").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> lootamount <amount>").commandInfo("change the loot amount").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> loot <loot>").commandInfo("change the loot chest (0 to clear)").build());
        sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> entitytype <entitytype>").commandInfo("change the entitytype").build());

    }

    private void help(CommandSender sender, String[] args) {
        Utils.sendMsg(sender, 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Schedule editor").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule create").commandInfo("schedule a koth").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit").commandInfo("Edit an existing schedule").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule remove <ID>").commandInfo("removes an existing schedule").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule list").commandInfo("shows the ID's of the schedule").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule asmember").commandInfo("shows the schedule as member").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule clear").commandInfo("clear the complete schedule list").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule debug").commandInfo("debug info").build());
    }

    @Override
    public IPerm getPermission() {
        return Perm.SCHEDULE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "schedule",
            "time"
        };
    }
    
    @Override
    public String getUsage() {
        return "/koth schedule";
    }

    @Override
    public String getDescription() {
        return "Shows the schedule menu";
    }

}

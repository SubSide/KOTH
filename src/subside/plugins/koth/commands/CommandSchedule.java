package subside.plugins.koth.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.exceptions.CommandMessageException;
import subside.plugins.koth.loaders.ScheduleLoader;
import subside.plugins.koth.scheduler.Day;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.utils.IPerm;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class CommandSchedule implements ICommand {

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
            } else {
                help(sender, newArgs);
            }
        } else {
            help(sender, args);
        }
    }
    
    private void clear (CommandSender sender, String[] args) {
        ScheduleHandler.getInstance().getSchedules().clear();
        ScheduleLoader.save();
        throw new CommandMessageException(Lang.COMMAND_SCHEDULE_CLEARED);
    }

    private void asMember(CommandSender sender, String[] args) {
        List<Schedule> schedules = ScheduleHandler.getInstance().getSchedules();
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getGlobal().getTimeZone()));
       
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_CURRENTDATETIME).date(sdf.format(new Date(System.currentTimeMillis() + ConfigHandler.getCfgHandler().getGlobal().getMinuteOffset()*60*1000))).buildArray());
        for (Day day : Day.values()) {
            List<String> subList = new ArrayList<>();
            for (Schedule sched : schedules) {
                if (sched.getDay() == day) {
                    subList.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_ENTRY).day(day.getDay()).lootAmount(sched.getLootAmount()).koth(sched.getKoth()).time(sched.getTime()).captureTime(sched.getCaptureTime()).buildArray());
                }
            }
            if (subList.size() > 0) {
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_DAY).day(day.getDay()).buildArray());
                list.addAll(subList);
            }
        }
        if (schedules.size() < 1) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_EMPTY);
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
    }

    private void pre_create(CommandSender sender, String[] args){
        if (args.length < 2) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule create <koth> <day|(daily)> <time> [capturetime] [maxruntime] [lootamount] [lootchest]"));
        }
        
        String[] days = args[1].split(",");
        if(args[1].equalsIgnoreCase("daily")){
            days = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            
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
        Schedule schedule = new Schedule(args[0], day, pTime);
        
        int captureTime = 15;
        int maxRunTime = -1;
        int lootAmount = ConfigHandler.getCfgHandler().getLoot().getLootAmount();
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

        ScheduleHandler.getInstance().getSchedules().add(schedule);
        ScheduleLoader.save();
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_CREATED).koth(args[0]).lootAmount(lootAmount).day(day.getDay()).time(pTime).captureTime(captureTime));

    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule remove <ID>");
        }
        
        try {
            String kth = ScheduleHandler.getInstance().removeId(Integer.parseInt(args[0]));
            if (kth == null) {
                throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
            }
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVED).koth(kth));
        }
        catch (NumberFormatException e) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_REMOVENOID);
        }
        catch (IndexOutOfBoundsException e) {
            throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
        }
    }

    private void admin_list(CommandSender sender, String[] args) {
        List<Schedule> schedules = ScheduleHandler.getInstance().getSchedules();
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getGlobal().getTimeZone()));
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME).date(sdf.format(new Date(System.currentTimeMillis() + ConfigHandler.getCfgHandler().getGlobal().getMinuteOffset()*60*1000))).buildArray());
        for (Day day : Day.values()) {
            List<String> subList = new ArrayList<>();
            for (Schedule sched : schedules) {
                if (sched.getDay() == day) {
                    subList.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_ENTRY).id(schedules.indexOf(sched)).day(day.getDay()).maxTime(sched.getMaxRunTime() * 60).koth(sched.getKoth()).time(sched.getTime()).captureTime(sched.getCaptureTime()).buildArray());
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
                schedule = ScheduleHandler.getInstance().getSchedules().get(id);
            }
            catch (Exception e) {
                throw new CommandMessageException(Lang.COMMAND_SCHEDULE_NOTEXIST);
            }

            if (args[1].equalsIgnoreCase("koth")) { // change koth
                schedule.setKoth(args[2]);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_KOTH).koth(args[2]).id(id));
            } else if (args[1].equalsIgnoreCase("capturetime")) { // change capturetime
                int captureTime;
                try {
                    captureTime = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER).id(id));
                }
                schedule.setCaptureTime(captureTime);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_CAPTURETIME).id(id));
            } else if (args[1].equalsIgnoreCase("day")) { // change day
                Day day = Day.getDay(args[2]);
                if (day == null) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NOVALIDDAY).id(id));
                }
                schedule.setDay(day);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_DAY).id(id).day(day.getDay()));
            } else if (args[1].equalsIgnoreCase("time")) { // change time
                schedule.setTime(args[2]);
                ScheduleLoader.save();
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
                ScheduleLoader.save();
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
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOTAMOUNT).id(id));
            } else if (args[1].equalsIgnoreCase("loot")) { // change loot chest
                if (args[2].equalsIgnoreCase("0")) {
                    schedule.setLootChest(null);
                } else {
                    schedule.setLootChest(args[2]);
                }
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOT).id(id));
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

    }

    private void help(CommandSender sender, String[] args) {
        Utils.sendMsg(sender, 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Schedule editor").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule create").commandInfo("schedule a koth").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit").commandInfo("Edit an existing schedule").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule remove <ID>").commandInfo("removes an existing schedule").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule list").commandInfo("shows the ID's of the schedule").build(), 
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule asmember").commandInfo("shows the schedule as member").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule clear").commandInfo("clear the complete schedule list").build());
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

}

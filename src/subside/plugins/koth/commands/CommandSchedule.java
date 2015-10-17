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
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
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
        if (args.length > 0 && Perm.Admin.SCHEDULE.has(sender)) {
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            if (args[0].equalsIgnoreCase("create")) {
                create(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("remove")) {
                remove(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("list")) {
                admin_list(sender, newArgs);
            } else if (args[0].equalsIgnoreCase("edit")) {
                edit(sender, newArgs);
            } else {
                help(sender, newArgs);
            }
        } else {
            list(sender, args);
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (args.length < 3) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE + "/koth schedule create <koth> <day> <time>").build());
        }

        Koth koth = KothHandler.getKoth(args[0]);
        if (koth == null) {
            throw new CommandMessageException(new MessageBuilder(Lang.KOTH_ERROR_NOTEXIST).koth(args[0]).build());
        }

        Day day = Day.getDay(args[1].toUpperCase());
        if (day == null) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOVALIDDAY).build());
        }

        String time = args[2];

        ScheduleHandler.getInstance().getSchedules().add(new Schedule(koth.getName(), day, time));
        throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_CREATED).koth(koth.getName()).day(day.getDay()).time(time).build());
    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_GLOBAL_USAGE + "/koth schedule remove <ID>").build());
        }
        try {
            String kth = ScheduleHandler.getInstance().removeId(Integer.parseInt(args[0]));
            if (kth != null) {
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVED).koth(kth).build());
            } else {
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).build());
            }
        }
        catch (NumberFormatException e) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVENOID).build());
        }
        catch (IndexOutOfBoundsException e) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).build());
        }
    }

    private void list(CommandSender sender, String[] args) {
        List<Schedule> schedules = ScheduleHandler.getInstance().getSchedules();
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_CURRENTDATETIME).date(sdf.format(new Date())).buildArray());
        for (Day day : Day.values()) {
            List<String> subList = new ArrayList<>();
            for (Schedule sched : schedules) {
                if (sched.getDay() == day) {
                    subList.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_ENTRY).day(day.getDay()).lootAmount(sched.getLootAmount()).koth(sched.getKoth()).time(sched.getTime()).length(sched.getRunTime()).buildArray());
                }
            }
            if (subList.size() > 0) {
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_DAY).day(day.getDay()).buildArray());
                list.addAll(subList);
            }
        }
        if (schedules.size() < 1) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EMPTY).build());
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
    }

    private void admin_list(CommandSender sender, String[] args) {
        List<Schedule> schedules = ScheduleHandler.getInstance().getSchedules();
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone(ConfigHandler.getCfgHandler().getTimeZone()));
        list.add(" ");
        list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME).date(sdf.format(new Date())).buildArray());
        for (Day day : Day.values()) {
            List<String> subList = new ArrayList<>();
            for (Schedule sched : schedules) {
                if (sched.getDay() == day) {
                    subList.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_ENTRY).id(schedules.indexOf(sched)).day(day.getDay()).maxTime(sched.getMaxRunTime() * 60).koth(sched.getKoth()).time(sched.getTime()).length(sched.getRunTime()).buildArray());
                }
            }
            if (subList.size() > 0) {
                list.addAll(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_DAY).day(day.getDay()).buildArray());
                list.addAll(subList);
            }
        }
        sender.sendMessage(list.toArray(new String[list.size()]));
        if (schedules.size() < 1) {
            throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_EMPTY).build());
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
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).build());
            }

            if (args[1].equalsIgnoreCase("koth")) {
                schedule.setKoth(args[2]);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_KOTH).koth(args[2]).id(id).build());
            } else if (args[1].equalsIgnoreCase("runtime")) {
                int runTime;
                try {
                    runTime = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NONUMBER).id(id).build());
                }
                schedule.setRunTime(runTime);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_RUNTIME).id(id).build());
            } else if (args[1].equalsIgnoreCase("day")) {
                Day day = Day.getDay(args[2]);
                if (day == null) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NOVALIDDAY).id(id).build());
                }
                schedule.setDay(day);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_DAY).id(id).day(day.getDay()).build());
            } else if (args[1].equalsIgnoreCase("time")) {
                schedule.setTime(args[2]);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_TIME).id(id).time(args[2]).build());
            } else if (args[1].equalsIgnoreCase("maxruntime")) {
                int maxRunTime;
                try {
                    maxRunTime = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NONUMBER).id(id).build());
                }
                schedule.setMaxRunTime(maxRunTime);
                ScheduleLoader.save();
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_MAXRUNTIME).id(id).build());
            } else if (args[1].equalsIgnoreCase("lootamount")) {
                int lootAmount;
                try {
                    lootAmount = Integer.parseInt(args[2]);
                }
                catch (Exception e) {
                    throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NONUMBER).id(id).build());
                }
                schedule.setLootAmount(lootAmount);
                throw new CommandMessageException(new MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOTAMOUNT).id(id).build());
            } else {
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).build());
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> koth <kothname>").commandInfo("change the koth").build());
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> runtime <runtime>").commandInfo("change the runtime").build());
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> day <day>").commandInfo("change the day").build());
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> time <time>").commandInfo("change the time (e.g. 4:32AM)").build());
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> maxruntime <maxruntime>").commandInfo("change the maxruntime").build());
                sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> lootamount <amount>").commandInfo("change the loot amount").build());
            }

        } else {
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).build());
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> koth <kothname>").commandInfo("change the koth").build());
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> runtime <runtime>").commandInfo("change the runtime").build());
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> day <day>").commandInfo("change the day").build());
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> time <time>").commandInfo("change the time (e.g. 4:32AM)").build());
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> maxruntime <maxruntime>").commandInfo("change the maxruntime").build());
            sender.sendMessage(new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> lootamount <amount>").commandInfo("change the loot amount").build());
        }
    }

    // private @Getter String koth;
    // private @Getter int runTime;
    // private @Getter Day day;
    // private @Getter String time;
    // private @Getter int maxRunTime;
    // private @Getter int lootAmount;

    private void help(CommandSender sender, String[] args) {
        Utils.sendMsg(sender,
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule create").commandInfo("schedule a koth").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule remove <ID>").commandInfo("removes an existing schedule").build(),
                new MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule list").commandInfo("shows the ID's of the schedule").build()
        );
    }

    @Override
    public IPerm getPermission() {
        return Perm.Admin.SCHEDULE;
    }

    @Override
    public String[] getCommands() {
        return new String[] {
            "schedule"
        };
    }

}

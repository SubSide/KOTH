package subside.plugins.koth.commands.editor

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.scheduler.Day
import subside.plugins.koth.scheduler.Schedule
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils

class CommandSchedule : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (!Perm.Admin.SCHEDULE.has(sender)) {
            asMember(kothManager, sender, args)
            return
        }

        if (args.isEmpty()) {
            help(sender, args)
            return
        }

        val newArgs = offsetArgs(args, 1)
        when (args[0].toLowerCase()) {
            "create" -> preCreate(kothManager, sender, newArgs)
            "edit" -> edit(kothManager, sender, newArgs)
            "remove" -> remove(kothManager, sender, newArgs)
            "list" -> adminList(kothManager, sender, newArgs)
            "asmember" -> asMember(kothManager, sender, newArgs)
            "clear" -> clear(kothManager, sender, newArgs)
            "debug" -> debug(kothManager, sender, newArgs)
            else -> help(sender, args)
        }
    }

    private fun preCreate(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.size < 3) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule create <koth> <day|(daily)> <time> [capturetime] [maxruntime] [lootamount] [lootchest]")
                .send(sender)
            return
        }

        var days = args[1].split(",")
        if (args[0].equals("daily", ignoreCase = true)) {
           days = Day.values().map { it.text }.toArray()
        }

        val times = args[2].split(",")

        days.forEach { day ->
            times.forEach { time ->
                create(kothManager, sender, args, day, time)
            }
        }
    }

    private fun create(kothManager: KothManager, sender: CommandSender, args: Array<String>, pDay: String, pTime: String) {
        if (args.size < 2) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule create <koth> <day|(daily)> <time> [capturetime] [maxruntime] [lootamount] [lootchest]")
                .send(sender)
            return
        }

        val day = Day.values().find { it.text == pDay }
        if (day == null) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_NOVALIDDAY)
                .day(pDay)
                .send(sender)
            return
        }

        val schedule = Schedule(args[0], day, pTime)

        var captureTime = 15
        var maxRunTime = -1
        var lootAmount = kothManager.configHandler.loot.lootAmount
        var lootChest: String? = null
        var entityType: String? = null

        try {
            if (args.size > 3) captureTime = Integer.parseInt(args[3])
            if (args.size > 4) maxRunTime = Integer.parseInt(args[4])
            if (args.size > 5) lootAmount = Integer.parseInt(args[5])
            if (args.size > 6) lootChest = args[6]
            if (args.size > 7) entityType = args[7]
        } catch (e: Exception) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER).send(sender)
            return
        }

        schedule.let {
            it.maxRunTime = maxRunTime
            it.captureTime = captureTime
            it.lootAmount = lootAmount
            it.lootChest = lootChest
            it.entityType = entityType
        }

        kothManager.scheduleHandler.schedules.add(schedule)
        kothManager.scheduleHandler.saveSchedules()

        MessageBuilder(Lang.COMMAND_SCHEDULE_CREATED)
            .koth(kothManager.kothHandler, args[0])
            .lootAmount(lootAmount)
            .day(day.text)
            .time(pTime)
            .captureTime(captureTime)
            .send(sender)
    }

    private fun remove(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth schedule remove <ID>")
                .send(sender)
            return
        }
        try {
            val schedule = kothManager.scheduleHandler.schedules.getOrNull(Integer.parseInt(args[0]))
            if (schedule == null) {
                MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).send(sender)
                return
            }

            kothManager.scheduleHandler.schedules.remove(schedule)
            kothManager.scheduleHandler.save()
            MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVED).send(sender)
        } catch (e: NumberFormatException) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_REMOVENOID).send(sender)
        }
    }

    private fun adminList(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val list = ArrayList<String>()

        list.add(" ")
        list.addAll(
            MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_CURRENTDATETIME)
                .date(Utils.parseCurrentDate())
                .buildArray()
        )

        // If there are no schedules we just tell the user that
        if (kothManager.scheduleHandler.schedules.isEmpty()) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_EMPTY).send(sender)
            return
        }

        // We map the days to their schedules.
        val days: Map<String, List<Pair<Int, Schedule>>> = Day.values().map { it.text }.map { day ->
            day to kothManager.scheduleHandler.schedules
                .mapIndexed { id, schedule -> id to schedule }
                .filter { it.second.day == day }
        }.toMap()

        // And then loop over all of them
        days.forEach { (day, schedules) ->
            if (schedules.isEmpty())
                return@forEach

            list.addAll(MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_DAY).day(day).buildArray())
            schedules.forEach { (id, schedule) ->
                list.addAll(
                    MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_ENTRY)
                        .id(id)
                        .day(day)
                        .maxTime(schedule.maxRunTime * 60)
                        .koth(kothManager.kothHandler, schedule.koth)
                        .time(schedule.time)
                        .captureTime(schedule.captureTime)
                        .buildArray()
                )
            }
        }

        sender.sendMessage(list.toTypedArray())
    }

    private fun edit(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        fun showHelp() {
            Utils.sendMessage(sender, true,
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Scheddule editor").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> koth <kothname>").commandInfo("change the koth").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> day <day>").commandInfo("change the day").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> time <time>").commandInfo("change the time (e.g. 4:32AM)").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> capturetime <capturetime>").commandInfo("change the capturetime").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> maxruntime <maxruntime>").commandInfo("change the maxruntime").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> lootamount <amount>").commandInfo("change the loot amount").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> loot <loot>").commandInfo("change the loot chest (0 to clear)").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit <ID> entitytype <entitytype>").commandInfo("change the entitytype").build())
            return
        }

        if (args.size < 3) {
            showHelp()
            return
        }

        val id: Int
        val schedule = try {
            id = Integer.parseInt(args[0])
            kothManager.scheduleHandler.schedules[id]
        } catch (e: Exception) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).send(sender)
            return
        }

        fun saveSchedule(block: (Schedule) -> Unit) {
            block.invoke(schedule)
            kothManager.scheduleHandler.saveSchedules()
        }

        when (args[1].toLowerCase()) {
            "koth" -> {
                saveSchedule { it.koth = args[2] }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_KOTH)
                    .koth(kothManager.kothHandler, args[2])
                    .id(id)
                    .send(sender)
            }
            "capturetime" -> {
                val captureTime = try {
                    Integer.parseInt(args[2])
                } catch (e: Exception) {
                    MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER)
                        .id(id)
                        .send(sender)
                    return
                }
                saveSchedule { it.captureTime = captureTime }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_CAPTURETIME)
                    .id(id)
                    .send(sender)
            }
            "day" -> {
                val day = Day.values().find { it.text = args[2] }
                if (day == null) {
                    MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_NOVALIDDAY)
                        .id(id)
                        .send(sender)
                }
                saveSchedule { it.day = day }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_DAY)
                    .id(id)
                    .day(day.text)
                    .send(sender)
            }
            "time" -> {
                saveSchedule { it.time = args[2] }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_TIME)
                    .id(id)
                    .time(args[2])
                    .send(sender)
            }
            "maxruntime" -> {
                val maxRunTime = try {
                    Integer.parseInt(args[2])
                } catch (e: Exception) {
                    MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER)
                        .id(id)
                        .send(sender)
                    return
                }
                saveSchedule { it.maxRunTime = maxRunTime }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_MAXRUNTIME)
                    .id(id)
                    .send(sender)
            }
            "lootamount" -> {
                val lootAmount = try {
                    Integer.parseInt(args[2])
                } catch (e: Exception) {
                    MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER)
                        .id(id)
                        .send(sender)
                }
                saveSchedule { it.lootAmount = lootAmount }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOTAMOUNT)
                    .id(id)
                    .send(sender)
            }
            "loot" -> {
                saveSchedule { it.lootChest = if (args[2] == "0") null else args[2] }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_LOOT)
                    .id(id)
                    .send(sender)
            }
            "entitytype" -> {
                saveSchedule { it.entityType = args[2] }
                MessageBuilder(Lang.COMMAND_SCHEDULE_EDITOR_CHANGE_ENTITYTYPE)
                    .id(id)
                    .send(sender)
            }
            else -> showHelp()
        }
    }

    private fun debug(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        kothManager.scheduleHandler.schedules.forEachIndexed { index, schedule ->
            val time = (schedule.nextEvent - System.currentTimeMillis()) / 1000
            sender.sendMessage(
                "${ChatColor.DARK_GREEN}KoTH: ${ChatColor.GREEN} ${schedule.koth} " +
                        "${ChatColor.DARK_GREEN}ID: ${ChatColor.GREEN}$index " +
                        "${ChatColor.DARK_GREEN}Starts in: ${ChatColor.GREEN}$time secs"

            )
        }
    }

    private fun clear(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        kothManager.scheduleHandler.schedules.clear()
        kothManager.scheduleHandler.saveSchedules()
        MessageBuilder(Lang.COMMAND_SCHEDULE_CLEARED).send(sender)
    }

    private fun asMember(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val list = ArrayList<String>()

        fun sendBottomList() {
            if (
                Lang.COMMAND_SCHEDULE_LIST_BOTTOM.size > 0 &&
                !Lang.COMMAND_SCHEDULE_LIST_BOTTOM[0].equals("", ignoreCase = true)
            ) {
                MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_BOTTOM).send(sender)
            }
        }

        list.add(" ")
        list.addAll(
            MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_CURRENTDATETIME)
                .date(Utils.parseCurrentDate())
                .buildArray()
        )

        // If there are no messages we return nothing
        if (kothManager.scheduleHandler.schedules.isEmpty()) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_EMPTY).send(sender)
            sendBottomList()
            return
        }

        // We map the days to a list of schedules
        val days: Map<String, List<Schedule>> = Day.values().map { it.text }.map { day ->
            day to kothManager.scheduleHandler.schedules.filter { it.day == day }
        }.toMap()

        fun scheduleToMessage(schedule: Schedule): List<String> {
            return MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_ENTRY)
                .day(schedule.day.getText())
                .lootAmount(schedule.lootAmount)
                .koth(kothManager.kothHandler, schedule.koth)
                .timeTillNext(schedule)
                .time(schedule.time)
                .captureTime(sched.captureTime)
                .buildArray()
        }


        if (kothManager.configHandler.global.isCurrentDayOnly) {
            val day = Day.currentDay.text
            val schedules = days[day]

            list.addAll(MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_DAY).day(day).buildArray())

            if (schedules.isNullOrEmpty()) {
                list.addAll(MessageBuilder(Lang.COMMAND_SCHEDULE_LIST_NOENTRYFOUND).day(day).buildArray())
            } else {
                schedules.forEach {
                    list.addAll(scheduleToMessage(it))
                }
            }
        } else {
            days.forEach { (day, schedules) ->
                if (schedules.isEmpty())
                    return@forEach

                list.addAll(MessageBuilder(Lang.COMMAND_SCHEDULE_ADMIN_LIST_DAY).day(day).buildArray())
                schedules.forEach { schedule ->
                    list.addAll(scheduleToMessage(schedule))
                }
            }
        }

        sender.sendMessage(list.toTypedArray())

        sendBottomList()
    }


    private fun help(sender: CommandSender, args: Array<String>) {
        Utils.sendMsg(
            sender,
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Schedule editor").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule create").commandInfo("schedule a koth").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule edit").commandInfo("Edit an existing schedule").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule remove <ID>").commandInfo("removes an existing schedule").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule list").commandInfo("shows the ID's of the schedule").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule asmember").commandInfo("shows the schedule as member").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule clear").commandInfo("clear the complete schedule list").build(),
            MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth schedule debug").commandInfo("debug info").build()
        )
    }

    override val permission: IPerm = Perm.SCHEDULE
    override val commands: Array<String> = arrayOf("schedule", "time")
    override val usage: String = "/koth schedule"
    override val description: String = "Shows the schedule menu"
}
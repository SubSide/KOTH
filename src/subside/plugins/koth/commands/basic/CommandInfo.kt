package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.gamemodes.TimeObject
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.*
import java.lang.NumberFormatException

class CommandInfo : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (Perm.Admin.INFO.has(sender)) {
            if (args.size < 2) {
                sendHelp(sender)
                return
            }

            val newArgs = offsetArgs(args, 1)
            when (args[0].toLowerCase()) {
                "koth"-> kothInfo(kothManager, sender, newArgs)
                "loot" -> lootInfo(sender, newArgs)
                "schedule" -> scheduleInfo(sender, newArgs)
                else -> sendHelp(sender)
            }
        } else if (Perm.VERSION.has(sender)) {
            CommandVersion.sendVersionInfo(kothManager, sender, false)
        } else {
            MessageBuilder(Lang.COMMAND_GLOBAL_NO_PERMISSION).buildAndSend(sender)
        }
    }

    private fun kothInfo(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val koth = kothManager.kothHandler.getKoth(args[0])
        if (koth == null) {
            MessageBuilder(Lang.KOTH_ERROR_NOTEXIST).koth(kothManager.kothHandler, koth).buildAndSend(sender)
            return
        }

        val (C1, C2) = getColors()

        val list = ArrayList<String>()
        list.add(" ")
        list.addAll(MessageBuilder(Lang.COMMAND_INFO_TITLE_KOTH).koth(koth).buildArray())
        list.addAll(MessageBuilder("${C1}Name: ${C2}${koth.name}").buildArray())
        list.addAll(MessageBuilder("${C1}Last winner: ${C2}${koth.lastWinner?.name ?: "(None)"}").buildArray())
        list.addAll(MessageBuilder("${C1}Location: ${C2}${koth.middle?.formatted() ?: "(?, ?, ?)" }").buildArray())

        list.add(" ")
        list.addAll(MessageBuilder("${C1}Linked loot: ${C2}${koth.loot?.lootName ?: "None"}").buildArray())
        list.addAll(MessageBuilder("${C1}Loot position: ${C2}${koth.loot?.locations?.getOrNull(0)?.formatted() ?: "(?, ?, ?)"}").buildArray())

        list.add(" ")
        list.addAll(MessageBuilder("${C1}Areas:").buildArray())
        val areas = if (koth.areas.isEmpty()) "None" else koth.areas.joinToString(", ")
        list.addAll(MessageBuilder("${C2}$areas").buildArray())

        list.add(" ")
        list.addAll(MessageBuilder("${C1}Schedules linked:").buildArray())
        var schedules = kothManager.scheduleHandler.schedules
                .mapIndexed { index, schedule -> Pair(schedule, "#$index") }
                .filter { it.first.koth == koth.name }
                .joinToString(", ")
        if (schedules.isBlank()) schedules = "None"
        list.addAll(MessageBuilder("${C2}$schedules").buildArray())

        sender.sendMessage(list.toTypedArray())
    }

    private fun lootInfo(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val loot = kothManager.lootHandler.getLoot(args[0])
        if (loot == null) {
            MessageBuilder(Lang.LOOT_ERROR_NOTEXIST).loot(loot).buildAndSend(sender)
            return
        }

        val (C1, C2) = getColors()


        val list = ArrayList<String>()
        list.add(" ")
        list.addAll(MessageBuilder(Lang.COMMAND_INFO_TITLE_LOOT).loot(loot.name).buildArray())
        list.addAll(MessageBuilder("${C1}Name: ${C2}${loot.name}").buildArray())
        list.addAll(MessageBuilder("${C1}Contains: ${C2}${loot.inventory.contents.count { it != null }} filled slots").buildArray())

        list.add(" ")
        list.addAll(MessageBuilder("${C1}Koth linked:").buildArray())
        var koths = kothManager.kothHandler.availableKoths
                .filter { it.loot?.lootName == loot.name }
                .joinToString(", ")
        if (koths.isBlank()) koths = "None"

        list.addAll(MessageBuilder("${C2}$koths").buildArray())

        list.add(" ")
        list.addAll(MessageBuilder("${C1}Schedules linked:").buildArray())
        var schedules = kothManager.scheduleHandler.schedules
                .mapIndexed { index, schedule -> Pair(schedule, "#$index")}
                .filter { it.first.lootChest == loot.name }
                .joinToString(", ")
        if (schedules.isBlank()) schedules = "None"
        list.addAll(MessageBuilder("${C2}$schedules").buildArray())

        sender.sendMessage(list.toTypedArray())
    }

    private fun scheduleInfo(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val id: Int
        val schedule = try {
            id = args[0].toInt()
            kothManager.scheduleHandler.schedules[id]
        } catch (e: NumberFormatException) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_NOTANUMBER).buildAndSend(sender)
            return
        } catch (e: NumberFormatException) {
            MessageBuilder(Lang.COMMAND_SCHEDULE_NOTEXIST).buildAndSend(sender)
            return
        }

        val (C1, C2) = getColors()

        val list = ArrayList<String>()
        list.add(" ")
        list.addAll(MessageBuilder(Lang.COMMAND_INFO_TITLE_SCHEDULE).id(id).buildArray())
        list.addAll(MessageBuilder("${C1}ID: ${C2}$id").buildArray())
        list.addAll(MessageBuilder("${C1}Day: ${C2}${schedule.day.day}").buildArray())
        list.addAll(MessageBuilder("${C1}Time: ${C2}${schedule.time}").buildArray())
        list.addAll(MessageBuilder("${C1}KoTH: ${C2}${schedule.koth}").buildArray())

        list.add(" ")
        list.addAll(MessageBuilder("${C1}Using loot: ${C2}${schedule.lootChest ?: "None (Inherited)"}").buildArray())
        list.addAll(MessageBuilder("${C1}With: ${C2}${schedule.lootAmount} items").buildArray())

        list.add(" ")
        var captureType: String = schedule.captureType
        try {
            captureType += " (Java class: "+kothManager.captureTypeRegistry.getCaptureTypeClass(schedule.entityType)+")"
        } catch (e: Exception) {
            captureType += " (Invalid CaptureType)"
        }
        list.addAll(MessageBuilder("${C1}CaptureType: ${C2}$captureType").buildArray())
        list.addAll(MessageBuilder("${C1}Capture time: ${C2}${schedule.captureTime} minutes").buildArray())
        list.addAll(MessageBuilder("${C1}Max runtime: ${C2}${schedule.maxRunTime} minutes").buildArray())
        list.addAll(MessageBuilder("${C1}Starts in: ${C2}${TimeObject.getTimeTillNextEvent(schedule)}").buildArray())

        sender.sendMessage(list.toTypedArray())
    }



    private fun getColors(): Array<String> {
        var C1: String = "&2"
        var C2: String = "&a"
        if (Lang.COMMAND_INFO_COLORS.size > 1) {
            C1 = Lang.COMMAND_INFO_COLORS[0]
            C2 = Lang.COMMAND_INFO_COLORS[1]
        }
        return arrayOf(C1, C2)
    }

    private fun sendHelp(sender: CommandSender) {
        Utils.sendMessage(sender, true,
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("KoTH editor").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info koth <koth>").commandInfo("Info about a koth").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info loot <loot>").commandInfo("Info about a loot chest").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth info schedule <schedule>").commandInfo("Info about a schedule").build())
    }


    override val permission = Perm.ALLOW.ALLOW
    override val commands = arrayOf("info")
    override val usage = "/koth info"
    override val description = "Info about various thins (helpful!)"
}
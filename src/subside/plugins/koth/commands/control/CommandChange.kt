package subside.plugins.koth.commands.control

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.gamemodes.KothConquest
import subside.plugins.koth.gamemodes.RunningKoth
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import java.util.*

class CommandChange : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            help(sender)
            return
        }

        val runningKoth = kothManager.kothHandler.runningKoth
        if (runningKoth == null) {
            MessageBuilder(Lang.KOTH_ERROR_NONE_RUNNING).buildAndSend(sender)
            return
        }

        val newArgs = offsetArgs(args, 1)
        when(args[0].toLowerCase()) {
            "points" -> points(sender, newArgs, runningKoth)
            "time" -> time(sender, newArgs, runningKoth)
            else -> help(sender)
        }
        // TODO add time and such
    }

    private fun time(sender: CommandSender, args: Array<String>, runningKoth: RunningKoth) {
        // TODO
    }

    private fun points(sender: CommandSender, args: Array<String>, runningKoth: RunningKoth) {
        if (args.isEmpty()) {
            val list: MutableList<String> = ArrayList()
            list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Change points").buildArray())
            list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points set <faction> <points>").commandInfo("Set the points of a faction").buildArray())
            list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points add <faction> <points>").commandInfo("Add points to a faction").buildArray())
            list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points del <faction> <points>").commandInfo("remove points from a faction").buildArray())
            list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points reset <faction>").commandInfo("reset points of a faction").buildArray())
            sender.sendMessage(list.toTypedArray())
            return
        }
        val pointsHelp = MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth change points (add|set|del|reset) <faction> <points>")

        if (args.size < 3) {
            pointsHelp.buildAndSend(sender)
            return
        }

        if (runningKoth !is KothConquest) {
            MessageBuilder(Lang.KOTH_ERROR_NOT_COMPATIBLE).buildAndSend(sender)
            return
        }

        val fScore = runningKoth.fScores.find { it.faction.name.equals(args[1], ignoreCase = true) }

        // Here we calculate how many points we need to give
        fScore.points = if (args[0].equals("reset", ignoreCase = true)) {
            0
        } else {
            if (args.size < 3) {
                pointsHelp.buildAndSend(sender)
            }

            val argPoints = try {
                Integer.parseInt(args[2])
            } catch (e: NumberFormatException) {
                MessageBuilder(Lang.COMMAND_CHANGE_POINTS_NOTANUMBER)
                        .buildAndSend(sender)
                return
            }

            when (args[0].toLowerCase()) {
                "add" -> fScore.points + argPoints
                "del" -> fScore.points - argPoints
                "set" -> argPoints
                else -> {
                    pointsHelp.buildAndSend(sender)
                    return
                }
            }
        }
    }

    fun help(sender: CommandSender) {
        val list: MutableList<String> = ArrayList()
        list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Running game manager").buildArray())
        list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change time").commandInfo("Command to change the time").buildArray())
        list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth change points").commandInfo("Manage the points").buildArray())
        sender.sendMessage(list.toTypedArray())
    }

    override val permission: IPerm = Perm.Admin.CHANGE
    override val commands: Array<String> = arrayOf("change")
    override val usage: String = "/koth change"
    override val description: String = "Gives control over a running KoTH"
}
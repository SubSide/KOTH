package subside.plugins.koth.commands

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import java.util.ArrayList

class CommandAsMember : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        val rKoth = kothManager.kothHandler.runningKoth
        val list = kothManager.configHandler.global.helpCommand.map { help ->
            MessageBuilder(help)
                .koth(kothManager.kothHandler, rKoth?.koth ?: "None")
                .let { if (rKoth != null) it.time(rKoth.timeObject) else it.time("00:00")}
                .capper(rKoth?.capper?.name ?: "None")

        }
        sender.sendMessage(list.toTypedArray())
    }

    override val permission: IPerm = Perm.HELP
    override val commands: Array<String> = arrayOf("asmember")
    override val usage: String = "/koth asmember"
    override val description: String = "Shows the help menu as a normal player"
}
package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils

class CommandIgnore : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).buildAndSend(sender)
            return
        }

        if (Utils.toggleIgnoring(kothManager, sender)) {
            MessageBuilder(Lang.COMMAND_IGNORE_START).buildAndSend(sender)
        } else {
            MessageBuilder(Lang.COMMAND_IGNORE_STOP).buildAndSend(sender)
        }
    }

    override val permission = Perm.IGNORE
    override val commands = arrayOf("ignore", "stfu", "shutup")
    override val usage = "/koth ignore"
    override val description = "Ignore KoTH's messages"
}
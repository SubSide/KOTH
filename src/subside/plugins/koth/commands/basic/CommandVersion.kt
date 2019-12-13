package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.KothHandler
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import java.util.*

class CommandVersion : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        sendVersionInfo(kothManager, sender, Perm.Admin.HELP.has(sender))
    }

    override val permission = Perm.VERSION
    override val commands = arrayOf("version")
    override val usage = "/koth version"
    override val description = "Shows the version of this plugin"


    companion object {
        fun sendVersionInfo(kothManager: KothManager, commandSender: CommandSender, showIfOutdated: Boolean) {
            val list: MutableList<String> = ArrayList()
            list.add(" ")
            list.addAll(MessageBuilder("&8========> &2INFO &8<========").buildArray())
            list.addAll(MessageBuilder("&2Author: &aSubSide").buildArray())

            val version = ("&a" + kothManager.plugin.get()?.description?.version
                    + if (showIfOutdated && kothManager.versionChecker.newVersion != null) " &7(outdated)" else "")
            list.addAll(MessageBuilder("&2Version: &a$version").buildArray())
            list.addAll(MessageBuilder("&2Site: &ahttps://github.com/SubSide/KOTH").buildArray())
            commandSender.sendMessage(list.toTypedArray())
        }
    }
}
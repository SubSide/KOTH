package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandList : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        MessageBuilder(Lang.COMMAND_LISTS_LIST_TITLE).buildAndSend(sender)
        kothManager.kothHandler.availableKoths.forEach {
            MessageBuilder(Lang.COMMAND_LISTS_LIST_ENTRY).koth(it).buildAndSend(sender)
        }
    }

    override val permission = Perm.LIST
    override val commands = arrayOf("list")
    override val usage = "/koth list"
    override val description = "Shows all available koths"

}
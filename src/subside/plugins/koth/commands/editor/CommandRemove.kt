package subside.plugins.koth.commands.editor

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandRemove : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth remove <name>")
                    .send(sender)
            return
        }

        kothManager.kothHandler.removeKoth(args[0])
        MessageBuilder(Lang.COMMAND_KOTH_REMOVED)
                .koth(kothManager.kothHandler, args[0])
                .send(sender)
    }

    override val permission: IPerm = Perm.Admin.REMOVE
    override val commands: Array<String> = arrayOf("remove")
    override val usage: String = "/koth remove <koth>"
    override val description: String = "removes an existing koth"
}
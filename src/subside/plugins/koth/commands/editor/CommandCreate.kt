package subside.plugins.koth.commands.editor

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.areas.Area
import subside.plugins.koth.areas.Koth
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandCreate : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).buildAndSend(sender)
            return
        }

        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0].toString() + "/koth create <name>")
                    .buildAndSend(sender)
            return
        }

        if (kothManager.kothHandler.getKoth(args[0]) != null) {
            MessageBuilder(Lang.COMMAND_KOTH_ALREADYEXISTS)
                    .koth(kothManager.kothHandler, args[0])
                    .buildAndSend(sender)
            return
        }

        val selection = (kothManager.plugin.get()!!.server.pluginManager.getPlugin("WorldEdit") as WorldEditPlugin)
                .getSelection(sender)

        if (selection == null) {
            MessageBuilder(Lang.COMMAND_GLOBAL_WESELECT).buildAndSend(sender)
            return
        }

        val koth = Koth(args[0])
        koth.areas.add(Area(koth.name, selection.minimumPoint, selection.maximumPoint))
        kothManager.kothHandler.addKoth(koth)

        MessageBuilder(Lang.COMMAND_KOTH_CREATED)
                .koth(koth)
                .buildAndSend(sender)
    }

    override val permission: IPerm = Perm.Admin.CREATE
    override val commands: Array<String> = arrayOf("create")
    override val usage: String = "/koth create <koth>"
    override val description: String = "creates a new KoTH"
}
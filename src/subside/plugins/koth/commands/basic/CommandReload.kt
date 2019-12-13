package subside.plugins.koth.commands.basic

import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.KothPlugin
import subside.plugins.koth.commands.Command
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm

class CommandReload : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        kothManager.plugin.get()?.apply {
            trigger(KothPlugin.LoadingState.DISABLE)
            setupModules()
            trigger(KothPlugin.LoadingState.LOAD)
            trigger(KothPlugin.LoadingState.ENABLE)
        }

        MessageBuilder(Lang.COMMAND_RELOAD_RELOAD).buildAndSend(sender)
    }

    override val permission = Perm.Admin.RELOAD
    override val commands = arrayOf("reload")
    override val usage = "/koth reload"
    override val description = "Reloads the plugin"
}
package subside.plugins.koth.commands

import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.basic.*
import subside.plugins.koth.commands.basic.datatable.CommandDatatable
import subside.plugins.koth.commands.basic.datatable.CommandTop
import subside.plugins.koth.commands.control.*
import subside.plugins.koth.commands.editor.*
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.modules.Module
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils
import java.util.*

class CommandHandler: Module, CommandExecutor {
    val categories: MutableList<Category> = ArrayList()
    private lateinit var fallback: Command

    override fun onLoad(kothManager: KothManager) {
        categories.clear()

        fallback = CommandAsMember()

        // Basic commands
        val basic = Category("basic", "KoTH Basic Commands")
        basic.addCommands(
            CommandList(),
            fallback,
            CommandVersion(),
            CommandReload(),
            CommandTp(),
            CommandInfo(),
            CommandNext(),
            CommandIgnore()
        )

        if (kothManager.dataTable != null) {
            basic.addCommands(
                CommandDatatable(),
                CommandTop()
            )
        }
        categories.add(basic)

        // Control commands
        val control = Category("control", "KoTH Control Commands")
        control.addCommands(
            CommandStart(),
            CommandStop(),
            CommandEnd(),
            CommandMode(),
            CommandChange()
        )
        categories.add(control)

        // Editor commands
        val editor = Category("editor", "KoTH Editor Commands")
        editor.addCommands(
            CommandCreate(),
            CommandRemove(),
            CommandEdit(),
            CommandLoot(),
            CommandSchedule()
        )
        categories.add(editor)
    }

    override fun onEnable(kothManager: KothManager) {
        // Register the class to the command
        kothManager.plugin.get()?.getCommand("koth")?.executor = this
    }

    override fun onCommand(
        sender: CommandSender,
        cmd: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): Boolean {
        if (args.isEmpty()) {
            showHelp(kothManager, sender)
            return true
        }

        val newArgs = Arrays.copyOfRange(args, 1, args.size)
        categories.forEach { category ->
            category.commands
                .filter { it.commands.contains(args[0].toLowerCase()) }
                .filter { it.permission.has(sender) }
                .forEach {
                    it.run(kothManager, sender, newArgs)
                    return true
                }
        }

        showHelp(kothManager, sender)
        return true
    }

    fun showHelp(kothManager: KothManager, sender: CommandSender) {
        if (!Perm.Admin.HELP.has(sender)) {
            if(!fallback.permission.has(sender)) {
                MessageBuilder(Lang.COMMAND_GLOBAL_NO_PERMISSION).send(sender)
                return
            }
            fallback.run(kothManager, sender, emptyArray())
            return
        }

        val list = ArrayList<String>()
        list.add(" ")
        categories.forEach { category ->
            val commands = category.commands.filter { it.permission.has(sender) }
            if (commands.isEmpty()) return@forEach

            list.addAll(MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title(category.name).buildArray())
            commands.forEach { command ->
                list.addAll(
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO)
                        .command(command.usage)
                        .commandInfo(command.description)
                        .buildArray()
                )
            }
        }
        list.add(" ")
        sender.sendMessage(list.toTypedArray())
    }


    class Category(
        val id: String,
        val name: String,
        vararg commands: Command
    ) {
        internal val commands: MutableList<Command> = commands.toMutableList()


        fun addCommands(vararg command: Command) {
            commands.addAll(command)
        }
    }
}
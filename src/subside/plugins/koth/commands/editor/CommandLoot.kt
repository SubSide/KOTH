package subside.plugins.koth.commands.editor

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.commands.Command
import subside.plugins.koth.loot.Loot
import subside.plugins.koth.modules.Lang
import subside.plugins.koth.utils.IPerm
import subside.plugins.koth.utils.MessageBuilder
import subside.plugins.koth.utils.Perm
import subside.plugins.koth.utils.Utils

class CommandLoot : Command {
    override fun run(kothManager: KothManager, sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            MessageBuilder(Lang.COMMAND_GLOBAL_ONLYFROMINGAME).send(sender)
            return
        }

        if (!Perm.Admin.LOOT.has(sender)) {
            asMember(sender, args)
            return
        }

        fun showHelp() {
            Utils.sendMessage(sender, true,
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Loot editor").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot create <loot>").commandInfo("Create loot chest").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot edit <loot>").commandInfo("Edit loot chest").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot remove <loot>").commandInfo("Remove loot chest").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot>").commandInfo("Access loot commands").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot list").commandInfo("List loot chests").build(),
                    MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot asmember").commandInfo("Shows what members would see").build())
            return
        }

        if (args.isEmpty()) {
            showHelp()
            return
        }

        val newArgs = offsetArgs(args, 1)
        when (args[1].toLowerCase()) {
            "create" -> create(kothManager, sender, newArgs)
            "edit" -> edit(kothManager, sender, newArgs)
            "list" -> list(kothManager, sender, newArgs)
            "remove" -> remove(kothManager, sender, newArgs)
            "rename" -> rename(kothManager, sender, newArgs)
            "asmember" -> asMember(kothManager, sender, newArgs)
            "cmd" -> commands(kothManager, sender, newArgs)
            else -> showHelp()
        }
    }

    private fun asMember(kothManager: KothManager, sender: Player, args: Array<String>) {
        val runningKoth = kothManager.kothHandler.runningKoth
        val loot: String? = run {
            if (runningKoth?.koth != null) {
                // If there is a running KoTH, we try the linked lootChest, otherwise the
                // KoTH's linked lootChest, otherwise the default lootChest
                runningKoth.lootChest ?:
                    runningKoth.koth.loot.lootName ?:
                    kothManager.configHandler.loot.defaultLoot
            } else {
                // If there is no running KoTH, we try the next upcoming scheduled event
                // We try the lootChest linked to that schedule, otherwise the loot
                // of the linked KoTH, otherwise the default lootChest
                val schedule = kothManager.scheduleHandler.nextEvent ?: return@run null
                schedule.lootChest ?:
                    kothManager.kothHandler.getKoth(schedule.koth)?.loot?.lootName ?:
                    kothManager.configHandler.loot.defaultLoot ?: return@run null
            } ?: return
        }

        val lootObj = loot?.let { kothManager.lootHandler.getLoot(it) }

        if (lootObj == null) {
            MessageBuilder(Lang.COMMAND_LOOT_CMD_NONE_FOUND).send(sender)
            return
        }

        sender.openInventory(lootObj.inventory)
    }

    private fun create(kothManager: KothManager, sender: Player, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth loot create <loot>")
                    .send(sender)
            return
        }

        if (kothManager.lootHandler.getLoot(args[0]) != null) {
            MessageBuilder(Lang.LOOT_ERROR_ALREADYEXISTS).send(sender)
            return
        }

        kothManager.lootHandler.loots.add(Loot(args[0]))
        kothManager.lootHandler.save()

        MessageBuilder(Lang.COMMAND_LOOT_CREATE).send(sender)
    }

    private fun edit(kothManager: KothManager, sender: Player, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth loot edit <loot> (commands)")
                .send(sender)
            return
        }

        val loot = kothManager.lootHandler.getLoot(args[0])
        if (loot == null) {
            MessageBuilder(Lang.LOOT_ERROR_NOTEXIST).send(sender)
            return
        }

        sender.openInventory(loot.inventory)

        MessageBuilder(Lang.COMMAND_LOOT_OPENING)
            .loot(loot.name)
            .send(sender)
    }

    private fun commands(kothManager: KothManager, sender: Player, args: Array<String>) {
        if (!sender.isOp) {
            MessageBuilder(Lang.COMMAND_LOOT_CMD_OPONLY).send(sender)
            return
        }

        fun showHelp() {
            Utils.sendMessage(
                sender, true,
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_TITLE).title("Loot editor").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> add <command>").commandInfo("Add a command").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> list").commandInfo("Show a list of commands").build(),
                MessageBuilder(Lang.COMMAND_GLOBAL_HELP_INFO).command("/koth loot cmd <loot> remove <id>").commandInfo("Remove a command").build()
            )

            if (!kothManager.configHandler.loot.isCmdEnabled) {
                MessageBuilder(Lang.COMMAND_LOOT_CMD_CONFIG_NOT_ENABLED).send(sender)
            }
            return
        }

        if (args.size < 2) {
            showHelp()
            return
        }


        val loot = kothManager.lootHandler.getLoot(args[0])
        if (loot == null) {
            MessageBuilder(Lang.LOOT_ERROR_NOTEXIST).loot(args[0]).send(sender)
            return
        }

        when (args[1].toLowerCase()) {
            "add" -> {
                if (args.size < 3) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth loot cmd <loot> add <command>")
                        .send(sender)
                    return
                }

                loot.commands.add(offsetArgs(args, 2).joinToString(" "))
                kothManager.lootHandler.save()
                MessageBuilder(Lang.COMMAND_LOOT_CMD_CREATED)
                    .loot(loot.name)
                    .send(sender)
            }
            "remove" -> {
                if (args.size < 3) {
                    MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth loot cmd <loot> remove <id>")
                        .send(sender)
                    return
                }

                try {
                    loot.commands.remove(Integer.parseInt(args[2]))
                    kothManager.lootHandler.save()
                    MessageBuilder(Lang.COMMAND_LOOT_CMD_REMOVED).loot(loot.name).send(sender)
                } catch (e: Exception) {
                    MessageBuilder(Lang.COMMAND_LOOT_CMD_NOTANUMBER).send(sender)
                }
            }
            "list" -> {
                MessageBuilder(Lang.COMMAND_LISTS_LOOT_CMD_TITLE).send(sender)
                loot.commands.forEachIndexed { index, item ->
                    MessageBuilder(Lang.COMMAND_LISTS_LOOT_CMD_ENTRY)
                        .id(index)
                        .command(item)
                        .send(sender)
                }
            }
            else -> showHelp()
        }
    }

    private fun list(kothManager: KothManager, sender: Player, args: Array<String>) {
        MessageBuilder(Lang.COMMAND_LISTS_LOOT_TITLE).sender(sender)
        kothManager.lootHandler.loots.forEach {
            MessageBuilder(Lang.COMMAND_LISTS_LOOT_ENTRY)
                .loot(it.name)
                .send(sender)
        }
    }

    private fun remove(kothManager: KothManager, sender: Player, args: Array<String>) {
        if (args.isEmpty()) {
            MessageBuilder(Lang.COMMAND_GLOBAL_USAGE[0] + "/koth loot remove <loot>")
                .send(sender)
            return
        }

        val loot = kothManager.lootHandler.getLoot(args[0])
        if (loot == null) {
            MessageBuilder(Lang.LOOT_ERROR_NOTEXIST).send(sender)
            return
        }

        kothManager.lootHandler.loots.remove(loot)
        kothManager.lootHandler.save()

        MessageBuilder(Lang.COMMAND_LOOT_REMOVE)
            .loot(loot.name)
            .send(sender)
    }

    override val permission: IPerm = Perm.LOOT
    override val commands: Array<String> = arrayOf("loot")
    override val usage: String = "/koth loot"
    override val description: String = "Shows the loot menu"
}
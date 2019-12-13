package subside.plugins.koth.utils

import org.bukkit.command.CommandSender


enum class Perm constructor(perm: String) : IPerm {
    LIST("list"), LOOT("loot"), NEXT("next"), SCHEDULE("schedule"), VERSION("version"), HELP("help"), IGNORE("ignore"), TOP("top");

    private val perm: String = "koth.$perm"

    override fun has(sender: CommandSender): Boolean {
        return sender.hasPermission(perm)
    }


    enum class ALLOW : IPerm {
        ALLOW;
        override fun has(sender: CommandSender) = true
    }

    enum class Admin constructor(perm: String) : IPerm {
        TP("tp"), ADMIN("admin"), CHANGE("change"), CREATE("create"), INFO("info"),
        MODE("mode"), EDIT("edit"), REMOVE("remove"), BYPASS("bypass"),
        HELP("help"), LOOT("loot"), RELOAD("reload"), SCHEDULE("schedule");

        private val perm: String  = "koth.admin.$perm"

        override fun has(sender: CommandSender): Boolean {
            return sender.hasPermission(perm)
        }
    }


    enum class Control constructor(perm: String) : IPerm {
        END("end"), START("start"), STOP("stop");

        private val perm: String  = "koth.control.$perm"

        override fun has(sender: CommandSender): Boolean {
            return sender.hasPermission(perm)
        }
    }
}


interface IPerm {
    fun has(sender: CommandSender): Boolean
}
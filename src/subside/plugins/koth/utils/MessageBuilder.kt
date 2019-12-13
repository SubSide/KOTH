package subside.plugins.koth.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import subside.plugins.koth.KothManager
import subside.plugins.koth.areas.Area
import subside.plugins.koth.areas.Cappable
import subside.plugins.koth.areas.Koth
import subside.plugins.koth.captureentities.Capper
import subside.plugins.koth.gamemodes.TimeObject
import subside.plugins.koth.modules.KothHandler
import subside.plugins.koth.scheduler.Schedule

class MessageBuilder(message: Array<String>) {
    constructor(message: String): this(arrayOf(message))

    private val message: StrObj = StrObj(message.clone())

    private var excluders: MutableCollection<Player>? = null
    private var includers: MutableCollection<Player>? = null


    private class StrObj(val message: Array<String>) {
        fun replaceAll(search: String, replace: String): StrObj {
            for (i in 0..message.size) {
                try {
                    message[i] = message[i].replace(search, replace)
                } catch (e: Exception) {
                    println("$search : $replace")
                    e.printStackTrace()
                }
            }
            return this
        }

        fun build(): Array<String> {
            for (i in 0..message.size) {
                message[i] = ChatColor.translateAlternateColorCodes('&', message[i])
            }
            return message
        }
    }

    fun koth(kothManager: KothManager, koth: String): MessageBuilder {
        val koth = koth.replace("\\$".toRegex(), "\\\\\\$")
        kothManager.kothHandler.getKoth(koth)
            ?.let { koth(kothManager, it) }
            ?: koth(koth)

        return this
    }

    fun koth(kothManager: KothManager, koth: Koth): MessageBuilder {
        koth(koth.name)
        koth.middle?.let { location(kothManager, it) }

        return this
    }

    fun koth(koth: String): MessageBuilder {
        message.replaceAll(
            "%koth%",
            koth.replace("\\\\\\\\".toRegex(), "%5C")
                .replace("([^\\\\])_".toRegex(), "$1 ")
                .replace("\\\\_".toRegex(), "_")
                .replace("%5C".toRegex(), "\\\\\\\\")
        )

        return this
    }

    fun location(kothManager: KothManager, location: Location): MessageBuilder {
        message.replaceAll("%x%", location.blockX.toString())
            .replaceAll("%y%", location.blockY.toString())
            .replaceAll("%z%", location.blockZ.toString())
            .replaceAll("%world%", location.world.name)

        if (kothManager.configHandler.global.worldFilter) {
            include(location.world.players)
        }

        return this
    }

    fun area(kothManager: KothManager, area: Area): MessageBuilder {
        message.replaceAll("%area%", area.name)
        return location(kothManager, area.middle)
    }

    fun area(area: String): MessageBuilder {
        message.replaceAll("%area%", area)
        return this
    }

    fun loot(loot: String): MessageBuilder {
        message.replaceAll("%loot%", loot)
        return this
    }

    fun entry(entry: String): MessageBuilder {
        message.replaceAll("%entry%", entry)
        return this
    }

    fun title(title: String): MessageBuilder {
        message.replaceAll("%title%", title)
        return this
    }

    fun capper(capper: String?): MessageBuilder {
        message.replaceAll("%capper%", capper ?: "None")
        return this
    }

    fun day(day: String): MessageBuilder {
        message.replaceAll("%day%", day)
        return this
    }

    fun lootAmount(lootAmount: Int): MessageBuilder {
        message.replaceAll("%lootamount%", lootAmount.toString())
        return this
    }

    fun time(time: String): MessageBuilder {
        message.replaceAll("%time%", time)
        return this
    }

    fun times(times: String): MessageBuilder {
        message.replaceAll("%times%", times)
        return this
    }

    fun captureTime(captureTime: Int): MessageBuilder {
        message.replaceAll("%ct%", captureTime.toString())
        return this
    }

    fun timeTillNext(schedule: Schedule): MessageBuilder {
        message.replaceAll("%ttn%", TimeObject.getTimeTillNextEvent(schedule))
        return this
    }

    fun id(id: Int): MessageBuilder {
        message.replaceAll("%id%", id.toString())
        return this
    }

    fun date(date: String): MessageBuilder {
        message.replaceAll("%date%", date)
        return this
    }


    fun time(tO: TimeObject): MessageBuilder {
        message.replaceAll("%m%", String.format("%02d", tO.minutesCapped))
        message.replaceAll("%s%", String.format("%02d", tO.secondsCapped))
        message.replaceAll("%ml%", String.format("%02d", tO.minutesLeft))
        message.replaceAll("%sl%", String.format("%02d", tO.secondsLeft))

        return this
    }

    fun maxTime(maxTime: Int): MessageBuilder {
        message.replaceAll("%mt%", "" + maxTime / 60)
        return this
    }

    fun command(command: String): MessageBuilder {
        message.replaceAll("%command%", command)
        return this
    }

    fun commandInfo(commandInfo: String): MessageBuilder {
        message.replaceAll("%command_info%", commandInfo)
        return this
    }


    fun exclude(excluders: Collection<Player>): MessageBuilder {
        if (this.excluders == null)
            this.excluders = ArrayList()

        this.excluders?.addAll(excluders)
        return this
    }

    fun exclude(capper: Capper<*>?, area: Cappable): MessageBuilder {
        if (capper != null)
            this.exclude(capper.getAvailablePlayers(area))

        return this
    }

    fun include(includers: Collection<Player>): MessageBuilder {
        if (this.includers == null)
            this.includers = ArrayList()

        this.includers?.addAll(includers)
        return this
    }



    fun broadcast() {
        val players: MutableCollection<Player> = includers ?: Bukkit.getOnlinePlayers().toMutableList()

        excluders?.let { players.removeAll(it) }

        send(players)

    }

    fun send(capper: Capper<*>?, area: Cappable) {
        if (capper != null)
            send(capper.getAvailablePlayers(area))
    }

    fun send(players: Collection<Player>) {
        val msgs = build().filter { it != "" }
        if (msgs.isEmpty()) return

        players.forEach { player ->
            Utils.sendMsg(player, msgs)
        }
    }

    fun send(sender: CommandSender) {
        Utils.sendMsg(sender, build())
    }

    fun build() = message.build()
}
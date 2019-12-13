package subside.plugins.koth.areas

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import subside.plugins.koth.captureentities.Capper
import subside.plugins.koth.loot.Loot
import subside.plugins.koth.loot.LootHandler

data class Koth(
    var name: String,
    var loot: LootObject = LootObject(),
    var lastWinner: String? = null,
    val areas: MutableList<Area> = ArrayList()
) : Cappable, ConfigurationSerializable {

    override fun isInArea(offlinePlayer: OfflinePlayer?): Boolean {
        val player = offlinePlayer?.player ?: return false
        if (player.isDead) return false

        return areas.any { it.isInArea(player) }
    }

    fun getLootChest(lootHandler: LootHandler, lootChest: String?): Loot? {
        return lootHandler.getLoot(lootChest ?: loot?.lootName)
                ?: lootHandler.getDefaultLoot()
    }

    val middle: Location?
        get() {
            if (areas.isEmpty()) return null

            var X: Double = 0.0
            var Y: Double = 0.0
            var Z: Double = 0.0
            areas.forEach { area ->
                area.middle.let {
                    X += it.x
                    Y += it.y
                    Z += it.z
                }
            }
            return Location(
                    areas[0].middle.world,
                    X / areas.size,
                    Y / areas.size,
                    Z / areas.size
            )
        }

    data class LootObject(
            var lootName: String? = null,
            var lootPos: Location? = null,
            var direction: LootDirection? = null
    ): ConfigurationSerializable {
        val locations: List<Location>
            get() {
                var lootPos2: Location? = lootPos?.clone()
                when (direction) {
                    LootDirection.NORTH -> lootPos2?.add(0.0, 0.0, -1.0)
                    LootDirection.EAST -> lootPos2?.add(1.0, 0.0, 0.0)
                    LootDirection.SOUTH -> lootPos2?.add(0.0, 0.0, 1.0)
                    LootDirection.WEST -> lootPos2?.add(-1.0, 0.0, 0.0)
                    else -> lootPos2 = null
                }

                val list = ArrayList<Location>()
                lootPos?.let { list.add(it) }
                lootPos2?.let { list.add(it) }

                return list
            }

        override fun serialize(): Map<String, Any?> {
            return mapOf(
                "name" to lootName,
                "location" to lootPos?.serialize(),
                "direction" to direction?.text
            )
        }

        companion object {
            @JvmStatic fun deserialize(args: Map<String, Any?>): LootObject {
                return LootObject(
                    args["name"] as? String,
                    (args["location"] as? Map<String, Any?>)?.let { Location.deserialize(it) },
                    LootDirection.values().find { it.text == args["direction"] }
                )
            }
        }
    }

    enum class LootDirection(val text: String) {
        NONE("none"),
        NORTH("north"),
        EAST("east"),
        SOUTH("south"),
        WEST("west")
    }

    override fun serialize(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "loot" to loot.serialize(),
            "lastWinner" to lastWinner,
            "areas" to areas.map { it.serialize() }
        )
    }

    companion object {
        @JvmStatic fun deserialize(args: Map<String, Any>): Koth {
            return Koth(
                args["name"] as String,
                LootObject.deserialize(args["loot"] as Map<String, Any?>),
                args["lastWinner"] as String,
                (args["areas"] as List<Map<String, Any>>).map { Area.deserialize(it) }.toMutableList()
            )
        }
    }
}
package subside.plugins.koth.areas

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import subside.plugins.koth.captureentities.Capper
import subside.plugins.koth.loot.Loot
import subside.plugins.koth.loot.LootHandler

data class Koth(
        var name: String,
        var loot: LootObject? = null,
        var lastWinner: Capper<*>? = null,
        val areas: MutableList<Area> = ArrayList()
) : Cappable {

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
            var lootName: String,
            private var lootPos: Location,
            private var direction: LootDirection
    ) {
        val locations: List<Location>
            get() {
            var lootPos2: Location? = lootPos.clone()
            when (direction) {
                LootDirection.NORTH -> lootPos2?.add(0.0, 0.0, -1.0)
                LootDirection.EAST -> lootPos2?.add(1.0, 0.0, 0.0)
                LootDirection.SOUTH -> lootPos2?.add(0.0, 0.0, 1.0)
                LootDirection.WEST -> lootPos2?.add(-1.0, 0.0, 0.0)
                LootDirection.NONE -> lootPos2 = null
            }

            val list = ArrayList<Location>()
            list.add(lootPos)
            lootPos2?.let { list.add(it) }

            return list
        }
    }

    enum class LootDirection {
        NONE, NORTH, EAST, SOUTH, WEST
    }
}
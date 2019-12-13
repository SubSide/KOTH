package subside.plugins.koth.areas

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.json.simple.JSONObject
import subside.plugins.koth.utils.Utils
import java.lang.Double.max
import java.lang.Double.min

data class Area(
        val name: String,
        private var min: Location,
        private var max: Location
) : Cappable, ConfigurationSerializable {
    val middle: Location
        get() = min.clone().add(max.clone()).multiply(0.5)

    private fun isInAABB(loc: Location): Boolean {
        return min.blockX <= loc.blockX && max.blockX >= loc.blockX &&
                min.blockY <= loc.blockY && max.blockY >= loc.blockY &&
                min.blockZ <= loc.blockZ && max.blockZ >= loc.blockZ
    }

    override fun isInArea(offlinePlayer: OfflinePlayer?): Boolean {
        val player =  offlinePlayer?.player ?: return false

        if (player.isDead || player.world != min.world)
            return false

        return isInAABB(player.location)
    }

    fun setArea(loc1: Location, loc2: Location) {
        min = getMinimum(loc1, loc2)
        max = getMaximum(loc1, loc2)
    }

    override fun serialize(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "min" to min.serialize(),
            "max" to max.serialize()
        )
    }

    companion object {

        @JvmStatic fun deserialize(args: Map<String, Any>): Area {
            return Area(
                args["name"] as String,
                Location.deserialize(args["min"] as Map<String, Any?>),
                Location.deserialize(args["max"] as Map<String, Any?>)
            )
        }

        fun getMinimum(loc1: Location, loc2: Location) =
                Location(loc1.world, min(loc1.x, loc2.x), min(loc1.y, loc2.y), min(loc1.z, loc2.z))

        fun getMaximum(loc1: Location, loc2: Location) =
                Location(loc1.world, max(loc1.x, loc2.x), max(loc1.y, loc2.y), max(loc1.z, loc2.z))

        fun load(obj: JSONObject): Area? {
            val name = obj["name"] as String? // name
            val loc1 = Utils.getLocFromObject(obj["loc1"] as JSONObject?) // loc1
            val loc2 = Utils.getLocFromObject(obj["loc2"] as JSONObject?) // loc2
            return Area(name!!, loc1, loc2)
        }
    }
}
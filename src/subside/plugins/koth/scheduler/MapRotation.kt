package subside.plugins.koth.scheduler

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import subside.plugins.koth.loot.Loot

class MapRotation(
    val rotation: List<String> = ArrayList(),
    var index: Int = 0
)/* : ConfigurationSerializable*/ {
//    override fun serialize(): Map<String, Any> {
//        return mapOf(
//            "rotation" to rotation,
//            "index" to index
//        )
//    }

    fun getNext(): String {
        return rotation[index++ % rotation.size]
    }

//    companion object {
//        @JvmStatic fun deserialize(args: Map<String, Any>): MapRotation {
//            return MapRotation(
//                args["rotation"] as List<String>,
//                args["index"] as Int
//            )
//        }
//    }
}
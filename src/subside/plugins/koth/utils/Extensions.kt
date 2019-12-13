package subside.plugins.koth.utils

import org.bukkit.Location

fun Location.formatted() = "${world.name}, $blockX, $blockY, $blockZ"
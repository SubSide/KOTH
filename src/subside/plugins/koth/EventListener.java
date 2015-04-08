package subside.plugins.koth;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import subside.plugins.koth.area.Area;
import subside.plugins.koth.area.KothHandler;
import subside.plugins.koth.area.SingleLootChest;
import subside.plugins.koth.scoreboard.ScoreboardHandler;

public class EventListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent e) {
		if (e.getPlayer() instanceof Player) {
			if (e.getInventory().getHolder() instanceof Chest) {
				Chest chest = (Chest) e.getInventory().getHolder();
				Location loc = chest.getLocation();
				for (Area area : KothHandler.getAvailableAreas()) {
					Location vec = area.getLootPos();
					if (loc.getWorld() == vec.getWorld() && loc.getBlockX() == vec.getBlockX() && loc.getBlockY() == vec.getBlockY() && loc.getBlockZ() == vec.getBlockZ()) {
						if (!area.getLastWinner().equalsIgnoreCase(e.getPlayer().getName())) {
							e.setCancelled(true);
						}

					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Location loc = e.getBlock().getLocation();
		for (Area area : KothHandler.getAvailableAreas()) {
			Location vec = area.getLootPos();
			if (loc.getWorld() == vec.getWorld() && loc.getBlockX() == vec.getBlockX() && loc.getBlockY() == vec.getBlockY() && loc.getBlockZ() == vec.getBlockZ()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Location loc = e.getBlock().getLocation();
		for (Area area : KothHandler.getAvailableAreas()) {
			Location vec = area.getLootPos();
			if (vec == null) continue;
			if (loc.getWorld() == vec.getWorld() && loc.getBlockX() == vec.getBlockX() && loc.getBlockY() == vec.getBlockY() && loc.getBlockZ() == vec.getBlockZ()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryChange(InventoryClickEvent event) {
		if (!Utils.hasPerms((Player) event.getWhoClicked())) {
			if (event.getInventory().equals(SingleLootChest.getInventory())) {
				event.setCancelled(true);
				return;

			}

			for (Area area : KothHandler.getAvailableAreas()) {
				if (event.getInventory().equals(area.getInventory())) {
					event.setCancelled(true);
					return;
				}
			}
		}

	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (Utils.hasPerms((Player) event.getPlayer())) {
			if(ConfigHandler.getCfgHandler().getSingleLootChest()){
				if(SingleLootChest.getInventory().equals(event.getInventory())){
					KothLoader.save();
					return;
				}
			}
			for (Area area : KothHandler.getAvailableAreas()) {
				if (event.getInventory().equals(area.getInventory())) {
					KothLoader.save();
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ScoreboardHandler.clearPlayer(event.getPlayer());
	}
}

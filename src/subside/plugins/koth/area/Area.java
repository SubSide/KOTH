package subside.plugins.koth.area;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Koth;
import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;

public class Area {
	private String name;
	private Location min;
	private Location mid;
	private Location max;
	private Location lootPos = null;
	private String lastWinner;
	private Inventory lootInv;

	public Area(String name, Location min, Location max) {
		this.name = name;
		this.min = getMinimum(min, max);
		this.max = getMaximum(min, max);
		calculateMiddle();
		this.lootInv = Bukkit.createInventory(null, 54, new MessageBuilder(Lang.KOTH_LOOT_CHEST).area(name).build());
	}

	private Location getMinimum(Location loc1, Location loc2) {
		return new Location(loc1.getWorld(), (loc1.getX() < loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() < loc2.getY()) ? loc1.getY() : loc2.getY(), (loc1.getZ() < loc2.getZ()) ? loc1.getZ() : loc2.getZ());
	}

	private Location getMaximum(Location loc1, Location loc2) {
		return new Location(loc1.getWorld(), (loc1.getX() > loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() > loc2.getY()) ? loc1.getY() : loc2.getY(), (loc1.getZ() > loc2.getZ()) ? loc1.getZ() : loc2.getZ());
	}
	
	private void calculateMiddle(){
		this.mid = min.clone().add(max.clone()).multiply(0.5);
	}
	

	private boolean isInAABB(Location pos, Location pos2, Location pos3) {
		Location min = getMinimum(pos2, pos3);
		Location max = getMaximum(pos2, pos3);
		if (min.getBlockX() <= pos.getBlockX() && max.getBlockX() >= pos.getBlockX() && min.getBlockY() <= pos.getBlockY() && max.getBlockY() >= pos.getBlockY() && min.getBlockZ() <= pos.getBlockZ() && max.getBlockZ() >= pos.getBlockZ()) {
			return true;
		}
		return false;
	}

	public boolean isInArea(OfflinePlayer oPlayer) {
		if (!oPlayer.isOnline()) {
			return false;
		}
		Player player = oPlayer.getPlayer();
		if (player == null) {
			return false;
		}
		
		if(player.isDead()){
			return false;
		}

		if (player.getWorld() == min.getWorld()) {
			Location loc = player.getLocation();
			if (isInAABB(loc, min, max)) {
				return true;
			}
		}
		return false;
	}

	public void setLootPos(Location pos) {
		this.lootPos = pos;
	}

	public void setArea(Location min, Location max) {
		this.min = getMinimum(min, max);
		this.max = getMaximum(min, max);
		calculateMiddle();
	}

	public Location getMin() {
		return min;
	}

	public Location getMax() {
		return max;
	}
	
	public Location getMiddle(){
		return mid;
	}

	public String getName() {
		return name;
	}

	public Location getLootPos() {
		return lootPos;
	}

	public void setLastWinner(String player) {
		lastWinner = player;
	}

	public String getLastWinner() {
		return lastWinner == null ? "" : lastWinner;
	}

	public void createLootChest() {
		try {
			lootPos.getBlock().setType(Material.CHEST);
			if (lootPos.getBlock().getState() instanceof Chest) {
				Chest chest = (Chest) lootPos.getBlock().getState();
				ItemStack[] loot;
				if (ConfigHandler.getCfgHandler().getSingleLootChest()) {
					loot = SingleLootChest.getInventory().getContents();
				} else {
					loot = this.lootInv.getContents();
				}

				ArrayList<ItemStack> usableLoot = new ArrayList<ItemStack>();
				for (ItemStack stack : loot) {
					if (stack != null) {
						usableLoot.add(stack.clone());
					}
				}
				if (usableLoot.size() < 1) return;

				Inventory inv = chest.getInventory();
				if (ConfigHandler.getCfgHandler().getRandomizeLoot()) {
					for (int x = 0; x < ConfigHandler.getCfgHandler().getLootAmount(); x++) {
						inv.setItem(x, usableLoot.get(new Random().nextInt(usableLoot.size())).clone());
					}
				} else {
					for (int x = 0; x < usableLoot.size(); x++) {
						inv.setItem(x, usableLoot.get(x).clone());
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Inventory getInventory() {
		return lootInv;
	}

	public void setInventory(Inventory inv) {
		lootInv = inv;
	}

	public void removeLootChest() {
		final Area area = this;
		Bukkit.getScheduler().runTask(Koth.getPlugin(), new Runnable() {
			public void run() {
				if (area.getLootPos() != null) {
					if (area.getLootPos().getBlock() != null) {
						if (area.getLootPos().getBlock().getState() instanceof Chest) {
							Chest chest = (Chest) area.getLootPos().getBlock().getState();
							Inventory inv = chest.getInventory();
							inv.clear();
						}
						area.getLootPos().getBlock().setType(Material.AIR);
					}
				}
			}
		});

	}

}

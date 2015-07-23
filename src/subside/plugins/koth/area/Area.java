package subside.plugins.koth.area;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

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
import subside.plugins.koth.events.KothChestCreationEvent;

public class Area {
    private @Getter String name;
    private @Getter Location min;
    private @Getter Location middle;
    private @Getter Location max;
    private @Getter @Setter Location lootPos = null;
    private String lastWinner;
    private @Getter @Setter Inventory inventory;

    public Area(String name, Location min, Location max) {
        this.name = name;
        this.min = getMinimum(min, max);
        this.max = getMaximum(min, max);
        calculateMiddle();
        String title = new MessageBuilder(Lang.KOTH_LOOT_CHEST).area(name).build();
        if (title.length() > 32) title = title.substring(0, 32);
        this.inventory = Bukkit.createInventory(null, 54, title);
    }

    private Location getMinimum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), (loc1.getX() < loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() < loc2.getY()) ? loc1.getY() : loc2.getY(), (loc1.getZ() < loc2.getZ()) ? loc1.getZ() : loc2.getZ());
    }

    private Location getMaximum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), (loc1.getX() > loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() > loc2.getY()) ? loc1.getY() : loc2.getY(), (loc1.getZ() > loc2.getZ()) ? loc1.getZ() : loc2.getZ());
    }

    private void calculateMiddle() {
        this.middle = min.clone().add(max.clone()).multiply(0.5);
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

        if (player.isDead()) {
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

    public void setArea(Location min, Location max) {
        this.min = getMinimum(min, max);
        this.max = getMaximum(min, max);
        calculateMiddle();
    }

    public void setLastWinner(String player) {
        lastWinner = player;
    }

    public String getLastWinner() {
        return lastWinner == null ? "" : lastWinner;
    }

    public void createLootChest() {
        try {
            ItemStack[] loot;
            if (ConfigHandler.getCfgHandler().isSingleLootChest()) {
                loot = SingleLootChest.getInventory().getContents();
            } else {
                loot = this.inventory.getContents();
            }

            ArrayList<ItemStack> usableLoot = new ArrayList<ItemStack>();
            for (ItemStack stack : loot) {
                if (stack != null) {
                    usableLoot.add(stack.clone());
                }
            }
            if (usableLoot.size() < 1) return;

            Inventory inv = Bukkit.createInventory(null, 27);
            if (ConfigHandler.getCfgHandler().isRandomizeLoot()) {
                for (int x = 0; x < ConfigHandler.getCfgHandler().getLootAmount(); x++) {
                    if (usableLoot.size() < 1) {
                        break;
                    }

                    // UseItemsMultipleTimes
                    ItemStack uLoot = usableLoot.get(new Random().nextInt(usableLoot.size()));
                    if (!ConfigHandler.getCfgHandler().isUseItemsMultipleTimes()) {
                        usableLoot.remove(uLoot);
                    }

                    // Randomize amount of loot or not?
                    if (ConfigHandler.getCfgHandler().isRandomizeAmountLoot()) {
                        int amount = uLoot.getAmount();
                        ItemStack stack = uLoot.clone();
                        stack.setAmount(new Random().nextInt(amount) + 1);
                        inv.setItem(x, stack);
                    } else {
                        inv.setItem(x, uLoot.clone());
                    }
                }
            } else {
                for (int x = 0; x < usableLoot.size(); x++) {
                    inv.setItem(x, usableLoot.get(x).clone());
                }
            }
            KothChestCreationEvent event = new KothChestCreationEvent(this, inv.getContents());
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                lootPos.getBlock().setType(Material.CHEST);
                if (lootPos.getBlock().getState() instanceof Chest) {
                    Chest chest = (Chest) lootPos.getBlock().getState();

                    chest.getInventory().setContents(event.getLoot());

                    if (ConfigHandler.getCfgHandler().getRemoveLootAfterSeconds() <= 0) {
                        Bukkit.getScheduler().runTaskLater(Koth.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                removeLootChest();
                            }
                        }, ConfigHandler.getCfgHandler().getRemoveLootAfterSeconds() * 20);
                    }

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLootChest() {
        final Area area = this;
        Bukkit.getScheduler().runTask(Koth.getPlugin(), new Runnable() {
            public void run() {
                if (area.getLootPos() != null) {
                    if (area.getLootPos().getBlock() != null) {
                        if (!ConfigHandler.getCfgHandler().isDropLootOnRemoval()) {
                            if (area.getLootPos().getBlock().getState() instanceof Chest) {
                                Chest chest = (Chest) area.getLootPos().getBlock().getState();
                                Inventory inv = chest.getInventory();
                                inv.clear();
                            }
                        }
                        area.getLootPos().getBlock().setType(Material.AIR);
                    }
                }
            }
        });

    }

}

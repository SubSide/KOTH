package subside.plugins.koth.adapter;

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
import org.json.simple.JSONObject;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;
import subside.plugins.koth.SingleLootChest;
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

    private boolean isInAABB(Location loc, Location pos1, Location pos2) {
        Location min = getMinimum(pos1, pos2);
        Location max = getMaximum(pos1, pos2);
        if (min.getBlockX() <= loc.getBlockX() && max.getBlockX() >= loc.getBlockX() && min.getBlockY() <= loc.getBlockY() && max.getBlockY() >= loc.getBlockY() && min.getBlockZ() <= loc.getBlockZ() && max.getBlockZ() >= loc.getBlockZ()) {
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

        if (player.getWorld() != min.getWorld()) {
            return false;
        }
        
        Location loc = player.getLocation();
        if (isInAABB(loc, min, max)) {
            return true;
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

    public void createLootChest(int lootAmount) {
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
                for (int x = 0; x < lootAmount; x++) {
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

            if (ConfigHandler.getCfgHandler().isInstantLoot()) {
                Player player = Bukkit.getPlayer(this.lastWinner);
                if (player != null) {
                    ArrayList<ItemStack> dropItems = new ArrayList<>();
                    for (ItemStack is : inv.getContents()) {
                        if (is == null) continue;
                        if (player.getInventory().addItem(is).size() > 0) {
                            dropItems.add(is);
                        }
                    }
                    if (dropItems.size() > 0) {
                        new MessageBuilder(Lang.KOTH_WON_DROPPING_ITEMS).buildAndSend(player);
                        for (ItemStack item : dropItems) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                    }
                } else {
                    for (ItemStack item : inv.getContents()) {
                        if (item == null) continue;
                        middle.getWorld().dropItemNaturally(middle, item);
                    }
                }

            } else {
                KothChestCreationEvent event = new KothChestCreationEvent(this, inv.getContents());

                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                lootPos.getBlock().setType(Material.CHEST);
                if (!(lootPos.getBlock().getState() instanceof Chest)) {
                    return;
                }

                Chest chest = (Chest) lootPos.getBlock().getState();
                chest.getInventory().setContents(inv.getContents());

                if (ConfigHandler.getCfgHandler().getRemoveLootAfterSeconds() < 1) {
                    return;
                }

                Bukkit.getScheduler().runTaskLater(KothPlugin.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        removeLootChest();
                    }
                }, ConfigHandler.getCfgHandler().getRemoveLootAfterSeconds() * 20);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLootChest() {
        final Area area = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
            public void run() {
                if (area.getLootPos() == null) {
                    return;
                }

                if (area.getLootPos().getBlock() == null) {
                    return;
                }

                if (!ConfigHandler.getCfgHandler().isDropLootOnRemoval()) {
                    if (area.getLootPos().getBlock().getState() instanceof Chest) {
                        Chest chest = (Chest) area.getLootPos().getBlock().getState();
                        Inventory inv = chest.getInventory();
                        inv.clear();
                    }
                }
                area.getLootPos().getBlock().setType(Material.AIR);

            }

        });

    }
    
    public JSONObject saveObject(){
        JSONObject obj = new JSONObject();
    }
    
    public static Area loadObject(JSONObject obj){
        
    }

}

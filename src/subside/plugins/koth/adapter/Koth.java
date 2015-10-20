package subside.plugins.koth.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.events.KothChestCreationEvent;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Utils;

public class Koth {
    private @Getter @Setter String name;
    private @Getter @Setter Location lootPos = null;
    private @Setter String loot = null;
    private String lastWinner;
    private @Getter List<Area> areas = new ArrayList<>();

    public Koth(String name) {
        this.name = name;
    }
    
    public String getLoot(){
        if(loot != null && !loot.equalsIgnoreCase("")){
            return loot;
        }
        return ConfigHandler.getCfgHandler().getDefaultLoot();
    }
    
    public Location getMiddle(){
        if(areas.size() > 0){
            return areas.get(0).getMiddle();
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    public void setLastWinner(String player) {
        lastWinner = player;
    }

    public String getLastWinner() {
        return lastWinner == null ? "" : lastWinner;
    }
    
    public boolean isInArea(OfflinePlayer oPlayer){
        for(Area area : areas){
            if(area.isInArea(oPlayer)){
                return true;
            }
        }
        return false;
    }

    public void createLootChest(int lootAmount, String lootChest) {
        try {
            ItemStack[] lt;
            if(lootChest == null){
                lt = KothHandler.getLoot(getLoot()).getInventory(name).getContents();
            } else {
                lt = KothHandler.getLoot(lootChest).getInventory(name).getContents();
            }

            List<ItemStack> usableLoot = new ArrayList<>();
            for (ItemStack stack : lt) {
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
                    List<ItemStack> dropItems = new ArrayList<>();
                    for (ItemStack is : inv.getContents()) {
                        if (is == null) continue;
                        if (player.getInventory().addItem(is).size() > 0) {
                            dropItems.add(is);
                        }
                    }
                    if (dropItems.size() > 0) {
                        new MessageBuilder(Lang.KOTH_PLAYING_WON_DROPPING_ITEMS).buildAndSend(player);
                        for (ItemStack item : dropItems) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                    }
                } else {
                    for (ItemStack item : inv.getContents()) {
                        if (item == null) continue;
                        getMiddle().getWorld().dropItemNaturally(getMiddle(), item);
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
        final Koth koth = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
            public void run() {
                if (koth.getLootPos() == null) {
                    return;
                }

                if (koth.getLootPos().getBlock() == null) {
                    return;
                }

                if (!ConfigHandler.getCfgHandler().isDropLootOnRemoval()) {
                    if (koth.getLootPos().getBlock().getState() instanceof Chest) {
                        Chest chest = (Chest) koth.getLootPos().getBlock().getState();
                        Inventory inv = chest.getInventory();
                        inv.clear();
                    }
                }
                koth.getLootPos().getBlock().setType(Material.AIR);

            }

        });

    }
    
    public Area getArea(String ar){
        for(Area area : areas){
            if(area.getName().equalsIgnoreCase(ar)){
                return area;
            }
        }
        return null;
    }
//    
//    private @Getter String name;
//    private @Getter @Setter Location lootPos = null;
//    private String lastWinner;
//    private @Getter List<Area> areas = new ArrayList<>();
    
    public static Koth load(JSONObject obj){
        Koth koth = new Koth((String)obj.get("name")); //name
        
        if(obj.containsKey("lastWinner")){
            koth.lastWinner = (String)obj.get("lastWinner"); //lastwinner
        }
        
        if(obj.containsKey("loot")){
            JSONObject lootObj = (JSONObject)obj.get("loot");
            if(lootObj.containsKey("position")){
                koth.lootPos = Utils.getLocFromObject((JSONObject)lootObj.get("position")); //lootpos
            }
            if(lootObj.containsKey("name")){
                koth.loot = (String)lootObj.get("name"); //loot
            }
        }

        if(obj.containsKey("areas")){
            JSONArray areaz = (JSONArray)obj.get("areas");
            Iterator<?> it = areaz.iterator();
            while(it.hasNext()){
                koth.areas.add(Area.load((JSONObject)it.next())); //areas
            }
        }
        
        return koth;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("name", this.name); //name
        
        if(this.lastWinner != null){
            obj.put("lastWinner", this.lastWinner); //lastwinner
        }
        
        if(this.lootPos != null || (this.loot != null && !this.loot.equalsIgnoreCase(""))){
            JSONObject lootObj = new JSONObject();
            if(this.lootPos != null){
                lootObj.put("position", Utils.createLocObject(this.lootPos)); //lootpos
            }
            if(this.loot != null && !this.loot.equalsIgnoreCase("")){
                lootObj.put("name", this.loot); // loot
            }
            obj.put("loot", lootObj);
        }
        
        if(areas.size() > 0){
            JSONArray areaz = new JSONArray();
            for(Area area : areas){
                areaz.add(area.save());
            }
            obj.put("areas", areaz);
        }
        
        return obj;
    }

}

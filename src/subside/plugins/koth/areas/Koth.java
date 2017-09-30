package subside.plugins.koth.areas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.events.KothChestCreationEvent;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.loot.Loot;
import subside.plugins.koth.modules.ConfigHandler;
import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.JSONSerializable;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Utils;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class Koth implements Capable, JSONSerializable<Koth> {
    private @Getter @Setter String name;
    private @Getter @Setter Location lootPos = null;
    private @Getter @Setter LootDirection secondLootDirection = LootDirection.NONE;
    private @Setter String loot = null;
    private Capper<?> lastWinner;
    private @Getter List<Area> areas = new ArrayList<>();
    
    private @Getter KothHandler kothHandler;

    public Koth(KothHandler kothHandler, String name) {
        this.kothHandler = kothHandler;
        this.name = name;
    }

    /** Returns the loot that should be used for the chest.<br />
     *  Will return the default config setting if no loot has been set.
     * 
     * @return          the loot chest that should be used to create the chest
     */
    public String getLoot(){
        if(loot != null && !loot.equalsIgnoreCase("")){
            return loot;
        }
        return kothHandler.getPlugin().getConfigHandler().getLoot().getDefaultLoot();
    }
    
    /** Return a RunningKoth linked to this KoTH if there is running one
     * 
     * @return          the RunningKoth linked to this KoTH, null if none
     */
    public RunningKoth getRunningKoth(){
        for(RunningKoth rKoth : kothHandler.getRunningKoths()){
            if(rKoth.getKoth() == this){
                return rKoth;
            }
        }
        return null;
    }
    
    
    /** Is the current KoTH running?
     * 
     * @return          true if the KoTH is running
     */
    public boolean isRunning(){
        return getRunningKoth() != null;
    }
    

    /** Get the middle of the KoTH
     * 
     * @return          the middle of the koth
     */
    public Location getMiddle(){
        if(areas.size() > 0){
            return areas.get(0).getMiddle();
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }


    /** Change the Last winner of this KoTH
     * 
     * @param capper    The new winner
     */
    public void setLastWinner(Capper<?> capper) {
        lastWinner = capper;
    }


    /** Get the last winner of this KoTH
     * 
     * @return          the last winner of the KoTH
     */
    public Capper<?> getLastWinner() {
        return lastWinner;
    }

    /** Checks if the player is inside any area of the KoTH
     * 
     * @param oPlayer   OfflinePlayer to check
     * @return          true if player is in any KoTH area
     */
    @Override
    public boolean isInArea(OfflinePlayer oPlayer){
        if(oPlayer == null || !oPlayer.isOnline() || oPlayer.getPlayer() == null) {
            return false;
        }
        Player player = oPlayer.getPlayer();

        if (player.isDead()) {
            return false;
        }
        
        for(Area area : areas){
            if(area.isInArea(player)){
                return true;
            }
        }
        return false;
    }
    
    public Loot getLootChest(String lootChest){
        Loot loot = kothHandler.getPlugin().getLootHandler().getLoot((lootChest == null)?getLoot():lootChest);
        
        if(loot == null){
            loot = kothHandler.getPlugin().getLootHandler().getLoot(kothHandler.getPlugin().getConfigHandler().getLoot().getDefaultLoot()); 
        }
        
        return loot;
    }

    /** Creates the loot chest and trigger the commands
     * 
     * @param lootAmount        The amount of loot that should be created
     * @param lootChst          The lootChest to use
     */
    public void triggerLoot(int lootAmount, String lootChst) {
        ConfigHandler cfgHandler = kothHandler.getPlugin().getConfigHandler();
        try {
            Loot lootChest = getLootChest(lootChst);
            
            if(lootChest == null) return;
            
            lootChest.triggerCommands(this, this.lastWinner);
            
            if(lootChest.getInventory().getContents().length < 1){
                return;
            }
            
            ItemStack[] lt = lootChest.getInventory().getContents();

            List<ItemStack> usableLoot = new ArrayList<>();
            for (ItemStack stack : lt) {
                if (stack != null) {
                    usableLoot.add(stack.clone());
                }
            }
            if (usableLoot.size() < 1) return;

            Inventory inv = Bukkit.createInventory(null, 54);
            if (cfgHandler.getLoot().isRandomizeLoot()) {
                for (int x = 0; x < lootAmount; x++) {
                    if (usableLoot.size() < 1) {
                        break;
                    }

                    // UseItemsMultipleTimes
                    ItemStack uLoot = usableLoot.get(new Random().nextInt(usableLoot.size()));
                    if (!cfgHandler.getLoot().isUseItemsMultipleTimes()) {
                        usableLoot.remove(uLoot);
                    }

                    // Randomize amount of loot or not?
                    if (cfgHandler.getLoot().isRandomizeStackSize()) {
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

            if (cfgHandler.getLoot().isInstantLoot()) {
                Collection<Player> players = this.lastWinner.getAvailablePlayers(this).stream()
                        .limit(cfgHandler.getLoot().isRewardEveryone()?999:1)
                        .collect(Collectors.toSet());
                if(players.size() > 0){
                    for (Player player : players) {
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

                if(getSideChest() != null){
                    getSideChest().getBlock().setType(Material.CHEST);
                }

                if (!(lootPos.getBlock().getState() instanceof Chest || lootPos.getBlock().getState() instanceof DoubleChest)) {
                    return;
                }
                InventoryHolder chest2 = (InventoryHolder) lootPos.getBlock().getState();
                chest2.getInventory().setContents(Arrays.copyOf(inv.getContents(), chest2.getInventory().getSize()));

                if (kothHandler.getPlugin().getConfigHandler().getLoot().getRemoveLootAfterSeconds() < 1) {
                    return;
                }

                Bukkit.getScheduler().runTaskLater(kothHandler.getPlugin(), this::removeLootChest
                , kothHandler.getPlugin().getConfigHandler().getLoot().getRemoveLootAfterSeconds() * 20);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Removes the lootchest
     * 
     */
    public void removeLootChest() {
        final Koth koth = this;
        Bukkit.getScheduler().runTask(kothHandler.getPlugin(), () -> {
            if (koth.getLootPos() == null) {
                return;
            }

            if (koth.getLootPos().getBlock() == null) {
                return;
            }

            if (!kothHandler.getPlugin().getConfigHandler().getLoot().isDropLootOnRemoval()) {
                if (koth.getLootPos().getBlock().getState() instanceof Chest) {
                    Chest chest = (Chest) koth.getLootPos().getBlock().getState();
                    Inventory inv = chest.getInventory();
                    inv.clear();
                }
            }
            koth.getLootPos().getBlock().setType(Material.AIR);
            if(getSideChest() != null){
                getSideChest().getBlock().setType(Material.AIR);
            }

        });
    }

    /** Gets an area by name
     * 
     * @param ar      The area name
     * @return          The area object
     */
    public Area getArea(String ar){
        for(Area area : areas){
            if(area.getName().equalsIgnoreCase(ar)){
                return area;
            }
        }
        return null;
    }

    /**
     * Get the location of the side chest
     * @return the location
     */
    private Location getSideChest(){
        if(secondLootDirection == LootDirection.NONE)
            return null;

        Location lootPos2 = lootPos.clone();
        switch(secondLootDirection){
            case NORTH:
                lootPos2.add(0, 0, -1);
                break;
            case EAST:
                lootPos2.add(1, 0, 0);
                break;
            case SOUTH:
                lootPos2.add(0, 0, 1);
                break;
            case WEST:
                lootPos2.add(-1, 0, 0);
                break;
        }

        return lootPos2;
    }

    /**
     * A simple function to check if the given location is the lootchest of this KoTH
     * @param loc location to check
     * @return true if the given location is the lootchest
     */
    public boolean isLootChest(Location loc){
        if (lootPos != null
                && loc.getWorld() == lootPos.getWorld()
                && loc.getBlockX() == lootPos.getBlockX()
                && loc.getBlockY() == lootPos.getBlockY()
                && loc.getBlockZ() == lootPos.getBlockZ())
            return true;

        Location side = getSideChest();
        if (side != null
                && loc.getWorld() == side.getWorld()
                && loc.getBlockX() == side.getBlockX()
                && loc.getBlockY() == side.getBlockY()
                && loc.getBlockZ() == side.getBlockZ())
            return true;

        return false;
    }
  
    public Koth load(JSONObject obj){
        this.name = (String)obj.get("name"); //name
        
        if(obj.containsKey("lastWinner")){
            try {
                this.lastWinner = Capper.load(kothHandler.getPlugin().getCaptureTypeRegistry(), (JSONObject)obj.get("lastWinner")); //lastwinner
            } catch(Exception e){}
        }
        
        if(obj.containsKey("loot")){
            JSONObject lootObj = (JSONObject)obj.get("loot");
            if(lootObj.containsKey("position")){
                this.lootPos = Utils.getLocFromObject((JSONObject)lootObj.get("position")); //lootpos
                try {
                    if(lootObj.containsKey("secondLootDirection"))
                        this.secondLootDirection = LootDirection.valueOf((String)lootObj.get("secondLootDirection"));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(lootObj.containsKey("name")){
                this.loot = (String)lootObj.get("name"); //loot
            }
        }

        if(obj.containsKey("areas")){
            JSONArray areaz = (JSONArray)obj.get("areas");
            Iterator<?> it = areaz.iterator();
            while(it.hasNext()){
                this.areas.add(Area.load((JSONObject)it.next())); //areas
            }
        }
        
        return this;
    }

    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("name", this.name); //name
        
        if(this.lastWinner != null){
            obj.put("lastWinner", this.lastWinner.save()); //lastwinner
        }
        
        if(this.lootPos != null || (this.loot != null && !this.loot.equalsIgnoreCase(""))){
            JSONObject lootObj = new JSONObject();
            if(this.lootPos != null){
                lootObj.put("position", Utils.createLocObject(this.lootPos)); //lootpos
                lootObj.put("secondLootDirection", this.secondLootDirection.toString());
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

    public enum LootDirection {
        NONE, NORTH, EAST, SOUTH, WEST
    }
}

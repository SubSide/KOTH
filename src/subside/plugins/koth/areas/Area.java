package subside.plugins.koth.areas;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.utils.Utils;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class Area implements Capable {

    private @Getter String name;
    private @Getter Location min;
    private @Getter Location middle;
    private @Getter Location max;


    public Area(String name, Location loc1, Location loc2){
        this.name = name;
        this.min = getMinimum(loc1, loc2);
        this.max = getMaximum(loc1, loc2);
        calculateMiddle();
    }

    /** Overwrites the area
     * 
     * @param loc1      First location
     * @param loc2      Second location
     */
    public void setArea(Location loc1, Location loc2){
        this.min = getMinimum(loc1, loc2);
        this.max = getMaximum(loc1, loc2);
        calculateMiddle();
    }

    private Location getMinimum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), Math.min(loc1.getX(), loc2.getX()), Math.min(loc1.getY(), loc2.getY()), Math.min(loc1.getZ(), loc2.getZ()));
    }

    private Location getMaximum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), Math.max(loc1.getX(), loc2.getX()), Math.max(loc1.getY(), loc2.getY()), Math.max(loc1.getZ(), loc2.getZ()));
    }
    
    private void calculateMiddle() {
        this.middle = min.clone().add(max.clone()).multiply(0.5);
    }

    private boolean isInAABB(Location loc) {
        return
                min.getBlockX() <= loc.getBlockX() && max.getBlockX() >= loc.getBlockX() &&
                        min.getBlockY() <= loc.getBlockY() && max.getBlockY() >= loc.getBlockY() &&
                        min.getBlockZ() <= loc.getBlockZ() && max.getBlockZ() >= loc.getBlockZ();
    }

    

    /** Checks if the player is inside the area
     * 
     * @param oPlayer    Player to check
     * @return          true if player is in the area
     */
    public boolean isInArea(OfflinePlayer oPlayer) {
        if(oPlayer == null || !oPlayer.isOnline() || oPlayer.getPlayer() == null) {
            return false;
        }
        Player player = oPlayer.getPlayer();

        if (player.isDead()) {
            return false;
        }
        
        if (player.getWorld() != min.getWorld()) {
            return false;
        }
        
        Location loc = player.getLocation();

        return isInAABB(loc);
    }
    
    public static Area load(JSONObject obj){
        String name = (String)obj.get("name"); // name
        Location loc1 = Utils.getLocFromObject((JSONObject)obj.get("loc1")); // loc1
        Location loc2 = Utils.getLocFromObject((JSONObject)obj.get("loc2")); // loc2
        return new Area(name, loc1, loc2);
    }

    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("name", name); // name
        obj.put("loc1", Utils.createLocObject(min)); // loc1
        obj.put("loc2", Utils.createLocObject(max)); // loc2
        
        return obj;
    }
}

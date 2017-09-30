package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.areas.Capable;

public abstract class Capper<T> {
    protected @Getter CaptureTypeRegistry captureTypeRegistry;
    private @Getter String uniqueClassIdentifier;
    private @Getter T object;
    
    public Capper(CaptureTypeRegistry captureTypeRegistry, String uniqueClassIdentifier, T object){
        this.captureTypeRegistry = captureTypeRegistry;
        this.uniqueClassIdentifier = uniqueClassIdentifier;
        this.object = object;
    }
    
    /**
     * This should return a string which uniquely identifies the object
     * This is later used with the constructor to deserialize the object
     * 
     * @return the unique object identifier
     */
    public abstract String getUniqueObjectIdentifier();
    
    /**
     * This is to check if the given Player is part of the object.
     * 
     * @param oPlayer the Player to check
     * @return true if the Player is part of the object
     */
    public abstract boolean isInOrEqualTo(OfflinePlayer oPlayer);
    
    /**
     * This should return the display name of the object.
     * For players this should return the player name, for factions for example the faction name.
     * This doesn't need to be unique.
     * 
     * @return The display name of the object.
     */
    public abstract String getName();
    
    /**
     * Returns a collection of players that are currently online.
     * Used to do area checks and such
     * 
     * @return a collection of online players.
     */
    public abstract Collection<Player> getAllOnlinePlayers();

    /**
     * This should return all the avaiable players that are currently inside the area
     * 
     * @param area the area to be checked
     * @return a Collection of players that are currently standing in the area
     */
    public Collection<Player> getAvailablePlayers(Capable area){
        Collection<Player> list = new ArrayList<>();
        
        for(Player player : getAllOnlinePlayers()){
            if(area.isInArea(player)){
                list.add(player);
            }
        }
        
        return list;
    }

    /**
     * This is used to check if the Capper is still able to hold the point.
     * If this returns false the Capper most likely loses the point
     * 
     * @param cap The Capable area to check
     * @return true if the Capper is still standing on the point
     */
    public boolean areaCheck(Capable cap) {
        for(Player player : getAllOnlinePlayers()){
            if(cap.isInArea(player) && captureTypeRegistry.getPlugin().getHookManager().canCap(player)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * This is the JSON load function, this is used to load a Capper object from JSON
     * 
     * @param captureTypeRegistry The capture registry
     * @param obj the JSON Object
     * @return the actual Capper object
     */
    @SuppressWarnings("rawtypes")
    public static Capper load(CaptureTypeRegistry captureTypeRegistry, JSONObject obj){
        return captureTypeRegistry.getCapperFromType((String)obj.get("capperType"), (String)obj.get("uniqueId"));
    }

    /**
     * This is the JSON save function, this is used to save a capper object to JSON
     * 
     * @return a Serialized JSON Object
     */
    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("capperType", getUniqueClassIdentifier()); // Class identifier
        obj.put("uniqueId", getUniqueObjectIdentifier()); // Object identifier
        return obj;
    }
}

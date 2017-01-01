package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.areas.Capable;

public abstract class Capper {
    protected @Getter CaptureTypeRegistry captureTypeRegistry;
    
    public Capper(CaptureTypeRegistry captureTypeRegistry){
        this.captureTypeRegistry = captureTypeRegistry;
    }
    
    public abstract String getUniqueClassIdentifier();
    public abstract String getUniqueObjectIdentifier();
    public abstract boolean isInOrEqualTo(OfflinePlayer oPlayer);
    public abstract String getName();
    public abstract Object getObject();
    public abstract boolean areaCheck(Capable cap);
    public abstract List<Player> getAllOnlinePlayers();

    
    public List<Player> getAvailablePlayers(Capable area){
        List<Player> list = new ArrayList<Player>();
        
        for(Player player : getAllOnlinePlayers()){
            if(area.isInArea(player)){
                list.add(player);
            }
        }
        
        return list;
    }
    
    
    public static Capper load(CaptureTypeRegistry captureTypeRegistry, JSONObject obj){
        return captureTypeRegistry.getCapperFromType((String)obj.get("capperType"), (String)obj.get("uniqueId"));
    }

    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("capperType", getUniqueClassIdentifier()); // Class identifier
        obj.put("uniqueId", getUniqueObjectIdentifier()); // Object identifier
        return obj;
    }
}

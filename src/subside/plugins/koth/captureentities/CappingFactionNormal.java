package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayerColl;

public class CappingFactionNormal extends CappingGroup {
private Faction faction;
    
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, Faction faction){
        super(captureTypeRegistry);
        this.faction = faction;
    }
    
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, List<Player> playerList2){
        super(captureTypeRegistry);
        List<Player> playerList = new ArrayList<Player>(playerList2);
        Collections.shuffle(playerList);
        for(Player player : playerList){
            Faction fac = MPlayerColl.get().get(player).getFaction();
            if(fac.isNormal()){
                faction = fac;
                break;
            }
        }
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        try {
            return MPlayerColl.get().get(oPlayer).getFactionId().equals(faction.getId());
        } catch(Exception e){
            return false;
        }
    }

    @Override
    public String getUniqueClassIdentifier(){
        return "faction";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return faction.getId();
    }
    
    @Override
    public String getName(){
        return faction.getName();
    }
    
    public Faction getObject(){
        return faction;
    }
    
    @Override 
    public List<Player> getAllOnlinePlayers(){
        return faction.getOnlinePlayers();
    }
    

    public static Capper getFromUniqueName(CaptureTypeRegistry captureTypeRegistry, String name){
        return new CappingFactionNormal(captureTypeRegistry, FactionColl.get().get(name));
    }
}

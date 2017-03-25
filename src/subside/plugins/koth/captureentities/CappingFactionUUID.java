package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class CappingFactionUUID extends CappingGroup {
    private Faction faction;
    
    public CappingFactionUUID(CaptureTypeRegistry captureTypeRegistry, Faction faction){
        super(captureTypeRegistry);
        this.faction = faction;
    }
    
    public CappingFactionUUID(CaptureTypeRegistry captureTypeRegistry, List<Player> playerList2){
        super(captureTypeRegistry);
        List<Player> playerList = new ArrayList<Player>(playerList2);
        Collections.shuffle(playerList);
        for(Player player : playerList){
            Faction fac = FPlayers.getInstance().getByPlayer(player).getFaction();
            if(fac.isNormal()){
                faction = fac;
                break;
            }
        }
    }

    @Override
    public String getUniqueClassIdentifier(){
        return "factionuuid";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return faction.getId();
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return FPlayers.getInstance().getByOfflinePlayer(oPlayer).getFactionId().equals(faction.getId());
    }
    
    @Override
    public String getName(){
        return faction.getTag();
    }

    public Faction getObject(){
        return faction;
    }

    
    @Override 
    public List<Player> getAllOnlinePlayers(){
        return faction.getOnlinePlayers();
    }

    public static Capper getFromUniqueName(CaptureTypeRegistry captureTypeRegistry, String name){
        return new CappingFactionUUID(captureTypeRegistry, Factions.getInstance().getFactionById(name));
    }
}

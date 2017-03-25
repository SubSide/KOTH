package subside.plugins.koth.captureentities;

import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayerColl;

public class CappingFactionNormal extends CappingGroup<Faction> {
    
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, Faction faction){
        super(captureTypeRegistry, "faction", faction);
    }
    
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry, MPlayerColl.get().get(
                playerList.stream()
                .filter(player -> MPlayerColl.get().get(player).getFaction().isNormal())
                .findAny()
                .orElse(null)
            ).getFaction()
        );
    }
    
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, String name){
        this(captureTypeRegistry, FactionColl.get().get(name));
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        try {
            return MPlayerColl.get().get(oPlayer).getFactionId().equals(getObject().getId());
        } catch(Exception e){
            return false;
        }
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getId();
    }
    
    @Override
    public String getName(){
        return getObject().getName();
    }
    
    @Override 
    public Collection<Player> getAllOnlinePlayers(){
        return getObject().getOnlinePlayers();
    }
}

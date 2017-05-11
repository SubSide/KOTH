package subside.plugins.koth.captureentities;

import java.util.Collection;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;


public class CappingLegacyFactions extends CappingGroup<Faction> {
    
    public CappingLegacyFactions(CaptureTypeRegistry captureTypeRegistry, Faction faction) {
        super(captureTypeRegistry, "legacyfactions", faction);
    }
    
    public CappingLegacyFactions(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                .filter(player -> FPlayerColl.get(player).getFaction().isNormal()) // Filter to only normal factions
                .map(player -> FPlayerColl.get(player).getFaction()) // Create a new stream containing factions
                .findAny() // Grab a single faction
                .orElse(null) // If no faction exists return null
        );
    }

    public CappingLegacyFactions(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, FactionColl.get(uuid));
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getId();
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return FPlayerColl.get(oPlayer).getFactionId().equals(getObject().getId());
    }
    
    @Override
    public String getName(){
        return getObject().getTag();
    }

    @Override 
    public List<Player> getAllOnlinePlayers(){
        return getObject().getOnlinePlayers();
    }
    
}

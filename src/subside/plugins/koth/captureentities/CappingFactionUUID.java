package subside.plugins.koth.captureentities;

import java.util.Collection;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class CappingFactionUUID extends CappingGroup<Faction> {
    
    public CappingFactionUUID(CaptureTypeRegistry captureTypeRegistry, Faction faction){
        super(captureTypeRegistry, "factionuuid", faction);
    }
    
    public CappingFactionUUID(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                .filter(player -> FPlayers.getInstance().getByPlayer(player).getFaction().isNormal()) // Filter to only normal factions
                .map(player -> FPlayers.getInstance().getByPlayer(player).getFaction()) // Create a new stream containing factions
                .findAny() // Grab a single faction
                .orElse(null) // If no faction exists return null
        );
    }

    public CappingFactionUUID(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, Factions.getInstance().getFactionById(uuid));
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getId();
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return FPlayers.getInstance().getByOfflinePlayer(oPlayer).getFactionId().equals(getObject().getId());
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

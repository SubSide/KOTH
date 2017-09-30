package subside.plugins.koth.captureentities;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;

public class CappingMCMMOParty extends CappingGroup<Party> {
	public CappingMCMMOParty(CaptureTypeRegistry captureTypeRegistry, Party party){
        super(captureTypeRegistry, "mcmmoparty", party);
    }
    
    public CappingMCMMOParty(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                .map(PartyManager::getParty) // Create a new stream containing parties
                .filter(Objects::nonNull)
                .findAny() // Grab a single party
                .orElse(null) // If no party exists return null
        );
    }

    public CappingMCMMOParty(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, PartyManager.getParty(uuid));
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getName();
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return getObject().hasMember(oPlayer.getUniqueId());
    }
    
    @Override
    public String getName(){
        return getObject().getName();
    }

    @Override 
    public List<Player> getAllOnlinePlayers(){
        return getObject().getOnlineMembers();
    }
}

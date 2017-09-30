package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

public class CappingKingdom extends CappingGroup<Kingdom> {
    
    public CappingKingdom(CaptureTypeRegistry captureTypeRegistry, Kingdom kingdom){
        super(captureTypeRegistry, "kingdom", kingdom);
    }
    
    public CappingKingdom(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                .filter(player -> GameManagement.getPlayerManager().getSession(player) != null) // Filter to only players in a kingdom
                .map(player -> GameManagement.getPlayerManager().getSession(player).getKingdom()) // Create a new stream containing kingdoms
                .findAny() // Grab a single kingdom
                .orElse(null) // If no kingdom exists return null
        );
    }

    public CappingKingdom(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, GameManagement.getKingdomManager().getOrLoadKingdom(uuid));
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        if(!oPlayer.isOnline()) return false;
        
        return getObject().equals(GameManagement.getPlayerManager().getSession(oPlayer.getPlayer()).getKingdom());
    }

    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getKingdomName();
    }
    
    @Override
    public String getName(){
        return getObject().getKingdomName();
    }
    
    @Override 
    public Collection<Player> getAllOnlinePlayers(){
        List<Player> list = new ArrayList<>();
        for(KingdomPlayer player : getObject().getOnlineMembers()){
            list.add(player.getPlayer());
        }
        
        return list;
    }
}

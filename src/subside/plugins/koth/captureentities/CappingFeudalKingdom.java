package subside.plugins.koth.captureentities;

import java.util.*;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import us.forseth11.feudal.core.Feudal;
import us.forseth11.feudal.kingdoms.Kingdom;
import us.forseth11.feudal.user.User;

public class CappingFeudalKingdom extends CappingGroup<Kingdom> {

    public CappingFeudalKingdom(CaptureTypeRegistry captureTypeRegistry, Kingdom kingdom){
        super(captureTypeRegistry, "kingdom", kingdom);
    }

    public CappingFeudalKingdom(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                        .filter(player -> Feudal.getAPI().getKingdom(player) != null) // Filter to only players in a kingdom
                        .map(player -> Feudal.getAPI().getKingdom(player)) // Create a new stream containing kingdoms
                        .findAny() // Grab a single kingdom
                        .orElse(null) // If no kingdom exists return null
        );
    }

    public CappingFeudalKingdom(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, Feudal.getAPI().getKingdomByUUID(uuid));
    }

    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return getObject().equals(Feudal.getAPI().getKingdom(oPlayer));
    }

    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getUUID();
    }

    @Override
    public String getName(){
        return getObject().getName();
    }

    @Override
    public Collection<Player> getAllOnlinePlayers(){
        List<Player> list = new ArrayList<>();
        for(Map.Entry<String, User> member : Feudal.getOnlinePlayers().entrySet()){
            if(!member.getValue().getKingdomUUID().equals(getObject().getUUID()))
                continue;

            list.add(member.getValue().getPlayer());
        }

        return list;
    }
}

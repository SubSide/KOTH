package subside.plugins.koth.capture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.main.Kingdoms;
import org.kingdoms.manager.game.GameManagement;

import subside.plugins.koth.areas.Capable;

public class CappingKingdom extends CappingGroup {
    private Kingdom kingdom;
    
    public CappingKingdom(CaptureTypeRegistry captureTypeRegistry, Kingdom kingdom){
        super(captureTypeRegistry);
        this.kingdom = kingdom;
    }
    
    public CappingKingdom(CaptureTypeRegistry captureTypeRegistry, List<Player> playerList2){
        super(captureTypeRegistry);
        List<Player> playerList = new ArrayList<>(playerList2);
        Collections.shuffle(playerList);
        for(Player player : playerList){
            Kingdoms.getManagers();
            KingdomPlayer kp = GameManagement.getPlayerManager().getSession(player);
            if(kp.getKingdom() != null){
                this.kingdom = kp.getKingdom();
                break;
            }
        }
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        if(!oPlayer.isOnline()) return false;
        return kingdom.equals(GameManagement.getPlayerManager().getSession(oPlayer.getPlayer()).getKingdom());
    }

    @Override
    public String getUniqueClassIdentifier(){
        return "kingdom";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return kingdom.getKingdomName();
    }
    
    @Override
    public String getName(){
        return kingdom.getKingdomName();
    }
    
    public Kingdom getObject(){
        return kingdom;
    }

    @Override
    public boolean areaCheck(Capable cap) {
        for(KingdomPlayer kPlayer : kingdom.getOnlineMembers()){
            Player player = kPlayer.getPlayer();
            if(cap.isInArea(player) && captureTypeRegistry.getPlugin().getHookManager().canCap(player)){
                return true;
            }
        }
        return false;
    }
    
    @Override 
    public List<Player> getAllOnlinePlayers(){
        List<Player> list = new ArrayList<>();
        for(KingdomPlayer player : kingdom.getOnlineMembers()){
            list.add(player.getPlayer());
        }
        
        return list;
    }

    public static Capper getFromUniqueName(CaptureTypeRegistry captureTypeRegistry, String name){
        return new CappingKingdom(captureTypeRegistry, GameManagement.getKingdomManager().getOrLoadKingdom(name));
    }
}

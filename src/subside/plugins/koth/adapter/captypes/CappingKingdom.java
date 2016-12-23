package subside.plugins.koth.adapter.captypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.main.Kingdoms;
import org.kingdoms.manager.game.GameManagement;

import subside.plugins.koth.adapter.Capable;
import subside.plugins.koth.hooks.HookManager;

public class CappingKingdom extends CappingGroup {
    private Kingdom kingdom;
    
    public CappingKingdom(Kingdom kingdom){
        this.kingdom = kingdom;
    }
    
    public CappingKingdom(List<Player> playerList2){
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
            if(HookManager.getHookManager().canCap(player) && cap.isInArea(player)){
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

    public static Capper getFromUniqueName(String name){
        return new CappingKingdom(GameManagement.getKingdomManager().getOrLoadKingdom(name));
    }
}

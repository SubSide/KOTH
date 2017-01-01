package subside.plugins.koth.loot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.modules.AbstractModule;
import subside.plugins.koth.utils.JSONLoader;

public class LootHandler extends AbstractModule {
    private @Getter List<Loot> loots;
    
    public LootHandler(KothPlugin plugin){
        super(plugin);
        
        this.loots = new ArrayList<>();
    }
    
    /** Get a loot by name
     * 
     * @param name      The name of the loot chest
     * @return          The loot object
     */
    public Loot getLoot(String name){
        if(name == null) return null;
        for(Loot loot : loots){
            if(loot.getName().equalsIgnoreCase(name)){
                return loot;
            }
        }
        return null;
    }
    
    @Override
    public void onEnable() {
        Object obj = new JSONLoader(plugin, "loot.json").load();
        
        if(obj == null)
            return;
        
        if(obj instanceof JSONArray){
            JSONArray koths = (JSONArray) obj;
            
            Iterator<?> it = koths.iterator();
            while(it.hasNext()){
                try {
                    Loot loot = new Loot(this);
                    loot.load((JSONObject)it.next());
                    loots.add(loot);
                } catch(Exception e){
                    plugin.getLogger().log(Level.SEVERE, "////////////////\nError loading loot!\n////////////////", e);
                }
            }
        }
    }
    
    @Override
    public void onDisable() {
     // Make sure that nobody is viewing a loot chest
        // This is important because otherwise people could take stuff out of the viewing loot chest
        for(Player player : Bukkit.getOnlinePlayers()){
            String title = player.getOpenInventory().getTitle();
            for(Loot loot : loots){
                if(loot.getInventory().getTitle().equalsIgnoreCase(title)){
                    player.closeInventory();
                    break; // No need to close the players inventory more than once!
                }
            }
        }
        
        save(); // Save the loot
    }

    @SuppressWarnings("unchecked")
    public void save(){
        JSONArray obj = new JSONArray();
        for (Loot loot : loots) {
            obj.add(loot.save());
        }
        new JSONLoader(plugin, "loot.json").save(obj);
    }
}

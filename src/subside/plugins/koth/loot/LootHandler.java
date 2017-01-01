package subside.plugins.koth.loot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.AbstractModule;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.loaders.JSONLoader;

public class LootHandler extends AbstractModule {
    private @Getter List<Loot> loots;
    
    public LootHandler(KothPlugin plugin){
        super(plugin);
        
        this.loots = new ArrayList<>();
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
                    Loot loot = new Loot(null);
                    loot.load((JSONObject)it.next());
                    loots.add(loot);
                } catch(Exception e){
                    plugin.getLogger().log(Level.SEVERE, "////////////////\nError loading loot!\n////////////////", e);
                }
            }
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void onDisable() {
        JSONArray obj = new JSONArray();
        for (Loot loot : loots) {
            obj.add(loot.save());
        }
        new JSONLoader(plugin, "loot.json").save(obj);
    }
}

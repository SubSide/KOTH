package subside.plugins.koth.captureentities;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.gamemodes.KothConquest;
import subside.plugins.koth.modules.AbstractModule;

public class CaptureTypeRegistry extends AbstractModule {
    private @Getter Map<String, Class<? extends Capper>> captureClasses;
    private @Getter Map<String, Class<? extends Capper>> captureTypes;
    private @Getter @Setter Class<? extends Capper> preferedClass;
    
    public CaptureTypeRegistry(KothPlugin plugin){
        super(plugin);
        captureTypes = new HashMap<>();
        captureClasses = new HashMap<>();
    }
    
    @Override
    public void onLoad(){
        captureTypes.clear();
        captureClasses.clear();
        
        // Add the player entity
        registerCaptureClass("capperclass", Capper.class);
        
        registerCaptureType("player", CappingPlayer.class);
        setPreferedClass(CappingPlayer.class);
        boolean hasGroupPlugin = false;
        if(plugin.getConfigHandler().getHooks().isFactions() && plugin.getServer().getPluginManager().getPlugin("Factions") != null){
            try {
                // If this class is not found it means that Factions is not in the server
                Class.forName("com.massivecraft.factions.entity.FactionColl");
                registerCaptureType("faction", CappingFactionNormal.class);
                setPreferedClass(CappingFactionNormal.class);
                hasGroupPlugin = true;
            } catch(ClassNotFoundException e){
                // So if the class is not found, we add FactionsUUID instead
                registerCaptureType("factionuuid", CappingFactionUUID.class);
                setPreferedClass(CappingFactionUUID.class);
                hasGroupPlugin = true;
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
        if(plugin.getConfigHandler().getHooks().isKingdoms() && plugin.getServer().getPluginManager().getPlugin("Kingdoms") != null){
            registerCaptureType("kingdoms", CappingKingdom.class);
            setPreferedClass(CappingKingdom.class);
            hasGroupPlugin = true;
        }
        
        // Make sure when you register your own group-like capturetype, to register the CappingGroup in the capentityregistry
        if(hasGroupPlugin){
            registerCaptureClass("groupclass", CappingGroup.class);
            
            // Since we know we have a group plugin, we can also register Conquest in the GamemodeRegistry
            plugin.getGamemodeRegistry().register("conquest", KothConquest.class);
        }
        
        if(getCaptureTypeClass(plugin.getConfigHandler().getKoth().getDefaultCaptureType()) != null)
            setPreferedClass(getCaptureTypeClass(plugin.getConfigHandler().getKoth().getDefaultCaptureType()));
    }
    
    public void registerCaptureType(String captureTypeIdentifier, Class<? extends Capper> clazz){
        captureTypes.put(captureTypeIdentifier, clazz);

        registerCaptureClass(captureTypeIdentifier, clazz); // Also register it as a capture class
    }
    
    public void registerCaptureClass(String captureClassIdentifier, Class<? extends Capper> clazz){
        captureClasses.put(captureClassIdentifier, clazz);
    }
    
    public Class<? extends Capper> getCaptureTypeClass(String name){
        return captureTypes.get(name);
    }
    
    public Class<? extends Capper> getCaptureClass(String name){
        return captureClasses.get(name);
    }
    
    public String getIdentifierFromClass(Class<? extends Capper> clazz){
        for (Entry<String, Class<? extends Capper>> entry : captureClasses.entrySet()) {
            if (Objects.equals(clazz, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public Capper getCapperFromType(String captureTypeIdentifier, String objectUniqueId){
        if(!captureTypes.containsKey(captureTypeIdentifier)){
            return null;
        }
        try {
            return (Capper)captureTypes.get(captureTypeIdentifier).getDeclaredMethod("getFromUniqueName", CaptureTypeRegistry.class, String.class).invoke(null, this, objectUniqueId);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    

    public Capper getCapper(Class<? extends Capper> capperClazz, List<Player> players){
        try {
            for(Class<? extends Capper> clazz : getCaptureTypes().values()){
                if(capperClazz.isAssignableFrom(clazz)){
                    Capper capper =  clazz.getDeclaredConstructor(CaptureTypeRegistry.class, List.class).newInstance(this, players);
                    if(capper.getObject() == null){
                        return null;
                    }
                    return capper;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
}

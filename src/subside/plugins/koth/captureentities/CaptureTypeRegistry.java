package subside.plugins.koth.captureentities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.gamemodes.KothConquest;
import subside.plugins.koth.modules.AbstractModule;

@SuppressWarnings("rawtypes")
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
        registerCaptureType("player", CappingPlayer.class, true);
        
        // LegacyFactions, Factions, and FactionsUUID
        if(plugin.getConfigHandler().getHooks().isFactions()) {
            if(plugin.getServer().getPluginManager().getPlugin("LegacyFactions") != null){
                registerCaptureType("legacyfactions", CappingLegacyFactions.class, true);
            } else if(plugin.getServer().getPluginManager().getPlugin("Factions") != null){
                try {
                    // If this class is not found it means that Factions is not in the server
                    Class.forName("com.massivecraft.factions.entity.FactionColl");
                    registerCaptureType("faction", CappingFactionNormal.class, true);
                } catch(ClassNotFoundException e){
                    // So if the class is not found, we add FactionsUUID instead
                    registerCaptureType("factionuuid", CappingFactionUUID.class, true);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        
        // Kingdoms
        if(plugin.getConfigHandler().getHooks().isKingdoms() && plugin.getServer().getPluginManager().getPlugin("Kingdoms") != null){
            registerCaptureType("kingdom", CappingKingdom.class, true);
        }
        
        // Gangs
        if(plugin.getConfigHandler().getHooks().isGangs() && plugin.getServer().getPluginManager().getPlugin("GangsPlus") != null){
            registerCaptureType("gang", CappingGang.class, true);
        }
        
        // mcMMO parties
        if(plugin.getConfigHandler().getHooks().isMcMMO() && plugin.getServer().getPluginManager().getPlugin("mcMMO") != null){
        	registerCaptureType("mcmmoparty", CappingMCMMOParty.class, true);
        }
    }
    
    @Override
    public void onEnable(){
        // Set the default capture type that is defined in the config
        // The reason we're doing this here instead of the onload, is so other plugins can hook into the plugin
        // without overwriting the prefered class in the config
        if(getCaptureTypeClass(plugin.getConfigHandler().getKoth().getDefaultCaptureType()) != null)
            setPreferedClass(getCaptureTypeClass(plugin.getConfigHandler().getKoth().getDefaultCaptureType()));
    }
    
    /**
     * Use this function to register a capture type class that can actively capture a KoTH.
     * 
     * @param captureTypeIdentifier an unique identifier for this class specifically
     * @param clazz the class object
     */
    public void registerCaptureType(String captureTypeIdentifier, Class<? extends Capper> clazz){
        captureTypes.put(captureTypeIdentifier, clazz);
        
        // Automatically register the CappingGroup class if the registered class is from the CappingGroup type
        if(CappingGroup.class.isInstance(clazz)){
            registerCaptureClass("groupclass", CappingGroup.class);
            
            // Since we know we have a group plugin, we can also register Conquest in the GamemodeRegistry
            plugin.getGamemodeRegistry().register("conquest", KothConquest.class);
        }

        // Also register it as a capture class
        registerCaptureClass(captureTypeIdentifier, clazz);
    }
    
    /**
     * This function is a shortcut for registering the capturetype as well as prefering it
     * @param captureTypeIdentifier
     * @param clazz 
     * @param isPrefered if the class should be prefered or not
     */
    public void registerCaptureType(String captureTypeIdentifier, Class<? extends Capper> clazz, boolean isPrefered){
        registerCaptureType(captureTypeIdentifier, clazz);
        
        if(isPrefered)
            setPreferedClass(clazz);
    }
    
    /**
     * This class is automatically called when you register a capture type.
     * This is internally also used for the Capper class and the CappingGroup class
     * @param captureClassIdentifier the unique capture class identifier
     * @param clazz the class
     */
    public void registerCaptureClass(String captureClassIdentifier, Class<? extends Capper> clazz){
        captureClasses.put(captureClassIdentifier, clazz);
    }
    
    /**
     * Get a specific capture type class from its unique name
     * @param name the unique identifier of the class
     * @return The class
     */
    public Class<? extends Capper> getCaptureTypeClass(String name){
        return captureTypes.get(name);
    }
    
    /**
     * Get a specific capture class from its unique name
     * @param name the unique identifier of the class
     * @return The class
     */
    public Class<? extends Capper> getCaptureClass(String name){
        return captureClasses.get(name);
    }
    
    /**
     * Get the unique identifier from a specific class
     * @param clazz the class object
     * @return The unique identifier (or null if it doesn't exist)
     */
    public String getIdentifierFromClass(Class<? extends Capper> clazz){
        return captureClasses.entrySet().stream()
                .filter(entry -> clazz.equals(entry.getValue()))
                .map(obj -> obj.getKey())
                .findAny().orElse(null);
    }
    
    /**
     * Generate an instance of a Capper object from the class unique identifier and object unique identifier
     * @param captureTypeIdentifier the class unique identifier
     * @param objectUniqueId the object unique identifier
     * @return the Capper object (or null if it couldn't be created or if something went wrong)
     */
    public Capper getCapperFromType(String captureTypeIdentifier, String objectUniqueId){
        if(!captureTypes.containsKey(captureTypeIdentifier)){
            return null;
        }
        try {
            Capper capper =  captureTypes.get(captureTypeIdentifier).getDeclaredConstructor(CaptureTypeRegistry.class, String.class).newInstance(this, objectUniqueId);
            if(capper.getObject() == null){
                return null;
            }
            return capper;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generate a Capper from a random list of players
     * @param capperClazz the class of the object that should be created
     * @param players a list of players
     * @return the Capper object (or null if it couldn't be created or if something went wrong)
     */
    public Capper getCapper(Class<? extends Capper> capperClazz, List<Player> players){
        try {
            for(Class<? extends Capper> clazz : getCaptureTypes().values()){
                if(!capperClazz.isAssignableFrom(clazz)){
                    continue;
                }
                
                // Create the object
                Capper capper = clazz.getDeclaredConstructor(CaptureTypeRegistry.class, Collection.class).newInstance(this, players);
                if(capper.getObject() == null){
                    return null;
                }
                return capper;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
}

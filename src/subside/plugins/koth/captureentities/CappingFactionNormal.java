package subside.plugins.koth.captureentities;

import java.util.Collection;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayerColl;

/*
 * All the functions in this class are mandatory for a capture type to work.
 * All constructors must be available, with those exact parameters.
 * This is because objects are dynamically created by the CaptureEntityRegistry.
 * 
 * Make sure to register your capture type in the onLoad Phase. 
 * This can easily be done by using the KothPluginInitializationEvent.
 * You can then use setPreferedClass so KoTH will prefer your class.
 * <s>If it's a group plugin, make sure to register KoTH's CappingGroup.class as well.</s>
 * You don't need to register KoTH's CappingGroup anymore, as it will automatically
 * detect if your class is a CappingGroup class
 */
public class CappingFactionNormal extends CappingGroup<Faction> {
    
    /**
     * This is used as main constructor to call its parent constructor.
     * 
     * @param captureTypeRegistry the CaptureEntityRegistry
     * @param faction The Faction
     */
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, Faction faction){
        super(captureTypeRegistry, "faction", faction);
    }
    
    /**
     * This constructor is used for randomly selecting a capper from all players that are currently on the objective.
     * 
     * I used a stream to make sure that Factions like wilderness and such are not included.
     * If you return null as parameter then KoTH will see it as nobody is capturing it.
     * 
     * @param captureTypeRegistry the CaptureEntityRegistry
     * @param playerList a list containing all players on the point
     */
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, Collection<Player> playerList){
        this(captureTypeRegistry,
                playerList.stream() // Create a stream
                .filter(player -> MPlayerColl.get().get(player).getFaction().isNormal()) // Filter to only normal factions (e.g. no wilderness etc)
                .map(player -> MPlayerColl.get().get(player).getFaction()) // Create a new stream containing factions
                .findAny() // Grab a single faction
                .orElse(null) // If no faction exists return null
        );
    }
    
    /**
     * This is the constructor that is used for deserializing.
     * When for example a RunningKoth is loaded from cache, it will try to recreate
     * the Capping object using the unique identifier created by the getUniqueObjectIdentifier() function.
     * For players this will be their UUID, for Gangs their id, etc.
     * 
     * @param captureTypeRegistry the CaptureTypeRegistry
     * @param uuid the UUID to deserialize the object
     */
    public CappingFactionNormal(CaptureTypeRegistry captureTypeRegistry, String uuid){
        this(captureTypeRegistry, FactionColl.get().get(uuid));
    }

    /**
     * This will be used to see if a player is in or equal to the object.
     * This function is used for multiple things. Like to check if the user can open the chest.
     * Or with contestFreeze to see if there's someone standing on the point that is not of that object.
     * 
     * @param oPlayer the player to check
     * @return true if the player is part of the object.
     */
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        try {
            return MPlayerColl.get().get(oPlayer).getFaction().getId().equals(getObject().getId());
        } catch(Exception e){
            return false;
        }
    }
    
    /**
     * This should return a string which uniquely identifies the object
     * This is later used with the constructor to deserialize the object.
     * 
     * @return the unique object identifier
     */
    @Override
    public String getUniqueObjectIdentifier(){
        return getObject().getId();
    }

    /**
     * This should return the display name of the object.
     * For players this should return the player name, for factions for example the faction name.
     * This doesn't need to be unique and will be used for stuff like the scoreboard, broadcasts, etc.
     * 
     * @return The display name of the object.
     */
    @Override
    public String getName(){
        return getObject().getName();
    }

    /**
     * Returns a collection of players that are currently online.
     * Used to do area checks and such
     * 
     * @return a collection of online players.
     */
    @Override 
    public Collection<Player> getAllOnlinePlayers(){
        return getObject().getOnlinePlayers();
    }
}

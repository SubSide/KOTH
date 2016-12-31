package subside.plugins.koth.capture;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class CapEntityRegistry {
    private @Getter Map<String, Class<? extends Capper>> captureClasses;
    private @Getter Map<String, Class<? extends Capper>> captureTypes;
    private @Getter @Setter Class<? extends Capper> preferedClass;

    public CapEntityRegistry(){
        captureTypes = new HashMap<>();
        captureClasses = new HashMap<>();
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
            return (Capper)captureTypes.get(captureTypeIdentifier).getDeclaredMethod("getFromUniqueName", String.class).invoke(null, objectUniqueId);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
    

    public Capper getCapper(Class<? extends Capper> capperClazz, List<Player> players){
        try {
            for(Class<? extends Capper> clazz : getCaptureTypes().values()){
                if(capperClazz.isAssignableFrom(clazz)){
                    Capper capper =  clazz.getDeclaredConstructor(List.class).newInstance(players);
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

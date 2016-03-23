package subside.plugins.koth.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.adapter.RunningKoth.EndReason;
import subside.plugins.koth.adapter.captypes.Capper;
import subside.plugins.koth.events.KothStartEvent;
import subside.plugins.koth.exceptions.KothAlreadyExistException;
import subside.plugins.koth.exceptions.KothAlreadyRunningException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.loaders.KothLoader;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;
import subside.plugins.koth.scoreboard.SBManager;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothHandler {
    private static @Getter KothHandler instance;
    
    private @Getter List<RunningKoth> runningKoths;
    private @Getter List<Koth> availableKoths;
    private @Getter List<Loot> loots;
    private @Getter GamemodeRegistry gamemodeRegistry;
    private @Getter CapEntityRegistry capEntityRegistry;
    
    public KothHandler(){
        instance = this;
        runningKoths = new ArrayList<>();
        availableKoths = new ArrayList<>();
        loots = new ArrayList<>();
        
        gamemodeRegistry = new GamemodeRegistry();
        capEntityRegistry = new CapEntityRegistry();
    }

    @Deprecated
    public void update() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next().update();
            }
            if (ConfigHandler.getCfgHandler().getScoreboard().isUseScoreboard()) {
                SBManager.getManager().update();
            }
            ScheduleHandler.getInstance().tick();
        }
    }

    /** Gets a the currently running KoTH
     * 
     * @return          the currently running KoTH, null if none is running.
     */
    public RunningKoth getRunningKoth() {
        synchronized (runningKoths) {
            if (runningKoths.size() > 0) {
                return runningKoths.get(0);
            } else {
                return null;
            }
        }
    }
    
    @Deprecated
    public void startKoth(Schedule schedule){
        StartParams params = new StartParams(schedule.getKoth());
        params.setCaptureTime(schedule.getCaptureTime()*60);
        params.setMaxRunTime(schedule.getMaxRunTime());
        params.setLootAmount(schedule.getLootAmount());
        params.setLootChest(schedule.getLootChest());
        params.setScheduled(true);
        
        startKoth(params);
    }
    

    /** Start a certain KoTH
     * 
     * @param koth              The KoTH to run
     * @param captureTime       The captureTime
     * @param maxRunTime        The maximum time this KoTH can run (-1 for unlimited time)
     * @param lootAmount        The amount of loot that should spawn (-1 for default config settings)
     * @param lootChest         The lootchest it should use (null for default config settings)
     * @param isScheduled       This is used to see if it should obey stuff like minimumPlayers
     */
    @SuppressWarnings("deprecation")
    public void startKoth(StartParams params) {
        synchronized (runningKoths) {
            for (RunningKoth rKoth : runningKoths) {
                if (rKoth.getKoth() == params.getKoth()) {
                    throw new KothAlreadyRunningException(params.getKoth().getName());
                }
            }
            KothStartEvent event = new KothStartEvent(params.getKoth(), params.getCaptureTime(), params.getMaxRunTime(), params.isScheduled());

            if (params.isScheduled() && Lists.newArrayList(Bukkit.getOnlinePlayers()).size() < ConfigHandler.getCfgHandler().getKoth().getMinimumPlayersNeeded()) {
                event.setCancelled(true);
            }

            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                RunningKoth rKoth = this.getGamemodeRegistry().createGame(params.getGamemode());
                rKoth.init(params);
                runningKoths.add(rKoth);
            }
        }

    }


    /** Create a new KoTH
     * 
     * @param name              The KoTH name
     * @param min               The first location
     * @param max               The max position
     */
    public void createKoth(String name, Location min, Location max) {
        if (getKoth(name) == null && !name.equalsIgnoreCase("random")) {
            Koth koth = new Koth(name);
            koth.getAreas().add(new Area(name, min, max));
            availableKoths.add(koth);
            KothLoader.save();
        } else {
            throw new KothAlreadyExistException(name);
        }
    }


    /** Remove a certain KoTH
     * 
     * @param koth              The KoTH to remove
     */
    public void removeKoth(String name) {
        Koth koth = getKoth(name);
        if (koth == null) {
            throw new KothNotExistException(name);
        }

        availableKoths.remove(koth);
        KothLoader.save();
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

    /** Get a KoTH by name
     * 
     * @param name      The name of the KoTH
     * @return          The KoTH object
     */
    public Koth getKoth(String name) {
        for (Koth koth : availableKoths) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return koth;
            }
        }
        return null;
    }

    /** Stop all running koths
     * 
     */
    public void stopAllKoths() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        SBManager.getManager().clearAll();
    }

    @Deprecated
    public void remove(RunningKoth koth){
        synchronized (runningKoths) {
            runningKoths.remove(koth);

            if(runningKoths.size() < 1){
            	SBManager.getManager().clearAll();
            }
        }
    }
    
    /** Stop a specific koth
     * 
     * @param name      Stop a KoTH by a certain name
     */
    public void stopKoth(String name) {
        Iterator<RunningKoth> it = runningKoths.iterator();
        while (it.hasNext()) {
            RunningKoth koth = it.next();
            if (koth.getKoth().getName().equalsIgnoreCase(name)) {
                koth.endKoth(EndReason.FORCED);
            }
        }
    }


    /** Gracefully ends all running KoTHs
     * 
     */
    public void endAllKoths() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next().endKoth(EndReason.GRACEFUL);
            }
        }
    }

    /** Gracefully ends a certain KoTH
     * 
     * @param name      The name of the KoTH to end
     */
    public void endKoth(String name) {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                RunningKoth koth = it.next();
                if (koth.getKoth().getName().equalsIgnoreCase(name)) {
                    koth.endKoth(EndReason.GRACEFUL);
                    return;
                }
            }
            throw new KothNotExistException(name);
        }
    }
    
    public class GamemodeRegistry {
        private @Getter HashMap<String, Class<? extends RunningKoth>> gamemodes;
        private @Getter @Setter String currentMode;
        
        public GamemodeRegistry(){
            gamemodes = new HashMap<>();
            currentMode = "classic";
        }
        
        public void register(String name, Class<? extends RunningKoth> clazz){
            gamemodes.put(name, clazz);
        }
        
        public RunningKoth createGame(){
            return createGame(currentMode);
        }
        
        public RunningKoth createGame(String gamemode){
            if(gamemodes.containsKey(gamemode)){
                try {
                    return gamemodes.get(gamemode).newInstance();
                }
                catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    
    public class CapEntityRegistry {
        private @Getter Map<String, Class<? extends Capper>> captureTypes = new HashMap<>();

        public CapEntityRegistry(){
            captureTypes = new HashMap<>();
        }
        
        public void registerCaptureType(String captureTypeIdentifier, Class<? extends Capper> clazz){
            captureTypes.put(captureTypeIdentifier, clazz);
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
    }
}

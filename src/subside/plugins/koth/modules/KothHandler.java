package subside.plugins.koth.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.Lists;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.events.KothInitializeEvent;
import subside.plugins.koth.events.KothPostUpdateEvent;
import subside.plugins.koth.events.KothPreUpdateEvent;
import subside.plugins.koth.events.KothStartEvent;
import subside.plugins.koth.exceptions.AnotherKothAlreadyRunningException;
import subside.plugins.koth.exceptions.IllegalKothNameException;
import subside.plugins.koth.exceptions.KothAlreadyExistException;
import subside.plugins.koth.exceptions.KothAlreadyRunningException;
import subside.plugins.koth.exceptions.KothException;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.gamemodes.RunningKoth.EndReason;
import subside.plugins.koth.gamemodes.StartParams;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.utils.JSONLoader;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothHandler extends AbstractModule implements Runnable {
    private final @Getter List<RunningKoth> runningKoths;
    private final @Getter List<Koth> availableKoths;
    
    private @Getter int taskId;
    
    public KothHandler(KothPlugin plugin){
        super(plugin);
        
        runningKoths = new ArrayList<>();
        availableKoths = new ArrayList<>();
    }
    
    @Override
    public void onLoad(){
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void onEnable(){
        loadKoths(); // Load all KoTH's
        
        // Add a repeating ASYNC scheduler for the KothHandler
        this.taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, 20, 20);
    }
    
    @Override
    public void onDisable(){
        // Remove all previous schedulings
        Bukkit.getScheduler().cancelTask(this.taskId);
        
        saveKoths(); // Save all KoTH's
    }

    @Override
    public void run() {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                // Call an PreUpdateEvent, this can be cancelled (For whichever reason)
                KothPreUpdateEvent preEvent = new KothPreUpdateEvent(it.next());
                plugin.getServer().getPluginManager().callEvent(preEvent);
                if(!preEvent.isCancelled()){
                    preEvent.getRunningKoth().update();
                    
                    // If the preEvent is not cancelled call postUpdateEvent, this cannot be cancelled as there is nothing to cancel.
                    plugin.getServer().getPluginManager().callEvent(new KothPostUpdateEvent(preEvent.getRunningKoth()));
                }
            }
            getPlugin().getScheduleHandler().tick();
            getPlugin().getHookManager().tick();
        }
    }

    /** Gets a the currently running KoTH
     * 
     * @return the currently running KoTH, null if none is running.
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
    
    /** Remove a RunningKoth from runningKoths list
     * 
     * @param runningKoth the runningKoth object to remove
     */
    public void removeRunningKoth(RunningKoth runningKoth){
        synchronized (runningKoths) {
            runningKoths.remove(runningKoth);
        }
    }
    /** Add a runningKoth to the runningKoths list
     * 
     * @param runningKoth the RunningKoth object to add
     */
    public void addRunningKoth(RunningKoth runningKoth){
        KothInitializeEvent event = new KothInitializeEvent(runningKoth);
        Bukkit.getServer().getPluginManager().callEvent(event);
        
        runningKoths.add(runningKoth);
    }
    
    public void startKoth(Schedule schedule) throws KothException {
        StartParams params = new StartParams(this, schedule.getKoth());
        params.setCaptureTime(schedule.getCaptureTime()*60);
        params.setMaxRunTime(schedule.getMaxRunTime());
        params.setLootAmount(schedule.getLootAmount());
        params.setLootChest(schedule.getLootChest());
        params.setEntityType(schedule.getEntityType());
        params.setScheduled(true);
        
        startKoth(params);
    }
    

    /** Start a certain KoTH
     * 
     * @param params The params opject containing all the start params
     * @throws AnotherKothAlreadyRunningException Throws this when another KoTH is already running
     */
    public void startKoth(StartParams params) throws KothException {
        synchronized (runningKoths) {
            for (RunningKoth rKoth : runningKoths) {
                if (rKoth.getKoth() == params.getKoth()) {
                    throw new KothAlreadyRunningException(this, params.getKoth().getName());
                }
            }
            KothStartEvent event = new KothStartEvent(params.getKoth(), params.getCaptureTime(), params.getMaxRunTime(), params.isScheduled(), params.getEntityType());
            
            boolean anotherAlreadyRunning = false;
            if(this.getRunningKoth() != null && !plugin.getConfigHandler().getGlobal().isMultipleKothsAtOnce()){
                event.setCancelled(true);
                anotherAlreadyRunning = true;
            }
            
            boolean minimumNotMet = false;
            if (params.isScheduled() && Lists.newArrayList(Bukkit.getOnlinePlayers()).size() < plugin.getConfigHandler().getKoth().getMinimumPlayersNeeded()) {
                event.setCancelled(true);
                minimumNotMet = true;
            }

            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                RunningKoth rKoth = plugin.getGamemodeRegistry().createGame(params.getGamemode());
                rKoth.init(params);
                addRunningKoth(rKoth);
            } else if(anotherAlreadyRunning) {
                throw new AnotherKothAlreadyRunningException();
            }else if(minimumNotMet){
                new MessageBuilder(Lang.KOTH_PLAYING_MINIMUM_PLAYERS_NOT_MET).buildAndBroadcast();
            }
        }

    }


    public void addKoth(Koth koth){
        if(getKoth(koth.getName()) != null){
            throw new KothAlreadyExistException(this, koth.getName());
        }
        
        if(koth.getName().startsWith("$")){
            throw new IllegalKothNameException(this, koth.getName());
        }
        
        availableKoths.add(koth);
        saveKoths();
    }


    /** Remove a certain KoTH
     * 
     * @param name The name of the KoTH to remove
     * @throws KothNotExistException Throws this if the KoTH doesn't exists
     */
    public void removeKoth(String name) throws KothNotExistException {
        Koth koth = getKoth(name);
        if (koth == null) {
            throw new KothNotExistException(this, name);
        }

        availableKoths.remove(koth);
        saveKoths();
    }

    /** Get a KoTH by name
     * 
     * @param name  The name of the KoTH
     * @return  The KoTH object
     */
    public Koth getKoth(String name) {
        for (Koth koth : availableKoths) {
            if (koth.getName().equalsIgnoreCase(name)) {
                return koth;
            }
        }
        return null;
    }


    /** Gracefully ends all running KoTHs
     * 
     */
    public void endAllKoths(EndReason endReason) {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                it.next().endKoth(endReason);
            }
        }
    }

    /** Gracefully ends a certain KoTH
     * 
     * @param name The name of the KoTH to end
     * @throws KothNotExistException Throws this if the KoTH doesn't exist
     */
    public void endKoth(String name, EndReason endReason) throws KothNotExistException {
        synchronized (runningKoths) {
            Iterator<RunningKoth> it = runningKoths.iterator();
            while (it.hasNext()) {
                RunningKoth koth = it.next();
                if (koth.getKoth().getName().equalsIgnoreCase(name)) {
                    koth.endKoth(endReason);
                    return;
                }
            }
            throw new KothNotExistException(this, name);
        }
    }
    
    /* Save/Load time */
    public void loadKoths() {
        availableKoths.clear();
        
        Object obj = new JSONLoader(plugin, "koths.json").load();
        if(obj == null)
            return;
        
        if(obj instanceof JSONArray){
            JSONArray koths = (JSONArray) obj;
            
            Iterator<?> it = koths.iterator();
            while(it.hasNext()){
                try {
                    Koth koth = new Koth(this, null);
                    koth.load((JSONObject)it.next());
                    availableKoths.add(koth);
                } catch(Exception e){
                    plugin.getLogger().log(Level.SEVERE, "////////////////\nError loading koth!\n////////////////", e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void saveKoths() {
        JSONArray obj = new JSONArray();
        for (Koth koth : availableKoths) {
            obj.add(koth.save());
        }
        new JSONLoader(plugin, "koths.json").save(obj);
    }

}

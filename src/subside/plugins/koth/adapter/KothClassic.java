package subside.plugins.koth.adapter;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.captypes.Capper;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.scoreboard.ScoreboardManager;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 */
public class KothClassic implements RunningKoth {
    private @Getter Koth koth;
    private @Getter CapInfo capInfo;
    private int captureTime;
    
    private @Getter String lootChest;
    private int timeNotCapped;
    private int lootAmount;
    private int timeKnocked;
    private boolean knocked;

    private @Getter int maxRunTime;
    private int timeRunning;
    
    @Override
    public void init(StartParams params){
        this.koth = params.getKoth();
        this.captureTime = params.getCaptureTime();
        this.lootChest = params.getLootChest();
        this.lootAmount = params.getLootAmount();
        
        this.timeNotCapped = 0;
        this.capInfo = new CapInfo(this, this.koth, KothHandler.getInstance().getCapEntityRegistry().getCaptureClass(params.getEntityType()));
        this.maxRunTime = maxRunTime * 60;
        koth.removeLootChest();
        koth.setLastWinner(null);
        new MessageBuilder(Lang.KOTH_PLAYING_STARTING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
        
        final KothClassic thiz = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable(){
            @Override
            public void run() {
                ScoreboardManager.getInstance().loadScoreboard("default", thiz);
            }    
        });
    }
    
    @Override
    public Capper getCapper(){
        return getCapInfo().getCapper();
    }

    /**
     * Get the TimeObject for the running KoTH
     * @return The TimeObject
     */
    public TimeObject getTimeObject() {
        return new TimeObject(captureTime, capInfo.getTimeCapped());
    }

    public void endKoth(EndReason reason) {
        if (reason == EndReason.WON || reason == EndReason.GRACEFUL) {
            if (capInfo.getCapper() != null) {
                new MessageBuilder(Lang.KOTH_PLAYING_WON).maxTime(maxRunTime).capper(capInfo.getCapper().getName()).koth(koth)/*.shouldExcludePlayer()*/.buildAndBroadcast();
//                if (Bukkit.getPlayer(cappingPlayer) != null) {
//                    new MessageBuilder(Lang.KOTH_PLAYING_WON_CAPPER).maxTime(maxRunTime).capper(capInfo.getCapper().getName()).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
//                }
                // TO-DO
            }
        } else if (reason == EndReason.TIMEUP) {
            new MessageBuilder(Lang.KOTH_PLAYING_TIME_UP).maxTime(maxRunTime).koth(koth).buildAndBroadcast();
        }


        KothEndEvent event = new KothEndEvent(koth, capInfo.getCapper(), reason);
        Bukkit.getServer().getPluginManager().callEvent(event);

        koth.setLastWinner(capInfo.getCapper());
        if (event.isCreatingChest()) {
            Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
                public void run() {
                    koth.triggerLoot(lootAmount, lootChest);
                }
            });
        }
        
        
        final KothClassic thisObj = this;
        Bukkit.getScheduler().runTask(KothPlugin.getPlugin(), new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                KothHandler.getInstance().remove(thisObj);
            }
        });
    }

    @Deprecated
    public void update() {
        timeRunning++;
        timeNotCapped++;

        if (knocked && timeKnocked < ConfigHandler.getInstance().getKoth().getKnockTime()) {
            timeKnocked++;
            return;
        } else if (knocked) {
            knocked = false;
        }
        // CAPTURE INFO UPDATE
        capInfo.update();
        ////////

        if (capInfo.getCapper() != null) {
            timeNotCapped = 0;
            if (capInfo.getTimeCapped() < captureTime) {
                if (capInfo.getTimeCapped() % 30 == 0 && capInfo.getTimeCapped() != 0) {
                    new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME).maxTime(maxRunTime).time(getTimeObject()).capper(capInfo.getCapper().getName()).koth(koth)/*.shouldExcludePlayer()*/.buildAndBroadcast();
//                    if (Bukkit.getPlayer(cappingPlayer) != null) {
//                        new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME_CAPPER).maxTime(maxRunTime).time(getTimeObject()).capper(cappingPlayer).koth(koth).buildAndSend(Bukkit.getPlayer(cappingPlayer));
//                    }
                    // TO-DO
                }
            } else {
                endKoth(EndReason.WON);
            }
            return;
        }
        
        if(ConfigHandler.getInstance().getGlobal().getNoCapBroadcastInterval() != 0 && timeNotCapped % ConfigHandler.getInstance().getGlobal().getNoCapBroadcastInterval() == 0) {
            new MessageBuilder(Lang.KOTH_PLAYING_NOT_CAPPING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
        }

        if (maxRunTime > 0 && timeRunning > maxRunTime) {
            endKoth(EndReason.TIMEUP);
            return;
        }
    }
    
    public MessageBuilder fillMessageBuilder(MessageBuilder mB){
        return mB.maxTime(maxRunTime).time(getTimeObject()).capper(getCapInfo().getName()).koth(koth);
    }
    
    public String getType(){
        return "classic";
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject save(){
        JSONObject obj = new JSONObject();
        obj.put("koth", koth.getName());
        obj.put("capperType", capInfo.getCapper().getUniqueClassIdentifier());
        obj.put("capperEntity", capInfo.getCapper().getUniqueObjectIdentifier());
        obj.put("capperTime", capInfo.getTimeCapped());
        
        obj.put("captureTime", this.captureTime);
        obj.put("lootChest", this.lootChest);
        obj.put("lootAmount", this.lootAmount);
        obj.put("timeKnocked", this.timeKnocked);
        obj.put("knocked", this.knocked);
        obj.put("maxRunTime", this.maxRunTime);
        obj.put("timeRunning", this.timeRunning);

        return obj;
    }
    
    public KothClassic load(JSONObject obj){
        this.koth = KothHandler.getInstance().getKoth((String)obj.get("koth"));
        this.capInfo = new CapInfo(this, this.koth, KothHandler.getInstance().getCapEntityRegistry().getCaptureClass((String)obj.get("capperType")));
        this.capInfo.setCapper(KothHandler.getInstance().getCapEntityRegistry().getCapperFromType((String)obj.get("capperType"), (String)obj.get("capperEntity")));
        this.capInfo.setTimeCapped((int) obj.get("capperTime"));
        
        this.captureTime = (int) obj.get("captureTime");
        this.lootChest = (String) obj.get("lootChest");
        this.lootAmount = (int) obj.get("lootAmount");
        this.timeKnocked = (int) obj.get("timeKnocked");
        this.knocked = (boolean) obj.get("knocked");
        this.maxRunTime = (int) obj.get("maxRunTime");
        this.timeRunning = (int) obj.get("timeRunning");
        
        return this;
    }
}

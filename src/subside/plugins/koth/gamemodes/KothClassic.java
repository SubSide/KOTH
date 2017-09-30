package subside.plugins.koth.gamemodes;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import lombok.Getter;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.captureentities.CapInfo;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.captureentities.CapInfo.CapStatus;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.modules.ConfigHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

/**
 * @author Thomas "SubSide" van den Bulk
 */
public class KothClassic extends RunningKoth {
    private @Getter Koth koth;
    private @Getter CapInfo capInfo;

    private int staticCaptureTime;
    private int captureTime;
    
    private @Getter String lootChest;
    private int timeNotCapped;
    private int lootAmount;
    private int captureCooldown;

    private @Getter int maxRunTime;
    private int timeRunning;
    
    private CapStatus prevStatus = CapStatus.EMPTY;
    
    public KothClassic(GamemodeRegistry gamemodeRegistry){
        super(gamemodeRegistry);
    }
    
    @Override
    public void init(StartParams params){
        this.koth = params.getKoth();
        this.captureCooldown = 0;
        this.staticCaptureTime = params.getCaptureTime();
        this.captureTime = this.staticCaptureTime;
        this.lootChest = params.getLootChest();
        this.lootAmount = params.getLootAmount();
        this.maxRunTime = params.getMaxRunTime() * 60;
        
        this.timeNotCapped = 0;
        this.capInfo = new CapInfo(this, this.koth, getPlugin().getCaptureTypeRegistry().getCaptureTypeClass(params.getEntityType()));
        if(getPlugin().getConfigHandler().getKoth().isRemoveChestAtStart()){
            koth.removeLootChest();
        }
        new MessageBuilder(Lang.KOTH_PLAYING_STARTING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
    }
    
    @Override
    public Capper<?> getCapper(){
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
        boolean shouldTriggerLoot = true;
      
        if (reason == EndReason.WON || reason == EndReason.GRACEFUL) {
            if (capInfo.getCapper() != null) {
                new MessageBuilder(Lang.KOTH_PLAYING_WON).maxTime(maxRunTime).capper(capInfo.getCapper().getName()).koth(koth).exclude(capInfo.getCapper(), koth).buildAndBroadcast();
                new MessageBuilder(Lang.KOTH_PLAYING_WON_CAPPER).maxTime(maxRunTime).capper(capInfo.getCapper().getName()).koth(koth).buildAndSend(capInfo.getCapper(), koth);
            }
        } else if (reason == EndReason.TIMEUP) {
            MessageBuilder mB;
            if(getPlugin().getConfigHandler().getKoth().isFfaChestTimeLimit()){
                mB = new MessageBuilder(Lang.KOTH_PLAYING_TIME_UP_FREEFORALL);
            } else {
                mB = new MessageBuilder(Lang.KOTH_PLAYING_TIME_UP);
                shouldTriggerLoot = false;
            }
            
            mB.maxTime(maxRunTime).koth(koth).buildAndBroadcast();
        } else if(reason == EndReason.FORCED){
            shouldTriggerLoot = false;
        }


        KothEndEvent event = new KothEndEvent(this, capInfo.getCapper(), reason);
        event.setTriggerLoot(shouldTriggerLoot);
        
        Bukkit.getServer().getPluginManager().callEvent(event);

        koth.setLastWinner(capInfo.getCapper());
        if (event.isTriggerLoot()) {
            Bukkit.getScheduler().runTask(getPlugin(), () -> koth.triggerLoot(lootAmount, lootChest));
        }
        
        
        final KothClassic thisObj = this;
        Bukkit.getScheduler().runTask(getPlugin(), () -> getPlugin().getKothHandler().removeRunningKoth(thisObj));
    }

    public void update() {
        timeRunning++;
        timeNotCapped++;
        
        
        // Handling capture cooldown
        if (captureCooldown > 0) {
            captureCooldown--;
            return;
        }
        // Capture info update and cooldown initiator
        CapStatus status = capInfo.update();
        
        if(prevStatus == CapStatus.CAPPING && status == CapStatus.EMPTY){
            captureCooldown = getPlugin().getConfigHandler().getKoth().getCaptureCooldown();
        }
        prevStatus = status;
        
        if(status == CapStatus.CHANNELING) prevStatus = CapStatus.EMPTY;
        if(status == CapStatus.KNOCKED) prevStatus = CapStatus.CAPPING;
        if(status == CapStatus.CONTESTED) prevStatus = CapStatus.CAPPING;
        ////////

        
        if (capInfo.getCapper() != null) {
            timeNotCapped = 0;
            if (capInfo.getTimeCapped() < captureTime) {
                if (capInfo.getTimeCapped() % getPlugin().getConfigHandler().getKoth().getBroadcastInterval() == 0 && capInfo.getTimeCapped() != 0 && status != CapStatus.CONTESTED) {
                    new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME).maxTime(maxRunTime).time(getTimeObject()).capper(capInfo.getCapper().getName()).koth(koth).exclude(capInfo.getCapper(), koth).buildAndBroadcast();
                    new MessageBuilder(Lang.KOTH_PLAYING_CAPTIME_CAPPER).maxTime(maxRunTime).time(getTimeObject()).capper(capInfo.getCapper().getName()).koth(koth).buildAndSend(capInfo.getCapper(), koth);
                }
            } else {
                endKoth(EndReason.WON);
            }
            return;
        }

        // Capture Decrementation!
        ConfigHandler.Koth.CapDecrementation capDec = getPlugin().getConfigHandler().getKoth().getCapDecrementation();
        if(capDec.isEnabled()){
            captureTime = staticCaptureTime - (int)Math.floor(timeRunning/capDec.getEveryXSeconds()) * capDec.getDecreaseBy();
            captureTime = Math.max(captureTime, capDec.getMinimum());
        }
        
        if(getPlugin().getConfigHandler().getGlobal().getNoCapBroadcastInterval() != 0 && timeNotCapped % getPlugin().getConfigHandler().getGlobal().getNoCapBroadcastInterval() == 0) {
            new MessageBuilder(Lang.KOTH_PLAYING_NOT_CAPPING).maxTime(maxRunTime).time(getTimeObject()).koth(koth).buildAndBroadcast();
        }

        if (maxRunTime > 0 && timeRunning > maxRunTime) {
            endKoth(EndReason.TIMEUP);
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
        obj.put("capperType", getPlugin().getCaptureTypeRegistry().getIdentifierFromClass(capInfo.getOfType()));
        obj.put("capperTime", capInfo.getTimeCapped());
        if(capInfo.getCapper() != null){
            obj.put("capperEntity", capInfo.getCapper().getUniqueObjectIdentifier());
        }
        
        obj.put("captureTime", this.captureTime);
        obj.put("lootChest", this.lootChest);
        obj.put("lootAmount", this.lootAmount);
        obj.put("captureCooldown", this.captureCooldown);
        obj.put("maxRunTime", this.maxRunTime);
        obj.put("timeRunning", this.timeRunning);

        return obj;
    }
    
    public KothClassic load(JSONObject obj){
        this.koth = getPlugin().getKothHandler().getKoth((String)obj.get("koth"));
        this.capInfo = new CapInfo(this, this.koth, getPlugin().getCaptureTypeRegistry().getCaptureClass((String)obj.get("capperType")));
        this.capInfo.setTimeCapped((int) (long) obj.get("capperTime"));
        if(obj.containsKey("capperEntity")){
            this.capInfo.setCapper(getPlugin().getCaptureTypeRegistry().getCapperFromType((String)obj.get("capperType"), (String)obj.get("capperEntity")));
        }
        
        this.captureTime = (int) (long) obj.get("captureTime");
        this.lootChest = (String) obj.get("lootChest");
        this.lootAmount = (int) (long) obj.get("lootAmount");
        this.captureCooldown = (int) (long) obj.get("captureCooldown");
        this.maxRunTime = (int) (long) obj.get("maxRunTime");
        this.timeRunning = (int) (long) obj.get("timeRunning");
        
        return this;
    }
}

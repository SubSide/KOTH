package subside.plugins.koth.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.gamemodes.TimeObject;
import subside.plugins.koth.modules.ConfigHandler.Hooks.BossBar.Flags;
import subside.plugins.koth.utils.MessageBuilder;

public class BossbarHook extends AbstractRangeHook {
    private @Getter @Setter boolean enabled = false;
    private String text;
    private String barColor;
    private int barSegments;
    private boolean countingDown;
    
    private BossBar bossBar;
    
    private boolean createfog;
    private boolean darkensky;
    private boolean playmusic;
    
    public BossbarHook(HookManager hookManager) {
        super(hookManager, 
                hookManager.getPlugin().getConfigHandler().getHooks().getBossBar().getRange(), 
                hookManager.getPlugin().getConfigHandler().getHooks().getBossBar().getRangeMargin());
        
        subside.plugins.koth.modules.ConfigHandler.Hooks.BossBar bbHook = hookManager.getPlugin().getConfigHandler().getHooks().getBossBar();
        
        enabled = bbHook.isEnabled();
        text = bbHook.getText();
        barColor = bbHook.getBarColor();
        barSegments = bbHook.getBarsegments();
        countingDown = bbHook.isCountingDown();
        
        Flags flags = bbHook.getFlags();
        createfog = flags.isCreatefog();
        darkensky = flags.isDarkensky();
        playmusic = flags.isPlaymusic();
        
        getPlugin().getLogger().log(Level.INFO, "Bossbar hook: "+(enabled?"Enabled":"Disabled"));
        
    }
    
    
    @Override
    public void initialize(RunningKoth koth){
        BarFlag[] flags = new BarFlag[(createfog ? 1 : 0) + (darkensky ? 1 : 0) + (playmusic ? 1 : 0)];
        
        int fl = -1;
        if(createfog) flags[++fl] = BarFlag.CREATE_FOG;
        if(darkensky) flags[++fl] = BarFlag.DARKEN_SKY;
        if(playmusic) flags[++fl] = BarFlag.PLAY_BOSS_MUSIC;
        
        BarColor color = BarColor.valueOf(barColor.toUpperCase());
        
        BarStyle barStyle = (barSegments == 1) ? BarStyle.SOLID : BarStyle.valueOf("SEGMENTED_"+barSegments);
        
        bossBar = Bukkit.getServer().createBossBar(text, color, barStyle, flags);
    }

    @Override
    public void uninitialize(){
        if(bossBar == null) return;
        
        bossBar.removeAll();
        bossBar.setVisible(false);
    }
    
    @Override
    public void update(){
        if(bossBar == null) return;
        
        TimeObject time = getKoth().getTimeObject();
        
        String title = this.getKoth().fillMessageBuilder(new MessageBuilder(text).koth(this.getKoth().getKoth())).build()[0];
        double progress = (double)time.getTotalSecondsCapped()/(double)time.getLengthInSeconds();
        
        if(countingDown)
            progress = 1.0 - progress;

        bossBar.setTitle(title);
        bossBar.setProgress(progress);
    }
    

    @Override
    public void entersRange(Player player) {
        bossBar.addPlayer(player);
    }

    @Override
    public void leavesRange(Player player) {
        bossBar.removePlayer(player);
    }
    
}

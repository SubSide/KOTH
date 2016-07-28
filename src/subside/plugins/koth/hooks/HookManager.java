package subside.plugins.koth.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import lombok.Getter;
import subside.plugins.koth.hooks.plugins.VanishHook;

public class HookManager {
    private static @Getter HookManager hookManager;
    private @Getter List<IHook> hooks;
    
    public HookManager(){
        hookManager = this;
        hooks = new ArrayList<>();
        init();
    }
    
    private void init(){
        registerHook(new VanishHook());
    }
    
    public void registerHook(IHook hook){
        hooks.add(hook);
    }
    
    public boolean canCap(Player player){
        for(IHook hook : hooks){
            if(!hook.isEnabled()) continue;
            if(!hook.canCap(player)) return false;
        }
        
        return true;
    }
}

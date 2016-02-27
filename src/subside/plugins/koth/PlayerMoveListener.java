package subside.plugins.koth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import subside.plugins.koth.adapter.KothHandler;

public class PlayerMoveListener implements Listener {
	@SuppressWarnings("deprecation")
    @EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		KothHandler.getInstance().handleMoveEvent(event.getPlayer());
	}
}

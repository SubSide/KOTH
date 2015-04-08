package subside.plugins.koth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import subside.plugins.koth.area.KothHandler;

public class PlayerMoveListener implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		KothHandler.handleMoveEvent(event.getPlayer());
	}
}

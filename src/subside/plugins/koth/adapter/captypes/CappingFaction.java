package subside.plugins.koth.adapter.captypes;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.adapter.Capable;
import subside.plugins.koth.adapter.Koth;

public interface CappingFaction {
	public String getUniqueClassIdentifier();
    public String getUniqueObjectIdentifier();
    public boolean isInOrEqualTo(OfflinePlayer oPlayer);
    public String getName();
    public Object getObject();
    public boolean areaCheck(Capable cap);
    public List<Player> getAvailablePlayers(Koth koth);
}

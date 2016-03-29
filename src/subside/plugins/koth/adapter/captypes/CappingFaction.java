package subside.plugins.koth.adapter.captypes;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.adapter.Capable;
import subside.plugins.koth.adapter.Koth;

public abstract class CappingFaction extends Capper {
	public abstract String getUniqueClassIdentifier();
    public abstract String getUniqueObjectIdentifier();
    public abstract boolean isInOrEqualTo(OfflinePlayer oPlayer);
    public abstract String getName();
    public abstract Object getObject();
    public abstract boolean areaCheck(Capable cap);
    public abstract List<Player> getAvailablePlayers(Koth koth);
}

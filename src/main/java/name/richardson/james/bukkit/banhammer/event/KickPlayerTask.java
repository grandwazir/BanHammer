package name.richardson.james.bukkit.banhammer.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import name.richardson.james.bukkit.banhammer.record.BanRecord;

public class KickPlayerTask implements Runnable {

	private final Server server;
	private final Map<String, String> players = new HashMap<String, String>();

	public KickPlayerTask(Server server, Collection<BanRecord> records) {
		this.server = server;
		for (BanRecord record : records) {
			players.put(record.getPlayer().getLastKnownName(), PlayerListener.getKickMessage(record));
		}
	}

	@Override public void run() {
		for (Map.Entry<String, String> entry : players.entrySet())	{
			Player player = server.getPlayerExact(entry.getKey());
			if (player == null) continue;
			if (player.isOnline()) player.kickPlayer(entry.getValue());
		}
	}

}

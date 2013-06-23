package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import static org.easymock.EasyMock.*;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.plugin.Plugin;

public class PlayerNotifierTest {

	public static String[] PLAYER_NAMES = {"grandwazir", "Sergeant_Subtle"};

	public static Player[] getPlayers() {
		List<Player> players = new ArrayList<Player>(PLAYER_NAMES.length);
		for (String playerName : PLAYER_NAMES) {
			Player player = createNiceMock(Player.class);
			expect(player.getName()).andReturn(playerName).atLeastOnce();
			players.add(player);
		}
		expect(players.get(0).hasPermission((String) anyObject())).andReturn(true).atLeastOnce();
		expect(players.get(1).hasPermission((String) anyObject())).andReturn(false).atLeastOnce();
		return players.toArray(new Player[players.size()]);
	}

	@Before
	public void setUp()
	throws Exception {
		Server server = createNiceMock(Server.class);
		Plugin plugin = createNiceMock(Plugin.class);
		PluginManager pluginManager = createNiceMock(PluginManager.class);
		Player[] players = getPlayers();
		expect(server.getOnlinePlayers()).andReturn(players).atLeastOnce();
		expect(server.getPluginManager()).andReturn(pluginManager);
		replay(server);
	}

	@Test
	public void testOnPlayerBanned()
	throws Exception {

	}

	@Test
	public void testOnPlayerPardoned()
	throws Exception {

	}

}

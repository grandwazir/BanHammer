package name.richardson.james.bukkit.banhammer.ban.event;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.alias.persistence.PlayerNameRecord;
import name.richardson.james.bukkit.alias.persistence.PlayerNameRecordManager;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AliasBannedPlayerListenerTest extends TestCase {

	private AliasBannedPlayerListener listener;
	private PlayerNameRecordManager playerNameRecordManager;
	private PlayerRecord playerRecord;
	private PlayerRecordManager playerRecordManager;

	@Test
	public void testOnPlayerPardoned()
	throws Exception {
		BanRecord banRecord = mock(BanRecord.class, RETURNS_DEEP_STUBS);
		when(banRecord.getPlayer().getName()).thenReturn("frank");
		when(banRecord.getReason()).thenReturn("Alias of joe");
		PlayerNameRecord playerNameRecord = mock(PlayerNameRecord.class);
		when(playerNameRecord.getPlayerName()).thenReturn("joe");
		when(playerNameRecordManager.find(anyString())).thenReturn(playerNameRecord);
		BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(banRecord, null, false);
		listener.onPlayerPardoned(event);
		verify(playerNameRecordManager, times(2)).find(anyString());
		verify(playerNameRecord, times(1)).removeAssociation(playerNameRecord);
	}

	@Test
	public void testIsPlayerBannedByAlias()
	throws Exception {
		when(playerRecordManager.exists("joe")).thenReturn(true);
		when(playerRecordManager.create(anyString())).thenReturn(playerRecord);
		when(playerRecordManager.find("joe")).thenReturn(playerRecord);
		when(playerRecord.isBanned()).thenReturn(true);
		PlayerNameRecord playerNameRecord = mock(PlayerNameRecord.class);
		when(playerNameRecord.getPlayerName()).thenReturn("joe");
		when(playerNameRecordManager.find(anyString())).thenReturn(playerNameRecord);
		when(playerNameRecord.getAliases()).thenReturn(new HashSet<String>(Arrays.asList("joe")));
		assertTrue("Player should be banned", listener.isPlayerBanned("frank"));
	}

	@Before
	public void setUp()
	throws Exception {
		Plugin plugin = mock(Plugin.class);
		PluginManager pluginManager = mock(PluginManager.class);
		playerRecordManager = mock(PlayerRecordManager.class, RETURNS_MOCKS);
		playerNameRecordManager = mock(PlayerNameRecordManager.class);
		playerRecord = mock(PlayerRecord.class, RETURNS_MOCKS);
		listener = new AliasBannedPlayerListener(plugin, pluginManager, playerRecordManager, playerNameRecordManager);
	}
}

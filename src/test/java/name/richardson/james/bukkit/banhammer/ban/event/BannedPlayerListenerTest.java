package name.richardson.james.bukkit.banhammer.ban.event;

import java.sql.Timestamp;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.formatters.colours.ColourScheme;
import name.richardson.james.bukkit.utilities.formatters.localisation.ResourceBundles;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.*;

public class BannedPlayerListenerTest extends TestCase {

	private BannedPlayerListener listener;
	private PlayerRecordManager playerRecordManager;
	private Server server;

	@Before
	public void setUp()
	throws Exception {
		Plugin plugin = mock(Plugin.class);
		PluginManager pluginManager = mock(PluginManager.class);
		server = mock(Server.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		listener = new BannedPlayerListener(plugin, pluginManager, server, playerRecordManager);
	}

	@Test
	public void testGetColourScheme()
	throws Exception {
		assertNotNull("Class should have a colour scheme associated to it!", listener.getColourScheme());
	}

	@Test
	public void testGetColouredMessage()
	throws Exception {
		assertTrue("Message does not appear to have a colour code within it!", listener.getColouredMessage(ColourScheme.Style.INFO, "banned-permanently").contains("§"));
	}

	@Test
	public void testGetKickMessagePermanent() {
		BanRecord banRecord = getExampleBanRecord();
		when(banRecord.getType()).thenReturn(BanRecord.Type.PERMANENT);
		Assert.assertEquals("Kick message is inconsistent!", listener.getKickMessage(banRecord), "§cYou have been permanently banned by §efrank§c.\n\nReason: §enull§c.");
	}

	@Test
	public void testGetKickMessageTemporary() {
		BanRecord banRecord = getExampleBanRecord();
		when(banRecord.getType()).thenReturn(BanRecord.Type.TEMPORARY);
		Assert.assertEquals("Kick message is inconsistent!", listener.getKickMessage(banRecord), "§cYou have been banned by §efrank§c until §enull§c.\n\nReason: §enull§c.");
	}

	@Test
	public void testGetMessage()
	throws Exception {
		assertTrue("Message does not appear to have been translated!", listener.getMessage("banned-permanently").contentEquals("You have been permanently banned by {1}.\\n\\nReason: {0}."));
	}

	@Test
	public void testGetResourceBundle()
	throws Exception {
		Assert.assertEquals("Resource bundle should be the same as the Messages bundle!", listener.getResourceBundle(), ResourceBundles.MESSAGES.getBundle());
	}

	@Test
	public void testOnPlayerBanned()
	throws Exception {
		Player player = mock(Player.class);
		when(player.isOnline()).thenReturn(true);
		BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(getExampleBanRecord(), true);
		when(server.getPlayerExact(anyString())).thenReturn(player);
		listener.onPlayerBanned(event);
		verify(player).kickPlayer(anyString());
	}

	@Test
	public void testPlayerLoginAllowed()
	throws Exception {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(false);
		when(playerRecordManager.exists("frank")).thenReturn(true);
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		Player player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(player, "", null);
		AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent = new AsyncPlayerPreLoginEvent("frank", null);
		listener.onPlayerLogin(playerLoginEvent);
		Assert.assertEquals("Player should be allowed to login!", playerLoginEvent.getResult(), PlayerLoginEvent.Result.ALLOWED);
		listener.onPlayerLogin(asyncPlayerPreLoginEvent);
		Assert.assertEquals("Player should be allowed to login!", asyncPlayerPreLoginEvent.getLoginResult(), AsyncPlayerPreLoginEvent.Result.ALLOWED);
		verify(playerRecord, times(2)).isBanned();
	}

	@Test
	public void testPlayerLoginDenied()
	throws Exception {
		Player player = mock(Player.class);
		PlayerRecord playerRecord = getExamplePlayerRecord();
		when(playerRecord.isBanned()).thenReturn(true);
		BanRecord banRecord = getExampleBanRecord();
		when(playerRecord.getActiveBan()).thenReturn(banRecord);
		when(playerRecordManager.exists(anyString())).thenReturn(true);
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(player, "", null);
		AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent = new AsyncPlayerPreLoginEvent("frank", null);
		listener.onPlayerLogin(playerLoginEvent);
		Assert.assertEquals("Player should not be allowed to login!", PlayerLoginEvent.Result.KICK_BANNED, playerLoginEvent.getResult());
		listener.onPlayerLogin(asyncPlayerPreLoginEvent);
		Assert.assertEquals("Player should not be allowed to login!", AsyncPlayerPreLoginEvent.Result.KICK_BANNED, asyncPlayerPreLoginEvent.getLoginResult());
		verify(playerRecord, times(2)).isBanned();
	}

	@Test
	public void testPlayerRecordNotFound() {
		when(playerRecordManager.exists(anyString())).thenReturn(false);
		Player player = mock(Player.class);
		PlayerLoginEvent playerLoginEvent = new PlayerLoginEvent(player, "", null);
		AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent = new AsyncPlayerPreLoginEvent("frank", null);
		listener.onPlayerLogin(playerLoginEvent);
		Assert.assertEquals("Player should be allowed to login!", playerLoginEvent.getResult(), PlayerLoginEvent.Result.ALLOWED);
		listener.onPlayerLogin(asyncPlayerPreLoginEvent);
		Assert.assertEquals("Player should be allowed to login!", asyncPlayerPreLoginEvent.getLoginResult(), AsyncPlayerPreLoginEvent.Result.ALLOWED);
	}

	private BanRecord getExampleBanRecord() {
		BanRecord banRecord = mock(BanRecord.class);
		when(banRecord.getType()).thenReturn(BanRecord.Type.PERMANENT);
		when(banRecord.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
		PlayerRecord playerRecord = getExamplePlayerRecord();
		when(banRecord.getCreator()).thenReturn(playerRecord);
		when(banRecord.getPlayer()).thenReturn(playerRecord);
		return banRecord;
	}

	private PlayerRecord getExamplePlayerRecord() {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.getName()).thenReturn("frank");
		return playerRecord;
	}
}

package name.richardson.james.bukkit.banhammer.ban.event;

import java.sql.Timestamp;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.banhammer.BanHammer;
import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PlayerNotifierTest extends TestCase {

	private PlayerNotifier notifier;
	private Plugin plugin;
	private PluginManager pluginManager;
	private Server server;

	@Test
	public void testOnPlayerPardoned()
	throws Exception {
		BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(getBanRecord(), false);
		notifier.onPlayerPardoned(event);
		verify(server).broadcast("§a§bfrank§a has been pardoned.", BanHammer.NOTIFY_PERMISSION_NAME);
	}

	@Test
	public void testOnPlayerBanned()
	throws Exception {
		BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(getBanRecord(), false);
		notifier.onPlayerBanned(event);
		verify(server).broadcast("§d§bfrank§d banned by §bfrank§d on §b1 Jan 1970 01:00 (GMT)§d.", BanHammer.NOTIFY_PERMISSION_NAME);
		verify(server).broadcast("§aReason: §bnull§a.", BanHammer.NOTIFY_PERMISSION_NAME);
		verify(server).broadcast("§aLength: §bPermanent§a.", BanHammer.NOTIFY_PERMISSION_NAME);
	}

	@Test
	public void testOnPlayerPardonedSilent()
	throws Exception {
		BanHammerPlayerPardonedEvent event = new BanHammerPlayerPardonedEvent(getBanRecord(), true);
		notifier.onPlayerPardoned(event);
		verify(server, never()).broadcast(anyString(), anyString());
	}

	@Test
	public void testOnPlayerBannedSilent()
	throws Exception {
		BanHammerPlayerBannedEvent event = new BanHammerPlayerBannedEvent(getBanRecord(), true);
		notifier.onPlayerBanned(event);
		verify(server, never()).broadcast(anyString(), anyString());
	}

	private BanRecord getBanRecord() {
		BanRecord banRecord = mock(BanRecord.class);
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.getName()).thenReturn("frank");
		PlayerRecord creatorRecord = mock(PlayerRecord.class);
		when(creatorRecord.getName()).thenReturn("frank");
		when(banRecord.getCreator()).thenReturn(creatorRecord);
		when(banRecord.getPlayer()).thenReturn(playerRecord);
		when(banRecord.getType()).thenReturn(BanRecord.Type.PERMANENT);
		when(banRecord.getCreatedAt()).thenReturn(new Timestamp(0));
		return banRecord;
	}

	@Test
	public void testGetMessage()
	throws Exception {
		Assert.assertNotNull("Message not resolved correctly!", notifier.getMessage("header-label"));
	}

	@Before
	public void setUp()
	throws Exception {
		plugin = mock(Plugin.class);
		pluginManager = mock(PluginManager.class);
		server = mock(Server.class);
		notifier = new PlayerNotifier(plugin, pluginManager, server);
	}
}

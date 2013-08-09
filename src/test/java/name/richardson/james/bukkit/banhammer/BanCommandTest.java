package name.richardson.james.bukkit.banhammer;

import java.lang.reflect.Field;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.avaje.ebean.EbeanServer;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.*;

public class BanCommandTest extends TestCase {

	private BanCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;
	private PluginManager pluginManager;

	private static Map<String, Long> getLimits() {
		Map<String, Long> limits = new HashMap<String, Long>();
		limits.put("test", 60000L);
		return limits;
	}

	public static Set<String> getImmunePlayers() {
		Set<String> immunePlayers = new HashSet<String>();
		immunePlayers.add("grandwazir");
		return immunePlayers;
	}

	@Test
	public void testExecuteNoPlayerName()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify the name of a player!");

	}

	@Test
	public void testExecuteNoReason() {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify a reason!");
	}

	@Test
	public void testExecutePlayerAlreadyBanned() {
		when(commandContext.has(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(true);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§e§afrank§e is already banned!");
	}

	@Test
	public void testExecuteAttemptToBanPlayerOutsideLimits() {
		when(commandContext.has(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasFlag("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("5m");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban.test")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou are not allowed to do that.");
		verify(player).hasPermission("banhammer.ban.test");
	}

	@Test
	public void testExecuteAttemptToBanImmunePlayer() {
		when(commandContext.has(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("grandwazir");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasFlag("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("1m");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("grandwazir");
		command.execute(commandContext);
		verify(player).sendMessage("§cYou are not allowed to do that.");
		verify(player).hasPermission("banhammer.ban");
	}

	@Test
	public void testExecuteBanPlayerPermanently() {
		when(commandContext.has(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§c§efrank§c has been banned.");
		verify(pluginManager).callEvent(Matchers.<Event>any());
	}

	@Test
	public void testExecuteBanPlayerWithinLimits() {
		when(commandContext.has(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasFlag("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("1m");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban.test")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§c§efrank§c has been banned.");
		verify(pluginManager).callEvent(Matchers.<Event>any());
	}


	@Before
	public void setUp()
	throws Exception {
		PermissionManager permissionManager = mock(PermissionManager.class);
		pluginManager = mock(PluginManager.class);
		EbeanServer database = mock(EbeanServer.class);
		playerRecordManager = spy(new PlayerRecordManager(database));
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
		Server server = mock(Server.class);
		when(server.getPluginManager()).thenReturn(pluginManager);
		Field field = Bukkit.class.getDeclaredField("server");
		field.setAccessible(true);
		field.set(null, server);
		Permission permission = mock(Permission.class);
		when(permissionManager.listPermissions()).thenReturn(Arrays.asList(permission));
		command = new BanCommand(permissionManager, pluginManager, playerRecordManager, getLimits(), getImmunePlayers());
	}



	@After
	public void tearDown() throws Exception {
		Field field = Bukkit.class.getDeclaredField("server");
		field.setAccessible(true);
		field.set(null, null);
	}

}

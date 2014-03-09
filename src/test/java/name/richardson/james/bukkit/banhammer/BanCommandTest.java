package name.richardson.james.bukkit.banhammer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.avaje.ebean.EbeanServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.*;

public class BanCommandTest {

	private BanCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;
	private PluginManager pluginManager;

	@Before
	public void setUp()
	throws Exception {
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
		command = new BanCommand(pluginManager, playerRecordManager, getLimits(), getImmunePlayers());
	}

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

	@After
	public void tearDown() throws Exception {
		Field field = Bukkit.class.getDeclaredField("server");
		field.setAccessible(true);
		field.set(null, null);
	}

	@Test
	public void testExecuteAttemptToBanImmunePlayer() {
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("grandwazir");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasSwitch("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("1m");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("grandwazir");
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not ban §egrandwazir§c.");
		verify(player).hasPermission("banhammer.ban");
	}

	@Test
	public void testExecuteAttemptToBanPlayerOutsideLimits() {
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasSwitch("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("5m");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban.test")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not ban §efrank§c.");
		verify(player).hasPermission("banhammer.ban.test");
	}

	@Test
	public void testExecuteBanPlayerPermanently() {
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§aYou have banned §bfrank§a.");
		verify(pluginManager).callEvent(Matchers.<Event>any());
	}

	@Test
	public void testExecuteBanPlayerWithinDefinedLimit() {
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasSwitch("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("test");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban.test")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§aYou have banned §bfrank§a.");
		verify(pluginManager).callEvent(Matchers.<Event>any());
	}

	@Test
	public void testExecuteBanPlayerWithinLimits() {
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		when(commandContext.hasSwitch("t")).thenReturn(true);
		when(commandContext.getFlag("t")).thenReturn("1m");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban.test")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§aYou have banned §bfrank§a.");
		verify(pluginManager).callEvent(Matchers.<Event>any());
	}

	@Test
	@Ignore("Do not currently have a good way to test this.")
	public void testExecuteBanSilentPlayerPermanently() {
		when(commandContext.hasSwitch("-s")).thenReturn(true);
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		when(player.hasPermission("banhammer.ban")).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§aYou have banned §bfrank§a.");
	}

	@Test
	public void testExecuteNoPlayerName()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify the name of a player.");

	}

	@Test
	public void testExecuteNoReason() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify a reason.");
	}

	@Test
	public void testExecutePlayerAlreadyBanned() {
		when(commandContext.hasArgument(anyInt())).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(commandContext.getJoinedArguments(1)).thenReturn("blah");
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(true);
		doReturn(playerRecord).when(playerRecordManager).find("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§c§efrank§c is already banned.");
	}

}

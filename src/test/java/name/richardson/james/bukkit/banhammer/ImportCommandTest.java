package name.richardson.james.bukkit.banhammer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.avaje.ebean.EbeanServer;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ImportCommandTest extends TestCase {

	private ImportCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;
	private Server server;

	@Test
	public void testExecuteNoPermission()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou are not allowed to do that.");
	}

	@Test
	public void testExecuteWithDefaultReason()
	throws Exception {
		when(player.hasPermission(anyString())).thenReturn(true);
		OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
		when(offlinePlayer.getName()).thenReturn("joe");
		when(server.getBannedPlayers()).thenReturn(new HashSet<OfflinePlayer>(Arrays.asList(offlinePlayer)));
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find(anyString());
		command.execute(commandContext);
		verify(player).sendMessage("§aImported §bone ban§a.");
	}

	@Test
	public void testExecuteWithCustomReason()
	throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("blah");
		when(player.hasPermission(anyString())).thenReturn(true);
		OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
		when(offlinePlayer.getName()).thenReturn("joe");
		when(server.getBannedPlayers()).thenReturn(new HashSet<OfflinePlayer>(Arrays.asList(offlinePlayer)));
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		doReturn(playerRecord).when(playerRecordManager).find(anyString());
		command.execute(commandContext);
		verify(player).sendMessage("§aImported §bone ban§a.");
	}

	@Before
	public void setUp()
	throws Exception {
		PermissionManager permissionManager = mock(PermissionManager.class);
		EbeanServer database = mock(EbeanServer.class);
		playerRecordManager = spy(new PlayerRecordManager(database));
		server = mock(Server.class);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
		command = new ImportCommand(permissionManager, playerRecordManager, server);
	}
}

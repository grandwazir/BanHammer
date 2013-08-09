package name.richardson.james.bukkit.banhammer;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExportCommandTest extends TestCase {

	private ExportCommand command;
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
	public void testExecute()
	throws Exception {
		when(player.hasPermission(anyString())).thenReturn(true);
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.getName()).thenReturn("ted");
		when(playerRecordManager.list(anyString(), Matchers.<PlayerRecordManager.PlayerStatus>any())).thenReturn(Arrays.asList(playerRecord));
		when(playerRecordManager.count()).thenReturn(1);
		when(server.getOfflinePlayer(anyString())).thenReturn(player);
		command.execute(commandContext);
		verify(player).setBanned(true);
		verify(player).sendMessage("§aExported §bone ban§a.");
	}

	@Before
	public void setUp()
	throws Exception {
		PermissionManager permissionManager = mock(PermissionManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		server = mock(Server.class);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
		command = new ExportCommand(permissionManager, playerRecordManager, server);
	}

}

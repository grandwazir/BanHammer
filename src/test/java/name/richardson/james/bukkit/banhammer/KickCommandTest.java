package name.richardson.james.bukkit.banhammer;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KickCommandTest extends TestCase {

	private KickCommand command;
	private CommandContext commandContext;
	private Player player;
	private Server server;

	@Test
	public void testExecuteNoNameProvided()
	throws Exception {
		when(player.hasPermission(anyString())).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify the name of a player!");
	}

	@Test
	public void testExecuteKickWithDefaultReason()
	throws Exception {
		when(player.hasPermission(anyString())).thenReturn(true);
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getPlayer(0)).thenReturn(player);
		command.execute(commandContext);
		verify(player).kickPlayer("§cYou have been kicked by §efrank§c.\n\nReason: §eNo reason provided§c.");
		verify(server).broadcast("§c§efrank§c has been kicked by §efrank§c.", BanHammer.NOTIFY_PERMISSION_NAME);
		verify(server).broadcast("§eReason: §aNo reason provided§e.", BanHammer.NOTIFY_PERMISSION_NAME);
	}

	@Test
	public void testExecuteKickWithCustomReason()
	throws Exception {
		when(player.hasPermission(anyString())).thenReturn(true);
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getPlayer(0)).thenReturn(player);
		when(commandContext.has(1)).thenReturn(true);
		when(commandContext.getJoinedArguments(1)).thenReturn("reason");
		command.execute(commandContext);
		verify(player).kickPlayer("§cYou have been kicked by §efrank§c.\n\nReason: §ereason§c.");
		verify(server).broadcast("§c§efrank§c has been kicked by §efrank§c.", BanHammer.NOTIFY_PERMISSION_NAME);
		verify(server).broadcast("§eReason: §areason§e.", BanHammer.NOTIFY_PERMISSION_NAME);
	}


	@Test
	public void testExecuteNoPermission()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou are not allowed to do that.");
	}

	@Before
	public void setUp()
	throws Exception {
		PermissionManager permissionManager = mock(PermissionManager.class);
		server = mock(Server.class);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
		command = new KickCommand(permissionManager, server);
	}
}

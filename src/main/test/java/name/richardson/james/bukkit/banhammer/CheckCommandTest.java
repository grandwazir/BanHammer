package name.richardson.james.bukkit.banhammer;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import name.richardson.james.bukkit.utilities.command.context.PassthroughCommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class CheckCommandTest extends TestCase {

	private CheckCommand command;
	private CommandSender commandSender;
	private PermissionManager permissionManager;
	private PlayerRecordManager playerRecordManager;
	private Server server;

	@Test
	public void testExecuteNoPlayerProvided()
	throws Exception {
		String[] arguments = {"12"};
		new PassthroughCommandContext(arguments, commandSender, server);

	}

	@Before
	public void setUp()
	throws Exception {
		permissionManager = mock(PermissionManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		commandSender = mock(CommandSender.class);
		server = mock(Server.class);
		command = new CheckCommand(permissionManager, playerRecordManager);
	}
}

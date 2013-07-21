package name.richardson.james.bukkit.banhammer;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class CheckCommandTest extends TestCase {

	private CheckCommand command;
	private PermissionManager permissionManager;
	private PlayerRecordManager playerRecordManager;

	@Test
	public void testExecute()
	throws Exception {

	}

	@Before
	public void setUp()
	throws Exception {
		permissionManager = mock(PermissionManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		command = new CheckCommand(permissionManager, playerRecordManager);
	}
}

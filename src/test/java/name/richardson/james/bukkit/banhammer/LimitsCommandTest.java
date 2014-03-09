package name.richardson.james.bukkit.banhammer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LimitsCommandTest {

	private LimitsCommand command;
	private CommandContext commandContext;
	private Player player;

	@Test
	public void testExecuteNoPermission()
	throws Exception {
		command.execute(commandContext);
	}

	@Test
	public void testExecute()
	throws Exception {
		when(player.hasPermission(anyString())).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§dThere are a total of §b2 limits§d configured.");
		verify(player).sendMessage("§atest (1 minute), §atest2 (10 seconds)");
	}

	private static Map<String, Long> getLimits() {
		Map<String, Long> limits = new HashMap<String, Long>();
		limits.put("test", (long) 60000);
		limits.put("test2", (long) 10000);
		return limits;
	}


	@Before
	public void setUp()
	throws Exception {
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
		command = new LimitsCommand(LimitsCommandTest.getLimits());
	}

}

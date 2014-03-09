package name.richardson.james.bukkit.banhammer;

import java.sql.Timestamp;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.command.context.PassthroughCommandContext;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class CheckCommandTest {

	private CheckCommand command;
	private CommandSender commandSender;
	private PlayerRecord playerRecord;
	private PlayerRecordManager playerRecordManager;
	private Server server;

	@Test
	public void testExecuteNoPlayerProvided()
	throws Exception {
		String[] arguments = {""};
		CommandContext context = new PassthroughCommandContext(arguments, commandSender);
		command.execute(context);
	 	verify(commandSender).sendMessage("§cYou must specify a player name.");
	}

	@Test
	public void testExecuteNoPlayerRecord()
	throws Exception {
		String[] arguments = {"frank"};
		OfflinePlayer player = mock(OfflinePlayer.class);
		when(player.getName()).thenReturn("frank");
		when(server.getOfflinePlayer(anyString())).thenReturn(player);
		CommandContext context = new PassthroughCommandContext(arguments, commandSender);
		command.execute(context);
		verify(commandSender).sendMessage("§a§bfrank§a is not banned.");
	}

	@Test
	public void testExecutePlayerNotBanned()
	throws Exception {
		String[] arguments = {"frank"};
		OfflinePlayer player = mock(OfflinePlayer.class);
		when(player.getName()).thenReturn("frank");
		when(server.getOfflinePlayer(anyString())).thenReturn(player);
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(false);
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		CommandContext context = new PassthroughCommandContext(arguments, commandSender);
		command.execute(context);
		verify(commandSender).sendMessage("§a§bfrank§a is not banned.");
		verify(playerRecord).isBanned();
	}

	@Test
	public void testExecuteNoPermission()
	throws Exception {
		when(commandSender.hasPermission(anyString())).thenReturn(false);
		String[] arguments = {"frank"};
		CommandContext context = new PassthroughCommandContext(arguments, commandSender);
		command.execute(context);
		verify(commandSender).sendMessage("§cYou may not check if players are banned.");
	}

	@Test
	public void testExecuteSuccess()
	throws Exception {
		String[] arguments = {"frank"};
		OfflinePlayer player = mock(OfflinePlayer.class);
		when(player.getName()).thenReturn("frank");
		when(server.getOfflinePlayer(anyString())).thenReturn(player);
		playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(true);
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		BanRecord ban = getMockBan();
		when(playerRecord.getActiveBan()).thenReturn(ban);
		CommandContext context = new PassthroughCommandContext(arguments, commandSender);
		command.execute(context);
		verify(commandSender).sendMessage((String[]) any());
		verify(playerRecord).isBanned();
	}

	private BanRecord getMockBan() {
		BanRecord ban = mock(BanRecord.class);
		BanRecord.BanRecordFormatter formatter = mock(BanRecord.BanRecordFormatter.class, RETURNS_DEFAULTS);
		long now = System.currentTimeMillis();
		when(ban.getCreatedAt()).thenReturn(new Timestamp(now));
		when(ban.getExpiresAt()).thenReturn(new Timestamp(now));
		when(ban.getCreator()).thenReturn(playerRecord);
		when(ban.getPlayer()).thenReturn(playerRecord);
		when(ban.getReason()).thenReturn("This is a test reason.");
		when(ban.getType()).thenReturn(BanRecord.Type.PERMANENT);
		when(ban.getFormatter()).thenReturn(formatter);
		return ban;
	}

	@Before
	public void setUp()
	throws Exception {
		playerRecordManager = mock(PlayerRecordManager.class);
		commandSender = mock(CommandSender.class);
		when(commandSender.hasPermission(anyString())).thenReturn(true);
		server = mock(Server.class);
		command = new CheckCommand(playerRecordManager);
	}

}

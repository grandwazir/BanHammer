package name.richardson.james.bukkit.banhammer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RecentCommandTest extends TestCase {

	private BanRecordManager banRecordManager;
	private RecentCommand command;
	private CommandContext commandContext;
	private Player player;

	@Test
	public void testExecuteNoPermission()
	throws Exception {
		when(player.hasPermission(RecentCommand.PERMISSION_ALL)).thenReturn(false);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou are not allowed to view recent bans.");
	}

	@Test
	public void testExecuteDefaultLimit()
	throws Exception {
		List<BanRecord> banRecords = getMockBans();
		when(banRecordManager.list(5)).thenReturn(banRecords);
		command.execute(commandContext);
		verify(player, times(5)).sendMessage(any(String[].class));
	}

	@Test
	public void testExecuteCustomLimit()
	throws Exception {
		int count = 4;
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("4");
		List<BanRecord> banRecords = getMockBans();
		when(banRecordManager.list(count)).thenReturn(banRecords);
		command.execute(commandContext);
		verify(player, times(5)).sendMessage(any(String[].class));
	}

	private BanRecord getMockBan(PlayerRecord playerRecord) {
		BanRecord ban = mock(BanRecord.class);
		long now = System.currentTimeMillis();
		when(ban.getCreatedAt()).thenReturn(new Timestamp(now));
		when(ban.getExpiresAt()).thenReturn(new Timestamp(now));
		when(ban.getCreator()).thenReturn(playerRecord);
		when(ban.getPlayer()).thenReturn(playerRecord);
		when(ban.getReason()).thenReturn("This is a test reason.");
		when(ban.getType()).thenReturn(BanRecord.Type.PERMANENT);
		BanRecord.BanRecordFormatter formatter = mock(BanRecord.BanRecordFormatter.class);
		when(ban.getFormatter()).thenReturn(formatter);
		return ban;
	}

	private PlayerRecord getMockPlayerRecord() {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(true);
		when(playerRecord.getName()).thenReturn("joe");
		return playerRecord;
	}


	private List<BanRecord> getMockBans() {
		List<BanRecord> banRecords = new ArrayList<BanRecord>();
		PlayerRecord playerRecord = getMockPlayerRecord();
		do {
			BanRecord banRecord = getMockBan(playerRecord);
			banRecords.add(banRecord);
		} while (banRecords.size() != 5);
		return banRecords;
	}

	@Before
	public void setUp()
	throws Exception {
		banRecordManager = mock(BanRecordManager.class);
		command = new RecentCommand(banRecordManager);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		when(player.hasPermission(RecentCommand.PERMISSION_ALL)).thenReturn(true);
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
	}

}

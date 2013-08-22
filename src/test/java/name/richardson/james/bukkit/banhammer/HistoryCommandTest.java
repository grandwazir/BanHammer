package name.richardson.james.bukkit.banhammer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class HistoryCommandTest extends TestCase {

	private HistoryCommand command;
	private CommandContext commandContext;
	private CommandSender commandSender;
	private PlayerRecordManager playerRecordManager;

	@Before
	public void setUp()
	throws Exception {
		PermissionManager permissionManager = mock(PermissionManager.class);
		Permission permission = mock(Permission.class);
		when(permissionManager.listPermissions()).thenReturn(Arrays.asList(permission, permission));
		playerRecordManager = mock(PlayerRecordManager.class);
		commandSender = mock(CommandSender.class);
		when(commandSender.getName()).thenReturn("console");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(commandSender);
		command = new HistoryCommand(playerRecordManager);
	}

	@Test
	public void testExecute()
	throws Exception {
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("joe");
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(commandSender.hasPermission(anyString())).thenReturn(true);
		command.execute(commandContext);
		verify(commandSender).sendMessage(any(String[].class));
	}

	@Test
	public void testExecuteFallbackName()
	throws Exception {
		command.execute(commandContext);
		verify(commandSender).getName();
	}

	@Test
	public void testExecuteNoPermissionToViewOthers()
	throws Exception {
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("joe");
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(commandSender.hasPermission(HistoryCommand.PERMISSION_OWN)).thenReturn(true);
		command.execute(commandContext);
		verify(commandSender, atLeastOnce()).sendMessage("§cYou may not view §ejoe§c's ban history.");
	}

	@Test
	public void testExecuteNoPermissionToViewSelf()
	throws Exception {
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(commandSender.hasPermission(HistoryCommand.PERMISSION_OTHERS)).thenReturn(true);
		command.execute(commandContext);
		verify(commandSender, atLeastOnce()).sendMessage("§cYou may not view §econsole§c's ban history.");
	}

	@Test
	public void testExecutePlayerHasNoRecord()
	throws Exception {
		command.execute(commandContext);
		verify(commandSender).sendMessage("§a§bconsole§a has never been banned.");
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
		playerRecord = mock(PlayerRecord.class);
		List<BanRecord> banRecords = new ArrayList<BanRecord>();
		BanRecord banRecord = getMockBan(playerRecord);
		banRecords.add(banRecord);
		when(playerRecord.getBans()).thenReturn(banRecords);
		return playerRecord;
	}
}

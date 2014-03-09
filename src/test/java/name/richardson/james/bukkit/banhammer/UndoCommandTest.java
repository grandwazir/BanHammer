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
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.*;

public class UndoCommandTest extends TestCase {

	private BanRecordManager banRecordManager;
	private UndoCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;
	private final long undoTime = 6000L;

	@Test
	public void testExecuteNoPlayerName()
	throws Exception {
		command.execute(commandContext);
	}

	@Test
	public void testExecuteNoPlayerRecord()
	throws Exception {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§e§afrank§e has never been banned.");
	}

	@Test
	public void testExecuteNoBanRecord() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecord.getActiveBan()).thenReturn(null);
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		command.execute(commandContext);
		verify(player).sendMessage("§a§bfrank§a is not banned.");
	}

	@Test
	public void testExecuteFailedNoPermissionSelf() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		when(player.hasPermission(UndoCommand.PERMISSION_OTHERS)).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not undo bans made by §efrank§c.");
	}

	@Test
	public void testExecuteFailedNoPermissionOthers() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecord.getName()).thenReturn("joe");
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		when(player.hasPermission(UndoCommand.PERMISSION_OWN)).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not undo bans made by §ejoe§c.");
	}

	@Test
	public void testExecuteFailedOutsideTimeLimit() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		when(player.hasPermission(UndoCommand.PERMISSION_OWN)).thenReturn(true);
		when(playerRecord.getActiveBan().getCreatedAt()).thenReturn(new Timestamp(0));
		command.execute(commandContext);
		verify(player).sendMessage("§cThe time to undo this ban has expired.");
	}


	@Test
	public void testExecuteSuccessWithinTimeLimit() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		when(player.hasPermission(UndoCommand.PERMISSION_OWN)).thenReturn(true);
		command.execute(commandContext);
		verify(banRecordManager).delete(playerRecord.getActiveBan());
		verify(player).sendMessage("§aThe last ban issued to §bfrank§a has been deleted.");
	}

	@Test
	public void testExecuteSuccessUnrestricted() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find("frank")).thenReturn(playerRecord);
		when(player.hasPermission(UndoCommand.PERMISSION_OWN)).thenReturn(true);
		when(player.hasPermission(UndoCommand.PERMISSION_UNRESTRICTED)).thenReturn(true);
		when(playerRecord.getActiveBan().getCreatedAt()).thenReturn(new Timestamp(0));
		command.execute(commandContext);
		verify(banRecordManager).delete(playerRecord.getActiveBan());
		verify(player).sendMessage("§aThe last ban issued to §bfrank§a has been deleted.");
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
		return ban;
	}

	private PlayerRecord getMockPlayerRecord() {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(true);
		when(playerRecord.getName()).thenReturn("frank");
		List<BanRecord> banRecords = new ArrayList<BanRecord>();
		BanRecord banRecord = getMockBan(playerRecord);
		banRecords.add(banRecord);
		when(playerRecord.getBans()).thenReturn(banRecords);
		when(playerRecord.getActiveBan()).thenReturn(banRecord);
		return playerRecord;
	}

	@Before
	public void setUp()
	throws Exception {
		playerRecordManager = mock(PlayerRecordManager.class);
		banRecordManager = mock(BanRecordManager.class);
		command = new UndoCommand(playerRecordManager, banRecordManager, undoTime);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
	}

}

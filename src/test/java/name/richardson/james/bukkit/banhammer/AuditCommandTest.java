package name.richardson.james.bukkit.banhammer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Mockito.*;

public class AuditCommandTest extends TestCase {

	private BanRecordManager banRecordManager;
	private AuditCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;

	@Test
	public void testExecuteNoNameProvided()
	throws Exception {
		when(commandContext.has(0)).thenReturn(false);
		command.execute(commandContext);
		verify(playerRecordManager).find("frank");
	}

	@Test
	public void testExecuteNoBansMade()
	throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§eNo bans have been issued by §afrank§e.");
	}

	@Test
	public void testExecuteNoPermissionToViewOthers()
	throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("ted");
		PlayerRecord playerRecord = getExamplePlayerRecord();
		when(playerRecordManager.find("ted")).thenReturn(playerRecord);
		when(player.hasPermission(AuditCommand.PERMISSION_SELF)).thenReturn(true);
		command.execute(commandContext);
		verify(player, atLeastOnce()).hasPermission(AuditCommand.PERMISSION_OTHERS);
		verify(player).sendMessage("§cYou are not allowed to do that.");
	}

	@Test
	public void testExecutePermissionToViewSelf()
	throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		when(player.hasPermission(AuditCommand.PERMISSION_OTHERS)).thenReturn(true);
		command.execute(commandContext);
		verify(player, atLeastOnce()).hasPermission(AuditCommand.PERMISSION_SELF);
		verify(player).sendMessage("§cYou are not allowed to do that.");
	}

	@Test
	public void testExecuteSuccess() {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("ted");
		when(player.hasPermission(anyString())).thenReturn(true);
		PlayerRecord playerRecord = getExamplePlayerRecord();
		when(playerRecordManager.find("ted")).thenReturn(playerRecord);
		command.execute(commandContext);
		verify(player, times(1)).sendMessage(anyString());
		verify(player, times(1)).sendMessage(any(String[].class));
	}


	private PlayerRecord getExamplePlayerRecord() {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		List<BanRecord> banRecords = new ArrayList<BanRecord>();
		BanRecord banRecord = mock(BanRecord.class);
		banRecords.add(banRecord);
		when(banRecord.getType()).thenReturn(BanRecord.Type.PERMANENT);
		when(banRecord.getState()).thenReturn(BanRecord.State.NORMAL);
		when(playerRecord.getCreatedBans()).thenReturn(banRecords);
		return playerRecord;
	}

	@Before
	public void setUp()
	throws Exception {
		PermissionManager permissionManager = mock(PermissionManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		banRecordManager = mock(BanRecordManager.class);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
		command = new AuditCommand(permissionManager, playerRecordManager, banRecordManager);
	}
}

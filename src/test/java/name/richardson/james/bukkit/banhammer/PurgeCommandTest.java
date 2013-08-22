package name.richardson.james.bukkit.banhammer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PurgeCommandTest extends TestCase {

	private BanRecordManager banRecordManager;
	private PurgeCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;
	private PluginManager pluginManager;

	@Test
	public void testExecuteNoPermission()
	throws Exception {
		when(player.hasPermission(PurgeCommand.PERMISSION_ALL)).thenReturn(false);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou are not allowed to purge bans.");
	}

	@Test
	public void testExecuteNoPlayerName()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify the name of a player!");
	}

	@Test
	public void testExecuteNoPlayerRecord()
	throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§a§bfrank§a has never been banned.");
	}

	@Test
	public void testExecutePurgeOwnBans() throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecord.getName()).thenReturn("frank");
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(player.hasPermission(PurgeCommand.PERMISSION_OWN)).thenReturn(true);
		command.execute(commandContext);
		verify(banRecordManager).delete(anyList());
		verify(player).sendMessage("§aPurged §bone ban§a associated with §bfrank§a.");
	}

	@Test
	public void testExecutePurgeAllBans() throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("joe");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(player.hasPermission(PurgeCommand.PERMISSION_OTHERS)).thenReturn(true);
		command.execute(commandContext);
		verify(banRecordManager).delete(anyList());
		verify(player).sendMessage("§aPurged §bone ban§a associated with §bjoe§a.");
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
		when(playerRecord.getName()).thenReturn("joe");
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
		PermissionManager permissionManager = mock(PermissionManager.class);
		pluginManager = mock(PluginManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		banRecordManager = mock(BanRecordManager.class);
		command = new PurgeCommand(playerRecordManager, banRecordManager);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		when(player.hasPermission(PurgeCommand.PERMISSION_ALL)).thenReturn(true);
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
	}


}

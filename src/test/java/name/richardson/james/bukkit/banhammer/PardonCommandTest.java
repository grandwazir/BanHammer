package name.richardson.james.bukkit.banhammer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;
import name.richardson.james.bukkit.utilities.permissions.PermissionManager;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PardonCommandTest extends TestCase {

	private BanRecord ban;
	private BanRecordManager banRecordManager;
	private PardonCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecord playerRecord;
	private PlayerRecordManager playerRecordManager;
	private PluginManager pluginManager;

	@Test
	public void testExecute()
	throws Exception {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(player.hasPermission(anyString())).thenReturn(true);
		command.execute(commandContext);
		verify(banRecordManager).delete(ban);
		verify(pluginManager).callEvent(Matchers.<BanHammerPlayerBannedEvent>any());
		verify(player).sendMessage("§a§bfrank§a has been pardoned.");
	}

	@Test
	public void testExecuteNoPlayer()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify the name of a player!");
	}

	@Test
	public void testExecuteNoPlayerRecord() {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§e§afrank§e is not banned.");
	}

	@Test
	public void testExecuteNoPermission() {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not pardon bans made by §ejoe§c.");
	}

	@Test
	public void testExecuteNoPermissionToPardonOthers() {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(player.hasPermission(PardonCommand.PERMISSION_OWN)).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not pardon bans made by §ejoe§c.");
	}

	@Test
	public void testExecuteNoPermissionToPardonSelf() {
		when(commandContext.has(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(playerRecord.getName()).thenReturn("frank");
		when(player.hasPermission(PardonCommand.PERMISSION_OTHERS)).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not pardon bans made by §efrank§c.");
	}

	private BanRecord getMockBan(PlayerRecord playerRecord) {
		ban = mock(BanRecord.class);
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
		playerRecord = mock(PlayerRecord.class);
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
		banRecordManager = mock(BanRecordManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		pluginManager = mock(PluginManager.class);
		command = new PardonCommand(permissionManager, pluginManager, banRecordManager, playerRecordManager);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
	}

}

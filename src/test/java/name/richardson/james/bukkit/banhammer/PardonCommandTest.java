package name.richardson.james.bukkit.banhammer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import name.richardson.james.bukkit.utilities.command.context.CommandContext;

import name.richardson.james.bukkit.banhammer.ban.BanRecord;
import name.richardson.james.bukkit.banhammer.ban.BanRecordManager;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecord;
import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;
import name.richardson.james.bukkit.banhammer.ban.event.BanHammerPlayerBannedEvent;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PardonCommandTest extends TestCase {

	private BanRecord ban;
	private BanRecordManager banRecordManager;
	private PardonCommand command;
	private CommandContext commandContext;
	private Player player;
	private PlayerRecordManager playerRecordManager;
	private PluginManager pluginManager;

	@Before
	public void setUp()
	throws Exception {
		banRecordManager = mock(BanRecordManager.class);
		playerRecordManager = mock(PlayerRecordManager.class);
		pluginManager = mock(PluginManager.class);
		command = new PardonCommand(pluginManager, banRecordManager, playerRecordManager);
		player = mock(Player.class);
		when(player.getName()).thenReturn("frank");
		commandContext = mock(CommandContext.class);
		when(commandContext.getCommandSender()).thenReturn(player);
	}

	@Test
	public void testExecute()
	throws Exception {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(player.hasPermission(anyString())).thenReturn(true);
		command.execute(commandContext);
		verify(banRecordManager).save(ban);
		verify(pluginManager).callEvent(Matchers.<BanHammerPlayerBannedEvent>any());
		verify(player).sendMessage("§a§bfrank§a has been pardoned.");
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

	@Test
	public void testExecuteNoPermission() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not pardon §efrank§c.");
	}

	@Test
	public void testExecuteNoPermissionToPardonOthers() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(player.hasPermission(PardonCommand.PERMISSION_OWN)).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not pardon §efrank§c.");
	}

	@Test
	public void testExecuteNoPermissionToPardonSelf() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		PlayerRecord playerRecord = getMockPlayerRecord();
		when(playerRecordManager.find(anyString())).thenReturn(playerRecord);
		when(playerRecord.getName()).thenReturn("frank");
		when(player.hasPermission(PardonCommand.PERMISSION_OTHERS)).thenReturn(true);
		command.execute(commandContext);
		verify(player).sendMessage("§cYou may not pardon §efrank§c.");
	}

	@Test
	public void testExecuteNoPlayer()
	throws Exception {
		command.execute(commandContext);
		verify(player).sendMessage("§cYou must specify the name of a player.");
	}

	@Test
	public void testExecuteNoPlayerRecord() {
		when(commandContext.hasArgument(0)).thenReturn(true);
		when(commandContext.getString(0)).thenReturn("frank");
		command.execute(commandContext);
		verify(player).sendMessage("§a§bfrank§a is not banned.");
	}

}

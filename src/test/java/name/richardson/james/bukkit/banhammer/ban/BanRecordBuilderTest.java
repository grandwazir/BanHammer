package name.richardson.james.bukkit.banhammer.ban;

import java.util.UUID;

import com.avaje.ebean.EbeanServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import name.richardson.james.bukkit.banhammer.record.BanRecordBuilder;
import name.richardson.james.bukkit.banhammer.record.PlayerRecord;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerRecord.class)
public class BanRecordBuilderTest {

	private static final UUID PLAYER_UUID = UUID.fromString("89590f58-a3dc-4ecb-87bc-b4fc3535fba3");
	private static final String PLAYER_NAME = "grandwazir";
	private static final String REASON = "Test Reason";
	@Mock private PlayerRecord creator;
	@Mock private EbeanServer database;
	@Mock private PlayerRecord player;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(PlayerRecord.class);
		when(PlayerRecord.create(database, PLAYER_NAME)).thenReturn(player);
	}

	@Test
	public void whenCreatingBanRecordBuilderWithPlayerNameAttributesShouldBeSetCorrectly() {
		when(PlayerRecord.create(database, PLAYER_NAME)).thenReturn(player);
		when(PlayerRecord.create(database, PLAYER_UUID)).thenReturn(creator);
		final BanRecordBuilder builder = new BanRecordBuilder(database, PLAYER_NAME, PLAYER_UUID, REASON);
		assertEquals("Reason should be equal to the reason provided to the constructor!", REASON, builder.getRecord().getReason());
		assertEquals("Player should have resolved to mocked PlayerRecord!", player, builder.getRecord().getPlayer());
		assertEquals("Creator should have resolved to mocked PlayerRecord!", creator, builder.getRecord().getCreator());
	}

	@Test
	public void whenCreatingBanRecordBuilderWithPlayerUUIDAttributesShouldBeSetCorrectly() {
		when(PlayerRecord.create(database, PLAYER_UUID)).thenReturn(player, creator);
		final BanRecordBuilder builder = new BanRecordBuilder(database, PLAYER_UUID, PLAYER_UUID, REASON);
		assertEquals("Reason should be equal to the reason provided to the constructor!", REASON, builder.getRecord().getReason());
		assertEquals("Player should have resolved to mocked PlayerRecord!", player, builder.getRecord().getPlayer());
		assertEquals("Creator should have resolved to mocked PlayerRecord!", creator, builder.getRecord().getCreator());
	}

	@Test
	public void whenCreatingBanRecordWithPlayerNameUpdateLastKnownPlayerName() {
		when(PlayerRecord.create(database, PLAYER_UUID)).thenReturn(player, creator);
		final BanRecordBuilder builder = new BanRecordBuilder(database, PLAYER_NAME, PLAYER_UUID, REASON);
		verify(player).setLastKnownName(PLAYER_NAME);
	}

	@Test
	public void whenCreatingBanRecordWithPlayerUUIDUpdateName() {
		when(PlayerRecord.create(database, PLAYER_UUID)).thenReturn(player, creator);
		final BanRecordBuilder builder = new BanRecordBuilder(database, PLAYER_UUID, PLAYER_UUID, REASON);
		verify(player).updateName();
	}

	@Test
	public void whenSettingExpiryTimeEnsureTimeDifferenceCorrect() {
		when(PlayerRecord.create(database, PLAYER_UUID)).thenReturn(player, creator);
		final BanRecordBuilder builder = new BanRecordBuilder(database, PLAYER_UUID, PLAYER_UUID, REASON);
		final int expected = 60;
		builder.setExpiryTime(expected);
		Assert.assertEquals("The length of the ban is not consistant!", expected, builder.getRecord().getExpiresAt().getTime() - builder.getRecord().getCreatedAt().getTime());
	}

	@Test
	public void shouldCommitToDatabaseOnSave() {
		when(PlayerRecord.create(database, PLAYER_UUID)).thenReturn(player, creator);
		final BanRecordBuilder builder = new BanRecordBuilder(database, PLAYER_NAME, PLAYER_UUID, REASON);
		builder.save();
		verify(database).save(builder.getRecord());
	}

}

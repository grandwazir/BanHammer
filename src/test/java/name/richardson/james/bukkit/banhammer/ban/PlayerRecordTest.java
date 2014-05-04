package name.richardson.james.bukkit.banhammer.ban;

import java.util.UUID;

import com.avaje.ebean.EbeanServer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class PlayerRecordTest {

	private static UUID PLAYER_UUID = UUID.fromString("89590f58-a3dc-4ecb-87bc-b4fc3535fba3");
	private static String PLAYER_NAME = "grandwazir";

	private EbeanServer database;

	@Before
	public void setUp() {
		this.database = mock(EbeanServer.class, RETURNS_DEEP_STUBS);
	}

	@Test
	public void whenFindingUsingUUIDReturnRecord() {
		final UUID uuid = UUID.randomUUID();
		final PlayerRecord playerRecord = new PlayerRecord();
		when(database.find(PlayerRecord.class).where().eq("uuid", uuid).findUnique()).thenReturn(playerRecord);
		assertSame("Returned record should be the same as the one supplied to mocked method!", playerRecord, PlayerRecord.find(database, uuid));
	}

	@Test
	public void whenFindingUsingNameReturnRecord() {
		final PlayerRecord playerRecord = new PlayerRecord();
		when(database.find(PlayerRecord.class).where().eq("uuid", PLAYER_UUID).findUnique()).thenReturn(playerRecord);
		assertSame("Returned record should be the same as the one supplied to mocked method!", playerRecord, PlayerRecord.find(database, PLAYER_NAME));
	}

	@Test
	public void whenCreatingUsingUUIDAndRecordIsNonExistentReturnNewRecord() {
		ArgumentCaptor<PlayerRecord> recordCaptor = ArgumentCaptor.forClass(PlayerRecord.class);
		when(database.find(PlayerRecord.class).where().eq("uuid", PLAYER_UUID).findUnique()).thenReturn(null, new PlayerRecord(), null);
		PlayerRecord.create(database, PLAYER_UUID);
		verify(database).save(recordCaptor.capture());
		assertEquals("Newly created record should have UUID set correctly!", recordCaptor.getValue().getUuid(), PLAYER_UUID);
	}

	@Test
	public void whenCreatingUsingNameAndRecordIsNonExistentReturnNewRecord() {
		ArgumentCaptor<PlayerRecord> recordCaptor = ArgumentCaptor.forClass(PlayerRecord.class);
		when(database.find(PlayerRecord.class).where().eq("uuid", PLAYER_UUID).findUnique()).thenReturn(null, new PlayerRecord(), null);
		PlayerRecord.create(database, PLAYER_NAME);
		verify(database).save(recordCaptor.capture());
		assertEquals("Newly created record should have UUID set correctly!", recordCaptor.getValue().getUuid(), PLAYER_UUID);
	}

	@Test
	public void whenResolvingNameFromUUIDReturnCorrectName() {
		final PlayerRecord playerRecord = new PlayerRecord();
		playerRecord.setUuid(PLAYER_UUID);
		assertEquals("Player name should have been looked up correctly!", playerRecord.getCurrentName(), PLAYER_NAME);
	}

}

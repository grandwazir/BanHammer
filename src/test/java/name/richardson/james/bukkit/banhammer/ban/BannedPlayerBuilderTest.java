package name.richardson.james.bukkit.banhammer.ban;

import java.sql.Timestamp;
import java.util.Arrays;

import com.avaje.ebean.EbeanServer;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class BannedPlayerBuilderTest extends TestCase {

	private PlayerRecordManager.BannedPlayerBuilder builder;
	private EbeanServer database;
	private PlayerRecordManager manager;

	@Test
	public void testSetCreator() throws Exception {
		builder.setCreator("frank");
		verify(manager).create("frank");
	}

	@Test
	public void testSetPlayer() throws Exception {
		builder.setPlayer("frank");
		verify(manager).create("frank");
	}

	@Test
	public void testSetReason() throws Exception {
		builder.setReason("fred");
		assertSame("Reason is not consistent!", "fred", builder.getRecord().getReason());
	}

	@Test
	public void testSave() {
		when(manager.getDatabase()).thenReturn(database);
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(manager.create("frank")).thenReturn(playerRecord);
		builder.setPlayer("frank");
		builder.save();
		verify(database).save(anyObject());
	}

	@Test
	public void testSetExpiresTime() throws Exception {
		builder.setExpiryTime(500);
		assertTrue("Expiry time has not been offset correctly!", (builder.getRecord().getExpiresAt().getTime() - 500) == builder.getRecord().getCreatedAt().getTime());
	}


	@Test
	public void testSetExpiresAt() throws Exception {
		builder.setExpiresAt(new Timestamp(System.currentTimeMillis()));
		assertNotNull("Expiry time has not been set!", builder.getRecord().getExpiresAt());
		assertEquals("Expiry time has not been offset correctly!", builder.getRecord().getExpiresAt(), builder.getRecord().getCreatedAt());
	}

	@Test
	public void testCreatedTime() throws Exception {
		assertNotNull("Creation time should be set when object is created", builder.getRecord().getCreatedAt());
	}

	@Before
	public void setUp() {
		database = mock(EbeanServer.class);
		manager = mock(PlayerRecordManager.class);
		when(manager.getBannedPlayerBuilder()).thenCallRealMethod();
		builder = manager.getBannedPlayerBuilder();
	}

}

package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;
import java.util.List;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BanRecordManagerTest extends TestCase {

	private EbeanServer database;
	private BanRecordManager manager;

	@Test
	public void testCount()
	throws Exception {
		int count = 1;
		Query<BanRecord> query = mock(Query.class);
		when(query.findRowCount()).thenReturn(count);
		when(database.find(BanRecord.class)).thenReturn(query);
		assertEquals("Count is inconsistent!", count, manager.count());
	}

	@Test
	public void testList()
	throws Exception {
		Query<BanRecord> query = mock(Query.class);
		BanRecord banRecord = mock(BanRecord.class);
		List<BanRecord> banRecordList = Arrays.asList(banRecord);
		when(query.findList()).thenReturn(banRecordList);
		when(query.setMaxRows(5)).thenReturn(query);
		when(database.find(BanRecord.class)).thenReturn(query);
		Assert.assertEquals("BanRecordList is not the same!", banRecordList, manager.list());
		Assert.assertEquals("BanRecordList is not the same!", banRecordList, manager.list(5));
		verify(query, times(1)).setMaxRows(5);
	}

	@Test
	public void testSaveFalse()
	throws Exception {
		BanRecord banRecord = mock(BanRecord.class);
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(banRecord.getPlayer()).thenReturn(playerRecord);
		when(playerRecord.isBanned()).thenReturn(true);
		Assert.assertFalse("Record should not have been saved!", manager.save(banRecord));
		verify(database, never()).save(banRecord);
	}

	@Test
	public void testSaveTrue()
	throws Exception {
		BanRecord banRecord = mock(BanRecord.class);
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(banRecord.getPlayer()).thenReturn(playerRecord);
		when(playerRecord.isBanned()).thenReturn(false);
		Assert.assertTrue("Record should have saved without incident!", manager.save(banRecord));
		verify(database).save(banRecord);
	}

	@Test
	public void testDelete()
	throws Exception {
		BanRecord banRecord = mock(BanRecord.class);
		manager.delete(banRecord);
		verify(database).delete(Arrays.asList(banRecord));
	}

	@Before
	public void setUp()
	throws Exception {
		database = mock(EbeanServer.class);
		manager = new BanRecordManager(database);
	}

}

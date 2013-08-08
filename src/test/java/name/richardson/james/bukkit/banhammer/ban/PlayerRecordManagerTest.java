package name.richardson.james.bukkit.banhammer.ban;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.*;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class PlayerRecordManagerTest extends TestCase {

	private EbeanServer database;
	private PlayerRecordManager manager;
	private Query<PlayerRecord> query;

	@Test
	public void testSave()
	throws Exception {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		manager.save(playerRecord);
		verify(database, times(1)).save(Arrays.asList(playerRecord));
	}

	@Test
	public void testListAll()
	throws Exception {
		setExamplePlayerRecords();
		Assert.assertEquals("Collection should be empty!", Collections.emptyList(), manager.list());
	}

	@Test
	public void testListAny()
	throws Exception {
		setExamplePlayerRecords();
		Assert.assertEquals("Collection should contain two records!", 2, manager.list("", PlayerRecordManager.PlayerStatus.ANY).size());
	}


	@Test
	public void testListBanned()
	throws Exception {
		setExamplePlayerRecords();
		Assert.assertEquals("Collection should contain only one record!", 1, manager.list("", PlayerRecordManager.PlayerStatus.BANNED).size());
	}


	@Test
	public void testListCreators()
	throws Exception {
		setExamplePlayerRecords();
		Assert.assertEquals("Collection should contain only one record!", 1, manager.list("", PlayerRecordManager.PlayerStatus.CREATOR).size());

	}

	private void setExamplePlayerRecords() {
		ExpressionList<PlayerRecord> expressionList = mock(ExpressionList.class);
		setExamplePlayerRecords(expressionList);
	}

	private void setExamplePlayerRecords(ExpressionList<PlayerRecord> expressionList) {
		when(query.where()).thenReturn(expressionList);
		when(expressionList.istartsWith(anyString(), anyString())).thenReturn(expressionList);
		List<PlayerRecord> playerRecords = new ArrayList<PlayerRecord>();
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		when(playerRecord.isBanned()).thenReturn(true);
		playerRecords.add(playerRecord);
		playerRecord = mock(PlayerRecord.class);
		List<BanRecord> banRecords = new ArrayList<BanRecord>();
		BanRecord banRecord = mock(BanRecord.class);
		banRecords.add(banRecord);
		when(playerRecord.getCreatedBans()).thenReturn(banRecords);
		playerRecords.add(playerRecord);
		when(expressionList.findList()).thenReturn(playerRecords);
	}

	@Test
	public void testExists()
	throws Exception {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		ExpressionList<PlayerRecord> expressionList = mock(ExpressionList.class);
		when(query.where()).thenReturn(expressionList);
		when(expressionList.ieq(anyString(), anyString())).thenReturn(expressionList);
		when(expressionList.findUnique()).thenReturn(playerRecord);
		Assert.assertTrue("Player should exist!", manager.exists("frank"));
	}

	@Test
	public void testDoesNotExist()
	throws Exception {
		ExpressionList<PlayerRecord> expressionList = mock(ExpressionList.class);
		when(query.where()).thenReturn(expressionList);
		when(expressionList.ieq(anyString(), anyString())).thenReturn(expressionList);
		when(expressionList.findUnique()).thenReturn(null);
		Assert.assertFalse("Player should not exist!", manager.exists("frank"));
	}

	@Test(expected=PersistenceException.class)
	public void testDuplicateFound() throws Exception {
		ExpressionList<PlayerRecord> expressionList = mock(ExpressionList.class);
		when(query.where()).thenReturn(expressionList);
		when(expressionList.ieq(anyString(), anyString())).thenReturn(expressionList);
		when(expressionList.findUnique()).thenThrow(new PersistenceException());
		setExamplePlayerRecords(expressionList);
		manager.find("frank");
		verify(database, times(1)).delete(anyObject());
	}


	@Test
	public void testDelete()
	throws Exception {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		manager.delete(playerRecord);
		verify(database, times(1)).delete(anyCollection());
	}

	@Test
	public void testCreateNew()
	throws Exception {
		ExpressionList<PlayerRecord> expressionList = mock(ExpressionList.class);
		when(query.where()).thenReturn(expressionList);
		when(expressionList.ieq(anyString(), anyString())).thenReturn(expressionList);
		when(expressionList.findUnique()).thenReturn(null);
		manager.create("frank");
		verify(database, times(1)).save(anyCollection());
		verify(expressionList, times(2)).findUnique();
	}

	@Test
	public void testCreateAlreadyExisting()
	throws Exception {
		PlayerRecord playerRecord = mock(PlayerRecord.class);
		ExpressionList<PlayerRecord> expressionList = mock(ExpressionList.class);
		when(query.where()).thenReturn(expressionList);
		when(expressionList.ieq(anyString(), anyString())).thenReturn(expressionList);
		when(expressionList.findUnique()).thenReturn(playerRecord);
		manager.create("frank");
		verify(database, never()).save(anyCollection());
		verify(expressionList, times(1)).findUnique();
	}

	@Test
	public void testCount()
	throws Exception {
		int count = 0;
		when(query.findRowCount()).thenReturn(count);
		Assert.assertEquals("Row count is inconsistent!", count, manager.count());
		verify(query, atLeastOnce()).findRowCount();
	}


	@Before
	public void setUp()
	throws Exception {
		database = mock(EbeanServer.class);
		query = mock(Query.class);
		when(database.find(PlayerRecord.class)).thenReturn(query);
		manager = new PlayerRecordManager(database);
	}

}

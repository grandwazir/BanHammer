package name.richardson.james.bukkit.banhammer.record;

import com.avaje.ebean.EbeanServer;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.banhammer.ban.PlayerRecordManager;

public class PlayerRecordManagerTest extends TestCase {

	private EbeanServer database;
	private PlayerRecordManager manager;

	@Test
	public void testUpdate()
	throws Exception {

	}


	@Test
	public void testSave()
	throws Exception {

	}

	@Test
	public void testList()
	throws Exception {

	}

	@Test
	public void testFind()
	throws Exception {

	}

	@Test
	public void testExists()
	throws Exception {

	}

	@Test
	public void testDelete()
	throws Exception {

	}

	@Test
	public void testCreate()
	throws Exception {

	}

	@Test
	public void testCount()
	throws Exception {
		EasyMock.expect(database.find((Class) EasyMock.anyObject()).findRowCount()).andReturn(1).atLeastOnce();
		EasyMock.replay(database);
		int count = manager.count();
		EasyMock.verify(database);
		Assert.assertTrue(count == 1);
	}

	@Before
	public void setUp()
	throws Exception {
		database = EasyMock.createNiceMock(EbeanServer.class);
		manager = new PlayerRecordManager(database);
	}
}

package name.richardson.james.bukkit.banhammer.ban;

import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerRecordTest extends TestCase {

	private PlayerRecord record;

	@Test
	public void testIsBannedDefault()
	throws Exception {
		Assert.assertFalse("Player should not be banned!", record.isBanned());
	}

	@Test
	public void testIsBannedFalse()
	throws Exception {
		BanRecord ban = mock(BanRecord.class);
		when(ban.getState()).thenReturn(BanRecord.State.EXPIRED);
		record.setBans(Arrays.asList(ban));
		Assert.assertFalse("Player should not be banned!", record.isBanned());
		verify(ban, atLeastOnce()).getState();
	}

	@Test
	public void testIsBannedTrue()
	throws Exception {
		BanRecord ban = mock(BanRecord.class);
		when(ban.getState()).thenReturn(BanRecord.State.NORMAL);
		record.setBans(Arrays.asList(ban));
		Assert.assertTrue("Player should be banned!", record.isBanned());
		verify(ban, atLeastOnce()).getState();
	}


	@Test
	public void testSetName()
	throws Exception {
		String name = "frank";
		record.setName(name);
		Assert.assertEquals("Name is inconsistent!", name, record.getName());
	}

	@Test
	public void testSetId()
	throws Exception {
		int id = 1;
		record.setId(id);
		Assert.assertEquals("Id is inconsistent!", id, record.getId());
	}

	@Test
	public void testSetCreatedBans()
	throws Exception {
		BanRecord ban = mock(BanRecord.class);
		record.setCreatedBans(Arrays.asList(ban));
		Assert.assertSame("Set bans are not consistent!", ban, record.getCreatedBans().get(0));
	}

	@Test
	public void testSetBans()
	throws Exception {
		BanRecord ban = mock(BanRecord.class);
		record.setBans(Arrays.asList(ban));
		Assert.assertSame("Set bans are not consistent!", ban, record.getBans().get(0));
	}

	@Test
	public void testGetActiveBan()
	throws Exception {
		BanRecord activeBan = mock(BanRecord.class);
		when(activeBan.getState()).thenReturn(BanRecord.State.NORMAL);
		BanRecord inactiveBan = mock(BanRecord.class);
		when(inactiveBan.getState()).thenReturn(BanRecord.State.PARDONED);
		record.setBans(Arrays.asList(activeBan, inactiveBan));
		Assert.assertSame("Ban returned is not the active ban!", activeBan, record.getActiveBan());
	}

	@Test
	public void testToString()
	throws Exception {
		Assert.assertTrue("toString is not overriden.", record.toString().contains(PlayerRecord.class.getSimpleName()));
	}


	@Before
	public void setUp()
	throws Exception {
		record = new PlayerRecord();
	}
}

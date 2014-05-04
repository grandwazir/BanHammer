package name.richardson.james.bukkit.banhammer.ban;

import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.Test;

public class BanRecordTest {

	@Test
	public void whenExpiresAtIsInTheFutureStateShouldNotBeExpired() {
		final BanRecord record = new BanRecord();
		final long future = System.currentTimeMillis() + 5000;
		final Timestamp time = new Timestamp(future);
		final BanRecord.State state = BanRecord.State.PARDONED;
		record.setState(state);
		record.setExpiresAt(time);
		Assert.assertEquals("When expiresAt is in the future, the state of the ban should be whatever was persisted.", state, record.getState());
	}

	@Test
	public void whenExpiresAtIsInThePastStateShouldBeExpired() {
		final BanRecord record = new BanRecord();
		Timestamp time = new Timestamp(0);
		record.setExpiresAt(time);
		Assert.assertEquals("When expiresAt is in the past, the state of the ban should be EXPIRED.", BanRecord.State.EXPIRED, record.getState());
	}

	@Test
	public void whenExpiresAtIsPresentStateTypeShouldBeTemporary() {
		final BanRecord record = new BanRecord();
		Timestamp time = new Timestamp(0);
		record.setExpiresAt(time);
		Assert.assertEquals("When expiresAt is present the type of the ban should be TEMPORARY.", BanRecord.Type.TEMPORARY, record.getType());
	}

	@Test
	public void whenExpiresAtIsNotPresentStateTypeShouldBePermanent() {
		final BanRecord record = new BanRecord();
		Assert.assertEquals("When expiresAt is not present the type of the ban should be PERMANENT.", BanRecord.Type.PERMANENT, record.getType());
	}

}

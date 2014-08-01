package name.richardson.james.bukkit.banhammer.argument;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import name.richardson.james.bukkit.utilities.command.argument.Argument;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleTimeMarshallerTest {

	private Argument argument;
	private SimpleTimeMarshaller marshaller;

	@Before
	public void setup() {
		argument = mock(Argument.class);
		marshaller = new SimpleTimeMarshaller(argument);
	}

	@Test
	public void returnZeroWhenInvalidStringFormatProvided() {
		when(argument.getString()).thenReturn("blah");
		Assert.assertEquals("Parsed time should be 0 when provided value is incorrectly formatted!", 0, marshaller.getTime());
	}

	@Test
	public void returnCorrectTimeWhenValidStringFormatProvided() {
		when(argument.getString()).thenReturn("1m30s");
		Assert.assertEquals("Parsed time should be 90 when provided value is incorrectly formatted!", 90000, marshaller.getTime());
	}

}

package name.richardson.james.bukkit.banhammer;

import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class PluginConfigurationTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private PluginConfiguration configuration;

	@Test
	public void testIsAliasEnabled()
	throws Exception {
		assertFalse("Default should be false!", configuration.isAliasEnabled());
	}

	@Test
	public void testGetUndoTime()
	throws Exception {
		assertEquals("Default undo time should be 1 minute (in milliseconds)", 60000L, configuration.getUndoTime());
	}

	@Test
	public void testGetImmunePlayers()
	throws Exception {
		assertEquals("Immune player list should contain 1 name.", 1, configuration.getImmunePlayers().size());
	}

	@Test
	public void testGetBanLimits()
	throws Exception {
		assertEquals("Limit list should contain 4 limits.", 4, configuration.getBanLimits().size());
	}

	@Test
	@Ignore
	public void testGetBanLimitsWithInvalidValue()
	throws Exception {
		InputStream defaults = getClass().getClassLoader().getResourceAsStream("config-invalid.yml");
		PluginConfiguration invalidConfiguration = new PluginConfiguration(folder.newFile("config-invalid.yml"), defaults);
		assertEquals("Limit list should contain 3 limits.", 3, invalidConfiguration.getBanLimits().size());
	}


	@Before
	public void setUp()
	throws Exception {
		InputStream defaults = getClass().getClassLoader().getResourceAsStream("config.yml");
		configuration = new PluginConfiguration(folder.newFile("config.yml"), defaults);
	}

}

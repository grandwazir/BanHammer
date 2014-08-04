package name.richardson.james.bukkit.banhammer;

import com.avaje.ebean.config.ServerConfig;

import name.richardson.james.bukkit.utilities.persistence.DatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.EntityPersistenceController;
import name.richardson.james.bukkit.utilities.persistence.SQLiteDatabaseLoader;
import name.richardson.james.bukkit.utilities.persistence.configuration.DatabaseConfiguration;

import name.richardson.james.bukkit.banhammer.model.BanRecord;
import name.richardson.james.bukkit.banhammer.model.CommentRecord;
import name.richardson.james.bukkit.banhammer.model.PlayerRecord;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestDatabaseFactory {

	private static SQLiteDatabaseLoader SQLITE_DATABASE_LOADER;

	public static synchronized DatabaseLoader getSQLiteDatabaseLoader() {
		if (SQLITE_DATABASE_LOADER == null) {
			ServerConfig serverConfig = new ServerConfig();
			serverConfig.getDataSourceConfig().setUrl("jdbc:sqlite::memory:");
			serverConfig.getDataSourceConfig().setPassword("");
			serverConfig.getDataSourceConfig().setUsername("travis");
			serverConfig.getDataSourceConfig().setDriver("org.sqlite.JDBC");
			serverConfig.getDataSourceConfig().setIsolationLevel(8);
			serverConfig.setName("BanHammer");
			serverConfig.getClasses().add(PlayerRecord.class);
			serverConfig.getClasses().add(BanRecord.class);
			serverConfig.getClasses().add(CommentRecord.class);
			serverConfig.add(new EntityPersistenceController());
			DatabaseConfiguration configuration = mock(DatabaseConfiguration.class);
			when(configuration.getServerConfig()).thenReturn(serverConfig);
			when(configuration.getDataSourceConfig()).thenReturn(serverConfig.getDataSourceConfig());
			SQLITE_DATABASE_LOADER = new SQLiteDatabaseLoader(configuration);
			SQLITE_DATABASE_LOADER.initalise();
		}
		return SQLITE_DATABASE_LOADER;
	}

}
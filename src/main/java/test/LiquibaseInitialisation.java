package test;

import com.google.common.io.Resources;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@ApplicationScoped
public class LiquibaseInitialisation {

    public static final String LIQUIBASE_CONFIG = "liquibase.properties";

    private static final String LIQUIBASE_SCHEMANAME = "liquibase.schemaname";
    private static final String LIQUIBASE_H2_CHANGELOG_FILE = "liquibase.h2_changelog";

    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseInitialisation.class);

    private LiquibaseInitialisation() {
        // intentionally hide implicit constructor
    }

    /**
     * Initialisation of database with liquibase that is supposed to be used in @before of integration tests.
     *
     * @param connectionString the connection string for the database
     */
    public static void initH2(final String connectionString) {
        URL fileUrl = Resources.getResource(LIQUIBASE_CONFIG);
        String h2config;
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            h2config = readConfigFilesProperty(fileUrl, LIQUIBASE_H2_CHANGELOG_FILE);
            String schema = readConfigFilesProperty(fileUrl, LIQUIBASE_SCHEMANAME);

            try (Statement stat = connection.createStatement()) {
                stat.execute("CREATE SCHEMA " + schema);
            }

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(h2config,
                    new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());
            connection.commit();
        } catch (LiquibaseException | SQLException | IOException e) {
            LOG.error("Liquibase cannot be instantiated and run for H2 initialisation!", e);
        }
    }

    /**
     * Initializes a properties object with values from the given resource such as host, sender, etc.
     *
     * @return the initialized properties object or <code>null</code> if resource is invalid
     * @throws IOException
     */
    private static String readConfigFilesProperty(URL configUrl, String property) throws IOException {

        Properties fileProperties = new Properties();

        try (InputStream input = configUrl.openStream()) {

            // load the properties file
            fileProperties.load(input);

            if (fileProperties.getProperty(property) == null) {
                LOG.error("cannot read {}", property);
                return null;
            }
            return fileProperties.getProperty(property);
        }
    }
}

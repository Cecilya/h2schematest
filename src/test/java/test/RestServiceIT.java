package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class RestServiceIT {

    private static final String BASE_URI = "/h2schematest/rest";

    private static final String H2_CONNECTION_STRING = "jdbc:h2:mem:testdb;INIT=CREATE SCHEMA IF NOT EXISTS a\\;SET SCHEMA a\\;CREATE SCHEMA IF NOT EXISTS b\\;";

    private static final String SERVICE_URL = "http://localhost:" + EmbeddedServer.PORT;
    /**
     * Client timeout in seconds to not block the unit tests.
     **/
    private static final int CLIENT_TIMEOUT = 60;

    private static Connection connection;
    private static ResteasyWebTarget target;

    protected static ObjectMapper jsonParser;

    @BeforeClass
    public static void execOnce() {
        initialiseTarget();
    }

    private static void initialiseTarget() {
        ResteasyJackson2Provider resteasyJacksonProvider = new ResteasyJackson2Provider();
        resteasyJacksonProvider.setMapper(jsonParser);

        ResteasyClient client = new ResteasyClientBuilder()
                .connectionCheckoutTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .establishConnectionTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .socketTimeout(CLIENT_TIMEOUT, TimeUnit.SECONDS)
                .register(resteasyJacksonProvider)
                .build();
        target = client.target(SERVICE_URL);
    }

    @Before
    public void initializeDatabase() throws Exception {
        System.setProperty("active_env", "unittest");
        connection = DriverManager.getConnection(H2_CONNECTION_STRING);
        Statement stat = connection.createStatement();
        stat.execute("GRANT ALTER ANY SCHEMA TO PUBLIC");
        LiquibaseInitialisation.initH2(H2_CONNECTION_STRING);
        EmbeddedServer.startServer();

        Logger logger = LoggerFactory.getLogger("test");

        Statement statement = connection.createStatement();
        statement.execute("SELECT * FROM b.modelb;");
        connection.commit();

        ResultSet resultSet = statement.getResultSet();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        logger.warn("Printing SQL result now:");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= columnsNumber; i++) {
            builder.append(rsmd.getColumnName(i));
            builder.append(" | ");
        }
        logger.warn(builder.toString());


        while (resultSet.next()) {
            builder = new StringBuilder();
            for (int i = 1; i <= columnsNumber; i++) {
                builder.append(resultSet.getString(i));
                builder.append(" ");
            }
            logger.warn(builder.toString());
        }
    }

    @After
    public void execAfter() throws Exception {
        Statement stat = connection.createStatement();
        // H2 special query to reset DB
        stat.execute("DROP ALL OBJECTS");
        connection.close();
    }

    @Test
    public void accessSchemaA() {
        String path = BASE_URI + "/schemas" + "/a";
        Response response = target.path(path).request().get();

        assertEquals("Should return OK", Response.Status.OK.getStatusCode(), response.getStatus());

        List<ModelARest> result = response.readEntity(new GenericType<List<ModelARest>>() {
        });
        response.close();

        assertTrue(!result.isEmpty());
        assertTrue(result.get(0) != null);
        assertEquals("testdata for a", result.get(0).getField());
    }

    @Test
    public void accessSchemaB() {
        String path = BASE_URI + "/schemas" + "/b";
        Response response = target.path(path).request().get();

        assertEquals("Should return OK", Response.Status.OK.getStatusCode(), response.getStatus());

        List<ModelARest> result = response.readEntity(new GenericType<List<ModelARest>>() {
        });
        response.close();

        assertTrue(!result.isEmpty());
        assertTrue(result.get(0) != null);
        assertEquals("testdata for b", result.get(0).getField());
    }

}

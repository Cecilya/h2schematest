package test;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DatabaseAccessorIT {

    private static final String H2_CONNECTION_STRING = "jdbc:h2:mem:testdb;INIT=CREATE SCHEMA IF NOT EXISTS a\\;SET SCHEMA a\\;CREATE SCHEMA IF NOT EXISTS b\\;";

    private static Connection connection;


    @Before
    public void initializeDatabase() throws Exception {
        System.setProperty("active_env", "unittest");
        connection = DriverManager.getConnection(H2_CONNECTION_STRING);
        Statement stat = connection.createStatement();
        stat.execute("GRANT ALTER ANY SCHEMA TO PUBLIC");
        LiquibaseInitialisation.initH2(H2_CONNECTION_STRING);

        Logger logger = LoggerFactory.getLogger("test");

        /*
         Log the contents of table b.modelb to the console to make sure that a) there is a table and b) it has data.
        */
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

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet rs = databaseMetaData.getColumns("TESTDB", "B","MODELB", null);
        logger.warn("Has columns: " + rs.next());
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
        DatabaseAccessor service = new DatabaseAccessor();
        List<ModelA> result = service.getFromSchemaA();

        assertTrue(!result.isEmpty());
        assertTrue(result.get(0) != null);
        assertEquals("testdata for a", result.get(0).getField());
    }

    @Test
    public void accessSchemaB() {
        DatabaseAccessor service = new DatabaseAccessor();
        List<ModelB> result = service.getFromSchemaB();

        assertTrue(!result.isEmpty());
        assertTrue(result.get(0) != null);
        assertEquals("testdata for b", result.get(0).getAttribute());
    }

}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.starburstdata;

import oracle.jdbc.OracleConnection;
import picocli.CommandLine;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@CommandLine.Command(
        name = "test_connection",
        usageHelpAutoWidth = true
)
public class TestConnectionCommand
        implements Runnable
{
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit")
    public boolean usageHelpRequested;

    @CommandLine.Option(names = {"-u", "--user"}, required = true, description = "Oracle username")
    public String user;

    @CommandLine.Option(names = {"-p", "--password"}, required = true, description = "Oracle password")
    public String password;

    @CommandLine.Option(names = {"--jdbcUrl"}, required = true, description = "JDBC URL for Oracle connection")
    public String jdbcUrl;

    public static String DEFAULT_TIMEOUT = "10000";

    private TestConnectionCommand() {}

    @Override
    public void run()
    {
        try {
            Connection connection = createJdbcConnection();
            Statement statement = connection.createStatement();
            long start = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery(getTestQuery());
            while (resultSet.next()) {
                System.out.println("sysdate: " + resultSet.getString(1));
            }
            resultSet.close();
            long end = System.currentTimeMillis();
            System.out.println("finished in " + (end - start) + " ms");
            statement.close();
            connection.close();
        }
        catch (Exception e)
        {
            System.err.println("failure creating JDBC connection or executing query");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String getTestQuery()
    {
        return "SELECT sysdate FROM dual";
    }

    private Connection createJdbcConnection()
            throws SQLException, IOException
    {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, DEFAULT_TIMEOUT);
        return DriverManager.getConnection(jdbcUrl, properties);
    }
}


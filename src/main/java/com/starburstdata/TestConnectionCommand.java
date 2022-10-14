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

    @CommandLine.Option(names = "--oracle-config", required = true, description = "Properties file with Oracle connection config")
    public String configFilename;

    private TestConnectionCommand() {}

    @Override
    public void run()
    {
        List<String> queries = getQueriesToRun();
        try {
            Connection connection = createJdbcConnection();
            for (String query : queries) {
                System.out.println("Query: " + query);
                Statement statement = connection.createStatement();
                long start = System.currentTimeMillis();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    System.out.print("  ");
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        System.out.print(resultSet.getMetaData().getColumnName(i) + ", ");
                    }
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        String column;
                        if (resultSet.getMetaData().getColumnType(i) == Types.DATE) {
                            column = resultSet.getDate(i).toString();
                        }
                        else {
                            column = resultSet.getString(i);
                        }
                        System.out.print(column + ",");
                    }
                    System.out.println("");
                }
                resultSet.close();
                statement.close();
                long end = System.currentTimeMillis();
                System.out.println("  finished in " + (end - start) + " ms");
            }
            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("failure creating JDBC connection or executing query");
            e.printStackTrace();
        }
    }

    private List<String> getQueriesToRun()
    {
        return new LinkedList<String>(Arrays.asList("SELECT sysdate FROM dual"));
    }

    private Connection createJdbcConnection()
            throws SQLException, IOException
    {
        Properties connectionProperties = getConnectionProperties(configFilename);
        Properties properties = new Properties();
        /*properties.setProperty("user", connectionProperties.getProperty("user"));
        properties.setProperty("password", connectionProperties.getProperty("password"));
        properties.setProperty("SSL", "true");
        properties.setProperty("SSLVerification", "NONE");*/
        return DriverManager.getConnection(connectionProperties.getProperty("url"), properties);
    }

    private Properties getConnectionProperties(String configFile)
            throws IOException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(configFile));
        return props;
    }
}


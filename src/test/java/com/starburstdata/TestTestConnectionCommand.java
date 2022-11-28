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

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestTestConnectionCommand
{
    private JdbcDatabaseContainer<OracleContainer> container;

    @BeforeClass
    public final void setup()
    {
        container = new OracleContainer("gvenzl/oracle-xe:18.4.0-slim")
                .withPassword("starburst")
                .withEnv("ORACLE_PASSWORD", "starburst");
        container.start();
    }

    @AfterClass(alwaysRun = true)
    public void close()
    {
        container.close();
    }

    @Test
    public void testOracleUnavailable()
    {
        TestCli cli = TestCli.cli(
                "test_connection",
                "--user=" + container.getUsername(),
                "--password=" + container.getPassword(),
                "--jdbcUrl=jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 65432))(CONNECT_DATA = (SERVICE_NAME = ORCLPDB1)))"
        );
        cli.err();
    }

    @Test
    public void testOracleAvailable()
    {
        TestCli cli = TestCli.cli(
                "test_connection",
                "--user=" + container.getUsername(),
                "--password=" + container.getPassword(),
                "--jdbcUrl=" + container.getJdbcUrl()
        );
        cli.out();
    }
}

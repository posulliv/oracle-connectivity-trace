simple java class to execute `select sysdate from dual` against a remote oracle database 
with trace logging for the JDBC driver enabled.

# Instructions

1. compile with `mvn clean install -DskipTests`

2. Create `oraclelog.properties` with following contents:

```
.level=ALL
oracle.jdbc.level=ALL
oracle.jdbc.handlers=java.util.logging.FileHandler
java.util.logging.FileHandler.level=ALL
java.util.logging.FileHandler.pattern=jdbc.log
java.util.logging.FileHandler.count=1
java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter
```

3. Run like so:

```
java -Doracle.jdbc.Trace=true -Djava.util.logging.config.file=oraclelog.properties -jar ./target/simple-oracle-jdbc-test-1.0-SNAPSHOT-executable.jar test_connection --user ORACLE_USER --password ORACLE_PASSWORD --jdbcUrl "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))(CONNECT_DATA = (SERVICE_NAME = ORCLPDB1)))" 
```

The `jdbcUrl` parameter can in any valid format that the Oracle JDBC driver supports.

4. Oracle trace logs will be in `jdbc.log`

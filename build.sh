javac -d bin -classpath postgresql-42.7.8.jar PostgreSQLJDBCConnection.java
jar cfm PostgreSQLJDBCConnection.jar ./META-INF/MANIFEST.MF -C bin/ .

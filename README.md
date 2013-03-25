# Overview

This repository contains my (Joshua Mark Rutherford) response to the OPOWER Connection Pool Homework. This file now describes the assumptions, modifications and additions made during the completion of this homework assignment.  Lastly, instructions are provided for the compilation and test of my respsonse.

In addtion to this information, the code is documented extensively and can provide additional information on usage.

## Assumptions

As the only requirement explicitly defined within the homework assignment is the implementation of the ConnectionPool interface, I have made a number of assumptions as to the desired behaviour behind that interface.  As these assumptions have shaped my implementation, I am providing them here to facilitate the evaluation of my work.

- The solution should not rely upon any existing third party connection or object pool implementations.
- The solution should provide an option to bound the minimum and maximum number of connections to be maintained by the connection pool.
- The solution should not allow consumers to physically close connections.
- The solution should provide an option to recover idle connections.
- The solution should ensure that connections are valid prior to providing them to consumers.
- The solution should attempt to replace invalid connections with valid connections.

## Modifications

The following modifications have been made to the existing files:

- pom.xml - Added a version element for the maven-compiler-plugin to remove maven warnings, set the artifact version to 1.0.0 and added the javadoc plugin for javadoc generation.
- README.md - Added solution specific information.

## Additions

The following additions have been made:

- .gitignore - Added to the project to prevent the inclusion of platform, environment and build files.
- /src/main/java/com/opower/connectionpool/ConnectionPoolImpl.java - Contains the ConnectionPool implementation.
- /src/main/java/com/opower/connectionpool/WrappedCallableStatement.java - Defines a CallableStatement wrapper implementation that allows derived classes to override the functionality of any third party CallableStatement implementation.
- /src/main/java/com/opower/connectionpool/WrappedConnection.java - Defines a Connection wrapper implementation that allows derived classes to override the functionality of any third party Connection implementation.
- /src/main/java/com/opower/connectionpool/WrappedDatabaseMetaData.java - Defines a DatabaseMetaData wrapper implementation that allows derived classes to override the functionality of any third party DatabaseMetaData implementation.
- /src/main/java/com/opower/connectionpool/WrappedPreparedStatement.java - Defines a PreparedStatement wrapper implementation that allows derived classes to override the functionality of any third party PreparedStatement implementation.
- /src/main/java/com/opower/connectionpool/WrappedResultSet.java - Defines a ResultSet wrapper implementation that allows derived classes to override the functionality of any third party ResultSet implementation.
- /src/main/java/com/opower/connectionpool/WrappedStatement.java - Defines a Statement wrapper implementation that allows derived classes to override the functionality of any third party Statement implementation.
- /src/test/java/com/opower/connectionpool/ConnectionPoolImplTest.java - Defines the tests for the ConnectionPoolImpl class. 
- /src/test/java/com/opower/connectionpool/WrappedCallableStatementTest.java - Defines the tests for the WrappedCallableStatement class.
- /src/test/java/com/opower/connectionpool/WrappedConnectionTest.java - Defines the tests for the WrappedConnection class.
- /src/test/java/com/opower/connectionpool/WrappedDatabaseMetaDataTest.java - Defines the tests for the WrappedDatabaseMetaData class.
- /src/test/java/com/opower/connectionpool/WrappedPreparedStatementTest.java - Defines the tests for the WrappedPreparedStatement class.
- /src/test/java/com/opower/connectionpool/WrappedResultSetTest.java - Defines the tests for the WrappedResultSet class.
- /src/test/java/com/opower/connectionpool/WrappedStatementTest.java - Defines the tests for the WrappedStatement class.
- /src/test/java/com/opower/connectionpool/WrapperTester.java - Defines a test utility class that tests any wrapper class that meets certain requirements.

## Instructions

My project is hosted on [github](https://github.com/roostergator/com.opower.connectionpool). Note that I have not included any environment specific files in my github repository but these can be provided on request.

As no significant changes to the pom.xml file have been made, the instructions for use of my solution have not really changed.

    mvn compile      # compiles your code in src/main/java
    mvn test-compile # compile test code in src/test/java
    mvn test         # run tests in src/test/java for files named Test*.java
    mvn install      # installs project in your repo and also builds the Javadoc jar

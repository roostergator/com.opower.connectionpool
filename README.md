# Overview

This repository contains my (Joshua Mark Rutherford) response to the OPOWER Connection Pool Homework. This file now describes the assumptions, modifications and additions made during the completion of this homework assignment.  Lastly, instructions are provided for the compilation and test of my respsonse.

## Assumptions

As the only requirement explicitly defined within the homework assignment is the implementation of the ConnectionPool interface, I have made a number of assumptions as to the desired behaviour behind that interface.  As these assumptions have shaped my implementation, I am providing them here to facilitate the evaluation of my work.

The solution should not rely upon any existing third party connection pool implementation.  This includes any implmentations that may be exposed through the javax.sql.DataSource interface.  As a result, I have chosen to utilize the legacy java.sql.DriverManager.  Refactoring the connection pool implementation to utilize the javax.sql.DataSource interface should be possible with very little effort.

## Modifications

The following modifications have been made to the existing files:

pom.xml - Added a version element for the maven-compiler-plugin to remove maven warnings. 

## Additions

The following additions have been made:

.gitignore - Added to the project to prevent the inclusion of platform, environment and build files.

## Instructions

My project is hosted on [github][github]. Note that I have not included any environment specific files in my github repository but these can be provided on request.

As no significant changes to the pom.xml file have been made, the instructions for use of my solution have not changed.

    mvn compile      # compiles your code in src/main/java
    mvn test-compile # compile test code in src/test/java
    mvn test         # run tests in src/test/java for files named Test*.java

[github] https://github.com/roostergator/com.opower.connectionpool

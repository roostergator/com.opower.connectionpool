package com.opower.connectionpool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Wraps an instance of the @see java.sql.DatabaseMetaData interface. This implementation 
 * delegates all calls to the wrapped database meta data and provides no additional functionality.
 * It is intended to serve as a starting point for classes that need to customize the behavior of
 * existing data base metadata instances.
 * 
 * @author Joshua Mark Rutherford
 */
public class WrappedDatabaseMetaData implements DatabaseMetaData {

	/**
	 * Initializes a new instance of the WrappedDatabaseMetaData class.
	 * @param databaseMetaData The database meta data wrapped by the wrapped database meta data.
	 */
	public WrappedDatabaseMetaData(DatabaseMetaData databaseMetaData) {
		this.databaseMetaData = databaseMetaData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		return this.getDatabaseMetaData().allProceduresAreCallable();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		return this.getDatabaseMetaData().allTablesAreSelectable();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getURL()
	 */
	public String getURL() throws SQLException {
		return this.getDatabaseMetaData().getURL();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getUserName()
	 */
	public String getUserName() throws SQLException {
		return this.getDatabaseMetaData().getUserName();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return this.getDatabaseMetaData().isReadOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		return this.getDatabaseMetaData().nullsAreSortedHigh();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		return this.getDatabaseMetaData().nullsAreSortedLow();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		return this.getDatabaseMetaData().nullsAreSortedAtStart();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return this.getDatabaseMetaData().nullsAreSortedAtEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
	 */
	public String getDatabaseProductName() throws SQLException {
		return this.getDatabaseMetaData().getDatabaseProductName();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
	 */
	public String getDatabaseProductVersion() throws SQLException {
		return this.getDatabaseMetaData().getDatabaseProductVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverName()
	 */
	public String getDriverName() throws SQLException {
		return this.getDatabaseMetaData().getDriverName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverVersion()
	 */
	public String getDriverVersion() throws SQLException {
		return this.getDatabaseMetaData().getDriverVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
	 */
	public int getDriverMajorVersion() {
		return this.getDatabaseMetaData().getDriverMajorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
	 */
	public int getDriverMinorVersion() {
		return this.getDatabaseMetaData().getDriverMinorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#usesLocalFiles()
	 */
	public boolean usesLocalFiles() throws SQLException {
		return this.getDatabaseMetaData().usesLocalFiles();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		return this.getDatabaseMetaData().usesLocalFilePerTable();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().supportsMixedCaseIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().storesMixedCaseIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().storesLowerCaseIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().storesMixedCaseIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().supportsMixedCaseQuotedIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().storesUpperCaseQuotedIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().storesLowerCaseQuotedIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return this.getDatabaseMetaData().storesMixedCaseQuotedIdentifiers();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
	 */
	public String getIdentifierQuoteString() throws SQLException {
		return this.getDatabaseMetaData().getIdentifierQuoteString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLKeywords()
	 */
	public String getSQLKeywords() throws SQLException {
		return this.getDatabaseMetaData().getSQLKeywords();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getNumericFunctions()
	 */
	public String getNumericFunctions() throws SQLException {
		return this.getDatabaseMetaData().getNumericFunctions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getStringFunctions()
	 */
	public String getStringFunctions() throws SQLException {
		return this.getDatabaseMetaData().getStringFunctions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSystemFunctions()
	 */
	public String getSystemFunctions() throws SQLException {
		return this.getDatabaseMetaData().getSystemFunctions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
	 */
	public String getTimeDateFunctions() throws SQLException {
		return this.getDatabaseMetaData().getTimeDateFunctions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSearchStringEscape()
	 */
	public String getSearchStringEscape() throws SQLException {
		return this.getDatabaseMetaData().getSearchStringEscape();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
	 */
	public String getExtraNameCharacters() throws SQLException {
		return this.getDatabaseMetaData().getExtraNameCharacters();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return this.getDatabaseMetaData().supportsAlterTableWithAddColumn();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return this.getDatabaseMetaData().supportsAlterTableWithDropColumn();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
	 */
	public boolean supportsColumnAliasing() throws SQLException {
		return this.getDatabaseMetaData().supportsColumnAliasing();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return this.getDatabaseMetaData().nullPlusNonNullIsNull();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsConvert()
	 */
	public boolean supportsConvert() throws SQLException {
		return this.getDatabaseMetaData().supportsConvert();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
	 */
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return this.getDatabaseMetaData().supportsConvert(fromType, toType);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		return this.getDatabaseMetaData().supportsTableCorrelationNames();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return this.getDatabaseMetaData().supportsDifferentTableCorrelationNames();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return this.getDatabaseMetaData().supportsExpressionsInOrderBy();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		return this.getDatabaseMetaData().supportsOrderByUnrelated();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupBy()
	 */
	public boolean supportsGroupBy() throws SQLException {
		return this.getDatabaseMetaData().supportsGroupBy();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		return this.getDatabaseMetaData().supportsGroupByUnrelated();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return this.getDatabaseMetaData().supportsGroupByBeyondSelect();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		return this.getDatabaseMetaData().supportsLikeEscapeClause();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		return this.getDatabaseMetaData().supportsMultipleResultSets();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		return this.getDatabaseMetaData().supportsMultipleTransactions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsNonNullableColumns()
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		return this.getDatabaseMetaData().supportsMinimumSQLGrammar();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return this.getDatabaseMetaData().supportsMinimumSQLGrammar();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return this.getDatabaseMetaData().supportsCoreSQLGrammar();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return this.getDatabaseMetaData().supportsExtendedSQLGrammar();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return this.getDatabaseMetaData().supportsANSI92EntryLevelSQL();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return this.getDatabaseMetaData().supportsANSI92IntermediateSQL();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		return this.getDatabaseMetaData().supportsANSI92FullSQL();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return this.getDatabaseMetaData().supportsIntegrityEnhancementFacility();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOuterJoins()
	 */
	public boolean supportsOuterJoins() throws SQLException {
		return this.getDatabaseMetaData().supportsOuterJoins();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		return this.getDatabaseMetaData().supportsFullOuterJoins();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return this.getDatabaseMetaData().supportsLimitedOuterJoins();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemaTerm()
	 */
	public String getSchemaTerm() throws SQLException {
		return this.getDatabaseMetaData().getSchemaTerm();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureTerm()
	 */
	public String getProcedureTerm() throws SQLException {
		return this.getDatabaseMetaData().getProcedureTerm();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogTerm()
	 */
	public String getCatalogTerm() throws SQLException {
		return this.getDatabaseMetaData().getCatalogTerm();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#isCatalogAtStart()
	 */
	public boolean isCatalogAtStart() throws SQLException {
		return this.getDatabaseMetaData().isCatalogAtStart();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogSeparator()
	 */
	public String getCatalogSeparator() throws SQLException {
		return this.getDatabaseMetaData().getCatalogSeparator();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return this.getDatabaseMetaData().supportsSchemasInDataManipulation();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return this.getDatabaseMetaData().supportsSchemasInProcedureCalls();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return this.getDatabaseMetaData().supportsSchemasInTableDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return this.getDatabaseMetaData().supportsSchemasInIndexDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return this.getDatabaseMetaData().supportsSchemasInPrivilegeDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return this.getDatabaseMetaData().supportsCatalogsInDataManipulation();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return this.getDatabaseMetaData().supportsCatalogsInProcedureCalls();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return this.getDatabaseMetaData().supportsCatalogsInTableDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return this.getDatabaseMetaData().supportsCatalogsInIndexDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return this.getDatabaseMetaData().supportsCatalogsInPrivilegeDefinitions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		return this.getDatabaseMetaData().supportsPositionedDelete();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		return this.getDatabaseMetaData().supportsPositionedUpdate();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		return this.getDatabaseMetaData().supportsSelectForUpdate();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		return this.getDatabaseMetaData().supportsStoredProcedures();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return this.getDatabaseMetaData().supportsSubqueriesInComparisons();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		return this.getDatabaseMetaData().supportsSubqueriesInExists();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		return this.getDatabaseMetaData().supportsSubqueriesInIns();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return this.getDatabaseMetaData().supportsSubqueriesInQuantifieds();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return this.getDatabaseMetaData().supportsCorrelatedSubqueries();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsUnion()
	 */
	public boolean supportsUnion() throws SQLException {
		return this.getDatabaseMetaData().supportsUnion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsUnionAll()
	 */
	public boolean supportsUnionAll() throws SQLException {
		return this.getDatabaseMetaData().supportsUnionAll();	
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return this.getDatabaseMetaData().supportsOpenCursorsAcrossCommit();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return this.getDatabaseMetaData().supportsOpenCursorsAcrossRollback();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return this.getDatabaseMetaData().supportsOpenStatementsAcrossCommit();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return this.getDatabaseMetaData().supportsOpenStatementsAcrossRollback();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
	 */
	public int getMaxBinaryLiteralLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxBinaryLiteralLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxCharLiteralLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
	 */
	public int getMaxColumnNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxColumnNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		return this.getDatabaseMetaData().getMaxColumnsInGroupBy();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		return this.getDatabaseMetaData().getMaxColumnsInIndex();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		return this.getDatabaseMetaData().getMaxColumnsInOrderBy();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		return this.getDatabaseMetaData().getMaxColumnsInSelect();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
	 */
	public int getMaxColumnsInTable() throws SQLException {
		return this.getDatabaseMetaData().getMaxColumnsInTable();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxConnections()
	 */
	public int getMaxConnections() throws SQLException {
		return this.getDatabaseMetaData().getMaxConnections();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
	 */
	public int getMaxCursorNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxCursorNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxIndexLength()
	 */
	public int getMaxIndexLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxIndexLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxSchemaNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxProcedureNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxCatalogNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxRowSize()
	 */
	public int getMaxRowSize() throws SQLException {
		return this.getDatabaseMetaData().getMaxRowSize();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return this.getDatabaseMetaData().doesMaxRowSizeIncludeBlobs();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxStatementLength()
	 */
	public int getMaxStatementLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxStatementLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxStatements()
	 */
	public int getMaxStatements() throws SQLException {
		return this.getDatabaseMetaData().getMaxStatements();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
	 */
	public int getMaxTableNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxTableNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
	 */
	public int getMaxTablesInSelect() throws SQLException {
		return this.getDatabaseMetaData().getMaxTablesInSelect();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
	 */
	public int getMaxUserNameLength() throws SQLException {
		return this.getDatabaseMetaData().getMaxUserNameLength();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		return this.getDatabaseMetaData().getDefaultTransactionIsolation();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTransactions()
	 */
	public boolean supportsTransactions() throws SQLException {
		return this.getDatabaseMetaData().supportsTransactions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
	 */
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return this.getDatabaseMetaData().supportsTransactionIsolationLevel(level);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return this.getDatabaseMetaData().supportsDataDefinitionAndDataManipulationTransactions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
	 */
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return this.getDatabaseMetaData().supportsDataManipulationTransactionsOnly();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return this.getDatabaseMetaData().dataDefinitionCausesTransactionCommit();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return this.getDatabaseMetaData().dataDefinitionIgnoredInTransactions();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getProcedures(catalog, schemaPattern, procedureNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
		return this.getDatabaseMetaData().getTables(catalog, schemaPattern, tableNamePattern, types);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSchemas()
	 */
	public ResultSet getSchemas() throws SQLException {
		return this.getDatabaseMetaData().getSchemas();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCatalogs()
	 */
	public ResultSet getCatalogs() throws SQLException {
		return this.getDatabaseMetaData().getCatalogs();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTableTypes()
	 */
	public ResultSet getTableTypes() throws SQLException {
		return this.getDatabaseMetaData().getTableTypes();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getColumnPrivileges(catalog, schema, table, columnNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getTablePrivileges(String catalog,	String schemaPattern, String tableNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getTablePrivileges(catalog, schemaPattern, tableNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
	 */
	public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
		return this.getDatabaseMetaData().getBestRowIdentifier(catalog, schema, table, scope, nullable);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		return this.getDatabaseMetaData().getVersionColumns(catalog, schema, table);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		return this.getDatabaseMetaData().getPrimaryKeys(catalog, schema, table);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getImportedKeys(String catalog, String schema,	String table) throws SQLException {
		return this.getDatabaseMetaData().getImportedKeys(catalog, schema, table);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getExportedKeys(String catalog, String schema,	String table) throws SQLException {
		return this.getDatabaseMetaData().getExportedKeys(catalog, schema, table);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		return this.getDatabaseMetaData().getCrossReference(primaryCatalog, primarySchema, primaryTable, foreignCatalog, foreignSchema, foreignTable);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getTypeInfo()
	 */
	public ResultSet getTypeInfo() throws SQLException {
		return this.getDatabaseMetaData().getTypeInfo();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
		return this.getDatabaseMetaData().getIndexInfo(catalog, schema, table, unique, approximate);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
	 */
	public boolean supportsResultSetType(int type) throws SQLException {
		return this.getDatabaseMetaData().supportsResultSetType(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
	 */
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return this.getDatabaseMetaData().supportsResultSetConcurrency(type, concurrency);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
	 */
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return this.getDatabaseMetaData().ownUpdatesAreVisible(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
	 */
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return this.getDatabaseMetaData().ownDeletesAreVisible(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
	 */
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return this.getDatabaseMetaData().ownInsertsAreVisible(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
	 */
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return this.getDatabaseMetaData().othersUpdatesAreVisible(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
	 */
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return this.getDatabaseMetaData().othersDeletesAreVisible(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
	 */
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return this.getDatabaseMetaData().othersInsertsAreVisible(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
	 */
	public boolean updatesAreDetected(int type) throws SQLException {
		return this.getDatabaseMetaData().updatesAreDetected(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
	 */
	public boolean deletesAreDetected(int type) throws SQLException {
		return this.getDatabaseMetaData().deletesAreDetected(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
	 */
	public boolean insertsAreDetected(int type) throws SQLException {
		return this.getDatabaseMetaData().insertsAreDetected(type);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
	 */
	public boolean supportsBatchUpdates() throws SQLException {
		return this.getDatabaseMetaData().supportsBatchUpdates();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
	 */
	public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
		return this.getDatabaseMetaData().getUDTs(catalog, schemaPattern, typeNamePattern, types);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return this.getDatabaseMetaData().getConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsSavepoints()
	 */
	public boolean supportsSavepoints() throws SQLException {
		return this.getDatabaseMetaData().supportsSavepoints();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters() throws SQLException {
		return this.getDatabaseMetaData().supportsNamedParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults() throws SQLException {
		return this.getDatabaseMetaData().supportsMultipleOpenResults();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
	 */
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return this.getDatabaseMetaData().supportsGetGeneratedKeys();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getSuperTypes(catalog, schemaPattern, typeNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getSuperTables(catalog, schemaPattern, tableNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
		return this.getDatabaseMetaData().getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
	 */
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return this.getDatabaseMetaData().supportsResultSetHoldability(holdability);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return this.getDatabaseMetaData().getResultSetHoldability();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()
	 */
	public int getDatabaseMajorVersion() throws SQLException {
		return this.getDatabaseMetaData().getDatabaseMajorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
	 */
	public int getDatabaseMinorVersion() throws SQLException {
		return this.getDatabaseMetaData().getDatabaseMinorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
	 */
	public int getJDBCMajorVersion() throws SQLException {
		return this.getDatabaseMetaData().getJDBCMajorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
	 */
	public int getJDBCMinorVersion() throws SQLException {
		return this.getDatabaseMetaData().getJDBCMinorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#getSQLStateType()
	 */
	public int getSQLStateType() throws SQLException {
		return this.getDatabaseMetaData().getSQLStateType();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
	 */
	public boolean locatorsUpdateCopy() throws SQLException {
		return this.getDatabaseMetaData().locatorsUpdateCopy();
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.DatabaseMetaData#supportsStatementPooling()
	 */
	public boolean supportsStatementPooling() throws SQLException {
		return this.getDatabaseMetaData().supportsStatementPooling();
	}
	
	/**
	 * Gets the database meta data wrapped by the wrapped database meta data.
	 * @return The database meta data wrapped by the wrapped database meta data.
	 */
	protected DatabaseMetaData getDatabaseMetaData() {
		return this.databaseMetaData;
	}
	
	/**
	 * Gets the database meta data wrapped by the wrapped database meta data.
	 * @param value The database meta data wrapped by the wrapped database meta data.
	 */
	protected void setDatabaseMetaData(DatabaseMetaData value) {
		this.databaseMetaData = value;
	}
	
	private DatabaseMetaData databaseMetaData;
	
}
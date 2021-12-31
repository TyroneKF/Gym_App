package App_Code.Objects.Database_Objects.JDBC;


import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.Collator;
import java.util.*;
import java.lang.Long;

public class MyJDBC
{
    String userName;
    String password;
    String initialConnection = String.format("jdbc:mysql://localhost:3306");
    int connection_attempts = 2;

    String databaseName;  // must be all  lowercase
    String databaseConnection;
    int no_of_tables;
    LinkedHashMap<String, String> tableNamesInDBMap;
    boolean connectionStatus = false;

    boolean databaseExist = true;
    //################################################################
    public MyJDBC(String userName, String password, String databaseName, LinkedHashMap<String, String> tableNamesInDBMap)
    {
        boolean databaseConnected = false;
        boolean tablesConnected = false;

        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName.toLowerCase();
        this.tableNamesInDBMap = tableNamesInDBMap;

        no_of_tables = tableNamesInDBMap!=null ? tableNamesInDBMap.size():0;
        databaseConnection = initialConnection;

        String txtSeperated = "###############################################################################";
        for (int attemptsMade = 1; attemptsMade <= connection_attempts; attemptsMade++)
        {
            System.out.printf("\n%s\n Attempt %s: \n%s", txtSeperated, attemptsMade, txtSeperated);
            if (!(databaseConnected))
            {
                if (establishDBConnection())
                {
                    databaseConnected = true;
                }
            }
            if (databaseConnected && !(tablesConnected))
            {
                if (establishTables())
                {
                    tablesConnected = true;
                }
            }
            if (tablesConnected && databaseConnected)
            {
                System.out.printf("\n\nConnection successful, DB & Tables Exist");
                System.out.printf("\n\n%s", txtSeperated);
                connectionStatus = true;
                break;
            }
            else if (attemptsMade==connection_attempts)
            {
                System.out.printf("Connection Failed!");
                System.out.printf("\n\n%s", txtSeperated);
            }
        }
    }
    //################################################################
    public static void main(String[] args)
    {
        String sql3 = """
                                
                                 CREATE TABLE IF NOT EXISTS data
                                 (
                                    FirstName VARCHAR(32) PRIMARY KEY,
                                    LastName VARCHAR(30) NOT NULL,
                                    Sport    CHAR(32)   CHECK(Sport IN('Snowboarding', 'Rowing', 'Knitting', 'Speed Reading', 'Pool', 'None of the above')),
                                    Years  INT NOT NULL,
                                    Vegetarian BOOLEAN NOT NULL,
                                    DeleteRow CHAR(10) CHECK(DeleteRow IN('Delete Row'))
                                 ) 
                                                                  
                """;

        LinkedHashMap<String, String> tableInitilization = new LinkedHashMap<>();
        tableInitilization.put("data", sql3);

        MyJDBC db = new MyJDBC("root", "password", "gymapp", tableInitilization);

        Object[][] Data = db.getTableDataObject("select * from data;", "data");
        System.out.printf("\n\n\n\n%s\n\n", Arrays.deepToString(Data));
    }
    //################################################################
    public Boolean isDatabaseConnected()
    {
        if (!(connectionStatus))
        {
            JOptionPane.showMessageDialog(null, "Internal Error, connecting to Database", "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        return connectionStatus;
    }

    public boolean establishDBConnection()
    {
        //####################################################################
        // Checking if database exists

        String sql = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'", databaseName);

        if (getMultiColumnQuery(sql)!=null) // database exists
        {
            System.out.printf("\n\nDB  '%s' EXISTS!", databaseName);
        }
        else // database hasnt been created yet
        {
            System.out.printf("\n\nAttempting to create DB  '%s' NOW!", databaseName);
            //################################
            // creating database because it doesnt currently exist

            String sqlCommand = String.format("CREATE DATABASE %s;", databaseName);

            if (!(uploadData(sqlCommand, false)))
            {
                System.out.printf("\n\nError creating Database %s", databaseName);
                databaseExist = false;
                JOptionPane.showMessageDialog(null, "Internal Error, connecting to Database", "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public boolean establishTables()
    {
        if (no_of_tables==0)
        {
            return true;
        }

        //####################################################################
        // checking to see if tables exist

        databaseConnection = String.format("%s/%s", initialConnection, databaseName);

        // creating a  list of table names to be processed in query
        String tablesTXT = "(";

        int index = 1;
        for (String key : tableNamesInDBMap.keySet())
        {
            if (index==no_of_tables)
            {
                tablesTXT += String.format("'%s'); ", key);
                break;
            }
            else
            {
                tablesTXT += String.format("'%s', ", key);
            }
            index++;
        }

        // checking to see if tables exist
        String query = String.format("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE
                  table_schema = '%s' AND
                  table_name IN %s """, databaseName, tablesTXT);

        ArrayList<ArrayList<String>> queryResults = getMultiColumnQuery(query);

        //processing results
        if (queryResults!=null)
        {
            // if all of the tables don't exist
            if (Integer.parseInt(queryResults.get(0).get(0))!=no_of_tables)
            {
                System.out.printf("\n\nAttempting to create DB Tables for '%s' NOW!", databaseName);
                boolean errorCreatingTables = false;
                for (String key : tableNamesInDBMap.keySet()) // create the missing tables
                {
                    System.out.printf("\n\nChecking if Table %s EXISTS!", key);
                    String query2 = String.format("show tables like '%s'", key);
                    if (getMultiColumnQuery(query2)==null) // table doesn't exist
                    {
                        if (!(uploadData(tableNamesInDBMap.get(key), false))) // if a table wasn't successfully created
                        {
                            errorCreatingTables = true;
                            System.out.printf("\n\n  Error creating Table %s", key);
                            continue;
                        }

                        System.out.printf(" &  created Table %s", key);
                    }
                    else
                    {
                        System.out.printf(" & The Table Already EXISTS!", key);
                    }
                }

                if (!errorCreatingTables)
                {
                    System.out.println("\n\nDatabase Tables created successfully...\n\n");
                    return true;
                }
            }
            else // all the tables exist
            {
                System.out.printf("\n\nDB Tables for '%s' EXIST!", databaseName);
                return true;
            }
        }
        System.out.printf("\n\nOverall Un-successful table creation\n\n");
        return false;
    }

    public Boolean establishTableData()
    {
        return false;
    }
    //################################################################

    public Boolean uploadData(String query, boolean multipleQueries)
    {

        if (databaseExist)
        {
            //System.out.println("\n" + query);
            try
            {
                //Query Setup

                Connection connection = multipleQueries ? DriverManager.getConnection(databaseConnection+="?autoReconnect=true&amp;allowMultiQueries=true", userName, password)
                        : DriverManager.getConnection(databaseConnection, userName, password);

                Statement statement = connection.createStatement();
                statement.executeUpdate(query);
                connection.close();
                return true;
            }
            catch (Exception e)
            {
                System.out.printf("\n\n  @uploadData() \nQuery: %s \n%s", query, e);
                JOptionPane.showMessageDialog(null, String.format("Database Error, uploading Query:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(1);
            }
        }
        else
        {
            System.out.printf("\n\n  uploadData() DB couldn't successfully connect to DB %s", databaseName);
        }
        return false;
    }

    /*
      if one query fails the whole queries fail
      The changes made by a previous query in the list isn't visible to the query after it, the updates are made altogether
     */
    public Boolean uploadData_Batch_Altogether(String[] queries)
    {
        if (databaseExist)
        {
            try
            {
                Connection connection =  DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Setting auto-commit false
                connection.setAutoCommit(false);

                //################################
                // Creating Batch
                //################################
                for (String query: queries)
                {
                    statement.addBatch(query);
                }

                //Executing the batch
                statement.executeBatch();

                //Saving the changes
                connection.commit();
                //################################

                return true;
            }
            catch (Exception e)
            {
                System.out.printf("\n\n @uploadData_Batch() \n\nQuery:\n ");
                for (String query: queries)
                {
                    System.out.printf("\n\n%s",query);
                }
                System.out.printf("\n\n%s", e);

                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
        {
            System.out.printf("\n\n @uploadData_Batch() DB couldn't successfully connect to DB %s", databaseName);
        }
        return false;
    }

    /*
      Each query upload is executed separately and the query after, it can notice the changes
     */
    public Boolean uploadData_Batch_Independently(String[] queries)
    {
        if (databaseExist)
        {
            try
            {
                Connection connection =  DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Setting auto-commit false
                connection.setAutoCommit(false);

                //################################
                // Creating Batch
                //################################
                for (String query: queries)
                {
                    //statement.addBatch(query);
                    statement.executeUpdate(query);
                }

                //Executing the batch
               // statement.executeBatch();

                //Saving the changes
                connection.commit();
                //################################

                return true;
            }
            catch (Exception e)
            {
                System.out.printf("\n\n @uploadData_Batch() \n\nQuery:\n ");
                for (String query: queries)
                {
                    System.out.printf("\n\n%s",query);
                }
                System.out.printf("\n\n%s", e);

                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else
        {
            System.out.printf("\n\n @uploadData_Batch() DB couldn't successfully connect to DB %s", databaseName);
        }
        return false;
    }
    //################################################################

    /*
      Give this method an SQL command and this method will return an arraylist storing other arraylists which are rows
      of the SQL query

      @param the sql statement
      @return ArrayList of ArrayLists storing the output of the SQL request
     */
    public ArrayList<ArrayList<String>> getMultiColumnQuery(String query)
    {

        if (databaseExist)
        {
            try
            {
                //Query Setup
                Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Fetching Query
                ResultSet resultSet = statement.executeQuery(query);

                // Get size of query
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnSize = rsmd.getColumnCount();

                // checks if any data was returned, otherwise  the code will eventually return null
                if (resultSet.isBeforeFirst())
                {
                    //creating an ArrayList to store all the rows of the query data
                    ArrayList<ArrayList<String>> queryResultsList = new ArrayList<ArrayList<String>>();

                    // for each row of the query
                    while (resultSet.next())
                    {
                        ArrayList<String> tempList = new ArrayList<>(); // storing  all the columns results of a record

                        // add each column of the query to an overall string which repressents the query row
                        for (int i = 1; i <= columnSize; i++)
                        {
                            tempList.add(resultSet.getString(i));
                        }
                        queryResultsList.add(tempList);
                    }

                    connection.close();
                    return queryResultsList;
                }
                connection.close();

            }
            catch (Exception e)
            {
                System.out.printf("\n\n@getQueryResults()\n ERROR from query: \n\n'' %s '' \n\nException Msg: \n\n'' %s ''", query, e);
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(1);
            }
        }
        else
        {
            System.out.printf("\n\n  getQueryResults() DB couldn't successfully connect to DB %s", databaseName);
        }
        return null;
    }

    public ArrayList<ArrayList<Object>> get_Multi_ColumnQuery_Object(String query)
    {

        if (databaseExist)
        {
            try
            {
                //Query Setup
                Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Fetching Query
                ResultSet resultSet = statement.executeQuery(query);

                // Get size of query
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnSize = rsmd.getColumnCount();

                // checks if any data was returned, otherwise  the code will eventually return null
                if (resultSet.isBeforeFirst())
                {
                    //creating an ArrayList to store all the rows of the query data
                    ArrayList<ArrayList<Object>> queryResultsList = new ArrayList<ArrayList<Object>>();

                    // for each row of the query
                    while (resultSet.next())
                    {
                        ArrayList<Object> tempList = new ArrayList<>(); // storing  all the columns results of a record

                        // add each column of the query to an overall string which repressents the query row
                        for (int i = 1; i <= columnSize; i++)
                        {
                            tempList.add(resultSet.getString(i));
                        }
                        queryResultsList.add(tempList);
                    }

                    connection.close();
                    return queryResultsList;
                }
                connection.close();

            }
            catch (Exception e)
            {
                System.out.printf("\n\n  @getQueryResults() ERROR from query %s \n\n  %s", query, e);
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(1);
            }
        }
        else
        {
            System.out.printf("\n\n  getQueryResults() DB couldn't successfully connect to DB %s", databaseName);
        }
        return null;
    }

    public ArrayList<String> getSingleColumnQuery_ArrayList(String query)
    {
        if (databaseExist)
        {
            try
            {
                //Query Setup
                Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Fetching Query
                String query2 = String.format("%s", query);
                ResultSet resultSet = statement.executeQuery(query2);

                // checks if any data was returned, otherwise  the code will eventually return null
                if (resultSet.isBeforeFirst())
                {
                    //############################################
                    // Get number of  Columns in each query row
                    //############################################
                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int columnSize = rsmd.getColumnCount();

                    // if query has multiple columnns this method cannot produce a 2d list results,
                    if (columnSize > 1)
                    {
                        System.out.printf("\n\n!!! Query size bigger than one column, use multi-line query !!! \n\n");
                        throw new Exception();
                    }

                    //############################################
                    // Get the number of rows in the query
                    //############################################

                    /*
                    remove last char ";" for getRowsInQuery() method
                    sub-query cannot have a ";" in in the middle sub-query
                     */

                    Integer rowCount = getRowsInQuery(query); // get row count of query to this method "query"

                    if (rowCount!=null)
                    {
                        //############################################
                        // Storing query data in String[]
                        //############################################
                        ArrayList<String> queryData = new ArrayList<>(); // storing  all the columns results of a record
                        // System.out.printf("\nRow Count is %s", rowCount);

                        // for each row of the query
                        int i = 0;
                        while (resultSet.next())
                        {
                            String result = resultSet.getString(1); // resultset is the row
                            queryData.add(result);
                            i++;
                        }
                        return queryData;
                    }
                }
                connection.close();
            }
            catch (Exception e)
            {
                System.out.printf("\n\n  @getSingleColumnQuery() ERROR from query '%s' \n\n  %s", query, e);
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(1);
            }
        }
        else
        {
            System.out.printf("\n\n  @getSingleColumnQuery() DB couldn't successfully connect to DB %s", databaseName);
        }
        return null;
    }

    public Collection<String> getSingleColumnQuery_AlphabeticallyOrderedTreeSet(String query)
    {
        if (databaseExist)
        {
            try
            {
                //Query Setup
                Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Fetching Query
                String query2 = String.format("%s", query);
                ResultSet resultSet = statement.executeQuery(query2);

                // checks if any data was returned, otherwise  the code will eventually return null
                if (resultSet.isBeforeFirst())
                {
                    //############################################
                    // Get number of  Columns in each query row
                    //############################################
                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int columnSize = rsmd.getColumnCount();

                    // if query has multiple columnns this method cannot produce a 2d list results,
                    if (columnSize > 1)
                    {
                        System.out.printf("\n\n!!! Query size bigger than one column, use multi-line query !!! \n\n");
                        throw new Exception();
                    }

                    //############################################
                    // Get the number of rows in the query
                    //############################################

                    /*
                    remove last char ";" for getRowsInQuery() method
                    sub-query cannot have a ";" in in the middle sub-query
                     */

                    Integer rowCount = getRowsInQuery(query); // get row count of query to this method "query"

                    if (rowCount!=null)
                    {
                        //############################################
                        // Storing query data in String[]
                        //############################################
                        Collection<String> queryData = new TreeSet<String>(Collator.getInstance());
                        // System.out.printf("\nRow Count is %s", rowCount);

                        // for each row of the query
                        int i = 0;
                        while (resultSet.next())
                        {
                            String result = resultSet.getString(1); // resultset is the row
                            queryData.add(result);
                            i++;
                        }
                        return queryData;
                    }
                }
                connection.close();
            }
            catch (Exception e)
            {
                System.out.printf("\n\n  @getSingleColumnQuery() ERROR from query '%s' \n\n  %s", query, e);
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(1);
            }
        }
        else
        {
            System.out.printf("\n\n  @getSingleColumnQuery() DB couldn't successfully connect to DB %s", databaseName);
        }
        return null;
    }
    //################################################################

    public String[] getSingleColumnQuery(String query)
    {
        if (databaseExist)
        {
            try
            {
                //Query Setup
                Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //Fetching Query
                String query2 = String.format("%s", query);
                ResultSet resultSet = statement.executeQuery(query2);

                // checks if any data was returned, otherwise  the code will eventually return null
                if (resultSet.isBeforeFirst())
                {
                    //############################################
                    // Get number of  Columns in each query row
                    //############################################
                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int columnSize = rsmd.getColumnCount();

                    // if query has multiple columnns this method cannot produce a 2d list results,
                    if (columnSize > 1)
                    {
                        System.out.printf("\n\n!!! Query size bigger than one column, use multi-line query !!! \n\n");
                        throw new Exception();
                    }

                    //############################################
                    // Get the number of rows in the query
                    //############################################

                    /*
                    remove last char ";" for getRowsInQuery() method
                    sub-query cannot have a ";" in in the middle sub-query
                     */

                    Integer rowCount = getRowsInQuery(query); // get row count of query to this method "query"

                    if (rowCount!=null)
                    {
                        //############################################
                        // Storing query data in String[]
                        //############################################
                        String[] queryData = new String[rowCount]; // storing  all the columns results of a record
                        // System.out.printf("\nRow Count is %s", rowCount);

                        // for each row of the query
                        int i = 0;
                        while (resultSet.next())
                        {
                            String result = resultSet.getString(1); // resultset is the row
                            queryData[i] = result;
                            i++;
                        }
                        return queryData;
                    }
                }
                connection.close();
            }
            catch (Exception e)
            {
                System.out.printf("\n\n  @getSingleColumnQuery() ERROR from query '%s' \n\n  %s", query, e);
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(1);
            }
        }
        else
        {
            System.out.printf("\n\n  @getSingleColumnQuery() DB couldn't successfully connect to DB %s", databaseName);
        }
        return null;
    }

    //################################################################

    public String[] getColumnDataTypes(String tableName)
    {
        String columnDataTypesQuery = String.format("""                    
                select data_type
                from information_schema.columns
                where table_schema = '%s'
                and table_name = '%s'
                order by ordinal_position;                      
                                       """, databaseName, tableName);

        return getSingleColumnQuery(columnDataTypesQuery);
    }

    public String[] getColumnNames(String tableName)
    {
        String columnNamesQuery = String.format("""                    
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = '%s'
                AND table_name = '%s'
                ORDER BY ordinal_position;                      
                                       """, databaseName, tableName);
        return getSingleColumnQuery(columnNamesQuery);
    }

    public Integer getRowsInQuery(String query)
    {
        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
            Statement statement = connection.createStatement();

            query = query.replaceFirst(".$", ""); //=> aaabc  ;

            //Fetching Query
            String query3 = String.format("select count(*) from (%s) as count;", query);
            ResultSet resultSet = statement.executeQuery(query3);

            if (resultSet.isBeforeFirst())
            {
                resultSet.next(); // move cursor to first query result
                return resultSet.getInt(1);
            }
        }
        catch (Exception e)
        {
            System.out.printf("\n\n@getRowsInQuery() ERROR \n%s\n%s", query, e);
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            //System.exit(1);
        }

        return null;
    }

    //################################################################

    public Object[][] getTableDataObject(String query, String tableName)
    {
        if (databaseExist)
        {
           // System.out.printf("\n\n"+query);
            try
            {
                //Query Setup
                Connection connection = DriverManager.getConnection(databaseConnection, userName, password);
                Statement statement = connection.createStatement();

                //###################################
                //Fetching Query
                //###################################

                ResultSet resultSet = statement.executeQuery(query);

                // check if resultset is not empty
                if (resultSet.isBeforeFirst())
                {
                    //###################################
                    // Getting Query Data Info
                    //##################################

                    String[] columnDataTypes = getColumnDataTypes(tableName);
                   // System.out.println("\n\n"+Arrays.toString(columnDataTypes));

                    int noOfColumns = columnDataTypes.length;

                    Integer rowsInQuery = getRowsInQuery(query);

                    //####################################################################
                    // Creating Data Object for JTable
                    //####################################################################
                    if (rowsInQuery!=null)
                    {
                        Object[][] Data = new Object[rowsInQuery][noOfColumns];

                        for (int row = 0; row < rowsInQuery; row++)
                        {
                            resultSet.next(); // go to next row in query results

                            // For each col in the row
                            for (int col = 0; col < noOfColumns; col++)
                            {
                                String colData = resultSet.getString(col + 1);
                                String colDataType = columnDataTypes[col];

                                try
                                {
                                    // Convert to appropriate datatype
                                    switch (colDataType)
                                    {
                                        case "varchar":
                                            Data[row][col] = colData;
                                            break;
                                        case "tinyint":
                                            Data[row][col] = colData.equals("1") ? true:false;
                                            break;
                                        case "int":
                                            Data[row][col] = Integer.valueOf(colData);
                                            break;
                                        case "decimal":
                                            Data[row][col] = new BigDecimal(colData);
                                            break;
                                        case "bigint":
                                            Data[row][col] = colData;
                                            break;
                                        default:
                                            System.out.printf("\n\n '%s'", colDataType);
                                            throw new Exception();


                                    }
                                }
                                catch (Exception e)
                                {
                                    System.out.printf("\n@getTableDataObject() \nUn-Accounted table data type!");
                                    JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                                    //System.exit(1);
                                    return null;
                                }
                            }
                        }
                        return Data;
                    }
                }
            }
            catch (Exception e)
            {
                System.out.printf("\n\n@getTableDataObject() ERROR from query: \n'%s' \n\n %s", query, e);
            }
        }
        return null;
    }
}

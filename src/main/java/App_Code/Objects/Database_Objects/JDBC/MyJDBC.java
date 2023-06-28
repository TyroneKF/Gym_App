package App_Code.Objects.Database_Objects.JDBC;


import org.apache.ibatis.jdbc.ScriptRunner;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.text.Collator;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//######################################
import java.sql.Connection;
import java.sql.DriverManager;


public class MyJDBC
{
    private String
            databaseName, // must be in lowercase
            userName,
            password,
            initial_db_connection = "jdbc:mysql://localhost:3306",
            db_Connection_Address = initial_db_connection;

    private final String
            line_Separator = "############################################################################################################################",
            middle_line_Separator = "###########################################################################################";


    private boolean
            db_Connection_Status = false, override = true; // ERROR surrounding this

    private int
            connection_Attempts = 1;

    //##################################################################################################################
    //
    //##################################################################################################################
    public MyJDBC(String userName, String password, String databaseName, String db_Script_Address)
    {
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName.toLowerCase();
        boolean success = false;

        // Repeat attempts to connect to DB for an X amount of time
        for (int attemptsMade = 1; attemptsMade <= connection_Attempts; attemptsMade++)
        {
            System.out.printf("\n%s\n Attempt %s: \n%s", line_Separator, attemptsMade, line_Separator);

            //##############################################
            //  Check if DB has already been connected
            //##############################################
            System.out.printf("\n\nChecking if DB '%s' EXISTS! ", databaseName);

            if (attemptsMade == 1)
            {
                if (check_IF_DB_Exists(databaseName))
                {
                    System.out.printf("\n\nDB '%s' already exists!! \n%s", databaseName, line_Separator);
                    success = true;
                    break;
                }
            }

            //##############################################
            // Setup Database data
            //##############################################
            System.out.printf("\n\n%s \nCreating DB tables! \n%s", middle_line_Separator, middle_line_Separator);
            if (run_SQL_Script(db_Script_Address))
            {
                success = true;
                System.out.printf("\n\n%s \nSuccessfully, created DB & Initialized Data! \n%s", line_Separator, line_Separator);
            }
        }

        if (success)
        {
            override = false;
            db_Connection_Status = true;
            db_Connection_Address = String.format("%s/%s", db_Connection_Address, databaseName);
        }
    }

    //##################################################################################################################
    // Setup Methods
    //##################################################################################################################

    public boolean run_SQL_Script(String sql_Script_Address)
    {
        try
        {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());  //Registering the Driver

            System.out.println("\n\n");

            Connection con = DriverManager.getConnection(initial_db_connection, userName, password);

            ScriptRunner sr = new ScriptRunner(con);  //Initialize the script runner
            Reader reader = new BufferedReader(new FileReader(sql_Script_Address)); //Creating a reader object

            sr.setStopOnError(true);
            sr.runScript(reader);   //Running the script

            con.close();
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\n%s \nrun_SQL_Script() ERROR \n, ", line_Separator, e);
        }

        return false;
    }

    //##################################################################################################################
    //  Get Methods
    //##################################################################################################################
    public Boolean get_DB_Connection_Status()
    {
        return db_Connection_Status;
    }

    public boolean check_IF_DB_Exists(String databaseName)
    {
        //####################################################################
        // Checking if database exists
        //####################################################################

        String sql = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'", databaseName);

        if (getMultiColumnQuery(sql) != null) // database exists
        {
            return true;
        }
        return false;
    }

    //##################################################################################################################
    // Uploading Data to DB Methods
    //##################################################################################################################
    public Boolean uploadData(String query, boolean multipleQueries)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  @uploadData() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }
        try
        {
            //Query Setup
            Connection connection = multipleQueries ? DriverManager.getConnection(db_Connection_Address += "?autoReconnect=true&amp;allowMultiQueries=true", userName, password)
                    : DriverManager.getConnection(db_Connection_Address, userName, password);

            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            connection.close();
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\n  @uploadData() \nQuery: %s \n%s", query, e);
            JOptionPane.showMessageDialog(null, "Database Error, uploading Query:\n\nCheck Output ", "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }

        return false;
    }

    /*
      if one query fails the whole queries fail
      The changes made by a previous query in the list isn't visible to the query after it, the updates are made altogether
     */
    public Boolean uploadData_Batch_Altogether(String[] queries)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  uploadData_Batch_Altogether() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }

        try
        {
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
            Statement statement = connection.createStatement();

            //Setting auto-commit false
            connection.setAutoCommit(false);

            //################################
            // Creating Batch
            //################################
            for (String query : queries)
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
            for (String query : queries)
            {
                System.out.printf("\n\n%s", query);
            }
            System.out.printf("\n\n%s", e);

            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }

        return false;
    }

    /*
      Each query upload is executed separately and the query after, it can notice the changes
     */
    public Boolean uploadData_Batch_Independently(String[] queries)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  uploadData_Batch_Independently() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }
        try
        {
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
            Statement statement = connection.createStatement();

            //Setting auto-commit false
            connection.setAutoCommit(false);

            //################################
            // Creating Batch
            //################################
            for (String query : queries)
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
            for (String query : queries)
            {
                System.out.printf("\n\n%s", query);
            }
            System.out.printf("\n\n%s", e);

            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
    }

    //##################################################################################################################
    // DB Get Methods
    //##################################################################################################################

    /*
      Give this method an SQL command and this method will return an arraylist storing other arraylists which are rows
      of the SQL query

      @param the sql statement
      @return ArrayList of ArrayLists storing the output of the SQL request
     */
    public ArrayList<ArrayList<String>> getMultiColumnQuery(String query)
    {
        if (!(get_DB_Connection_Status()) && !(override))
        {
            System.out.printf("\n\n  getMultiColumnQuery() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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
        return null;
    }

    public ArrayList<ArrayList<Object>> get_Multi_ColumnQuery_Object(String query)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  get_Multi_ColumnQuery_Object() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }

        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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

        return null;
    }

    public String[] getSingleColumnQuery(String query)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getSingleColumnQuery() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }

        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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

                if (rowCount != null)
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

        return null;
    }

    public ArrayList<String> getSingleColumnQuery_ArrayList(String query)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getSingleColumnQuery_ArrayList() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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

                if (rowCount != null)
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
        }
        return null;
    }

    public Collection<String> getSingleColumnQuery_AlphabeticallyOrderedTreeSet(String query)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getSingleColumnQuery_AlphabeticallyOrderedTreeSet() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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

                if (rowCount != null)
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
        }
        return null;
    }

    //################################################################
    public Object[][] getTableDataObject(String query, String tableName)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getTableDataObject() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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
                if (rowsInQuery != null)
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
                                        Data[row][col] = colData.equals("1") ? true : false;
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

                                    case "datetime":

                                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        LocalDateTime result = LocalDateTime.parse(colData, format);

                                        Data[row][col] = result.toString().replaceAll("T", " ");

                                        break;

                                    case "time":

                                        // Remove :00 the seconds from the time
                                        String time = String.format("%s", colData)
                                                .replaceFirst(".$", "")
                                                .replaceFirst(".$", "")
                                                .replaceFirst(".$", "");

                                        Data[row][col] = time;
                                        break;

                                    default:
                                        System.out.printf("\n\n@getTableDataObject() Error With DataType '%s' = ' %s ' !", colDataType, colData);
                                        throw new Exception();
                                }
                            }
                            catch (Exception e)
                            {
                                System.out.printf("\n@getTableDataObject() \nUn-Accounted table data type! \n\n%s", e);
                                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
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
        return null;
    }

    //##################################################################################################################
    //  Get data about a query methods
    //##################################################################################################################

    public String[] getColumnDataTypes(String tableName)
    {
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getColumnDataTypes() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }

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
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getColumnNames() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }

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
        if (!(get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getRowsInQuery() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }

        try
        {
            //Query Setup
            Connection connection = DriverManager.getConnection(db_Connection_Address, userName, password);
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
}

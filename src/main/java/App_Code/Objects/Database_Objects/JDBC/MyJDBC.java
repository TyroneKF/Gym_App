package App_Code.Objects.Database_Objects.JDBC;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.text.Collator;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//######################################
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutionException;

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
            db_Connection_Status = false,
            override = true; // ERROR surrounding this

    private int
            connection_Attempts = 1;

    //##################################################################################################################
    //
    //##################################################################################################################
    public MyJDBC(String userName, String password, String databaseName, String db_Script_Folder_Address)
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

            if (attemptsMade==1)
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
            if (run_SQL_Script_Folder(db_Script_Folder_Address, true))
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
    // Changes to TXT files / SQL Backup Files  Methods
    //##################################################################################################################
    public boolean run_SQL_Script_Folder(String folder_Address, boolean updateDBVersionInFile)
    {
        File[] files = new File(folder_Address).listFiles();

        // Traversing through the  objects in the files array
        for (File file : files)
        {
            if (!file.isFile()) { continue; } // skip folders & subdirectory's and anything that isnt a file for now

            String filePath = file.getPath(); // get the path of the file
            if (updateDBVersionInFile) // if instructed to update the DB version in the database script update it
            {
                try
                {
                    //##############################################
                    // Update current Gym Version no in SQL File
                    //##############################################
                    Path path = Paths.get(filePath); // format the path of the file into proper format being a Path object
                    Charset charset = StandardCharsets.UTF_8;

                    String content = new String(Files.readAllBytes(path), charset); // Get the contents of the file
                    content = content.replaceAll("(gymapp)......?", databaseName + ";"); // replace gymapp????? with gymapp(Current Version no), find only one = reduced search

                    Files.write(path, content.getBytes(charset)); // write the current gym version inside the file
                }
                catch (Exception e)
                {
                    System.out.printf("\n\n%s run_SQL_Script_Folder() Error, replacing version number  in file '%s'!  \n\n%s \n%s", line_Separator, file.getName(), e, line_Separator);
                    return false;
                }
            }
            if (!(run_SQL_Script(filePath))) // Run the database script for specific table
            {
                System.out.printf("\n\n%s run_SQL_Script_Folder() Error, executing sql script '%s'! \n%s", line_Separator, file.getName(), line_Separator);
                return false;
            }

            System.out.printf("\n\n%s \nSuccessfully executed file '%s'! \n%s", line_Separator, file.getName(), line_Separator);

            /*else if (file.isDirectory()) // If a subdirectory is found, print the name of the sub directory
            {
                System.out.println("Directory: " + file.getName());

                run_SQL_Script_Folder(file.getPath()); // recursively call the function on the files in this new folder
            }*/
        }
        return true;
    }

    private boolean run_SQL_Script(String sql_Script_Address)
    {
        try
        {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());  //Registering the Driver
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

    //############################################################################ ######################################
    // Write Methods With BackUp Files
    //##################################################################################################################
    public boolean writeTxtToSQLFile(String sqlFilePath, String txt_To_Write_To_SQL_File)
    {
        //########################################
        // Creating Temp File
        //########################################
        String[] filenameAndExt = sqlFilePath.split("\\.(?=[^\\.]+$)"); // file name [0] & ext [1]
        String tempFilePath = String.format("%sTmp.%s", filenameAndExt[0], filenameAndExt[1]);
        String filename = new File(sqlFilePath).getName();

        //########################################
        // Reading File & Writing
        //########################################

        try (BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath)); // resources automatically released in try block / no need for reader.close()
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFilePath), "UTF-8")))
        {
            int count = 0;
            Iterator<String> it = reader.lines().iterator();
            while (it.hasNext()) // iterate through txt file
            {
                count++;

                String line = count==1 ? it.next():System.getProperty("line.separator") + it.next();

                if (!it.hasNext()) // last line
                {
                    line = line.replace(";", ","); // replace ";" with ","
                }

                writer.write(line);
            }

            writer.write(System.getProperty("line.separator") + txt_To_Write_To_SQL_File + ";"); // Add new line to file

        }
        catch (Exception e)
        {
            System.out.printf("\n\nwriteTxtToSQLFile() Error Writing / Reading to file \n%s", e);
            return false;
        }

        //########################################
        // Renaming File
        //########################################
        try
        {
            Files.move(Paths.get(tempFilePath), Paths.get(sqlFilePath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nwriteTxtToSQLFile() Error Replacing Temp File \n%s", e);
            return false;
        }
    }

    public boolean replaceTxtInSQLFile(String sqlFilePath, boolean multiValues, String txt_To_Find, String txt_Replacement)
    {

        //########################################
        // Creating Temp File
        //########################################
        String[] filenameAndExt = sqlFilePath.split("\\.(?=[^\\.]+$)"); // file name [0] & ext [1]
        String tempFilePath = String.format("%sTmp.%s", filenameAndExt[0], filenameAndExt[1]);

        System.out.println("\n\n Here1"); //hello delete

        //########################################
        // Reading File & Writing
        //########################################

        try (BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath)); // resources automatically released in try block / no need for reader.close()
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFilePath), "UTF-8")))
        {
            int count = 0;
            boolean replacementFound = false;
            Iterator<String> it = reader.lines().iterator();

            while (it.hasNext()) // iterate through txt file
            {
                count++;

                // Processing next lines
                String currentLine = it.next().trim(); // trim  current line

                // if replacement found & there isn't multi values just write to the file
                if (replacementFound && !multiValues)
                {
                    currentLine = count==1 ? currentLine:System.getProperty("line.separator") + currentLine;
                    writer.write(currentLine);
                    continue;
                }
                if (currentLine.contains(txt_To_Find)) // check if current line is the line to be deleted
                {
                    System.out.printf("\n\nReplacement made: %s", currentLine);

                    currentLine = currentLine.contains(";") ? txt_Replacement + ";":txt_Replacement + ",";

                    replacementFound = true;
                }

                currentLine = count==1 ? currentLine:System.getProperty("line.separator") + currentLine;
                writer.write(currentLine);// Writing Line to File
            }

            //########################################
            // If replacement not found, exit
            //########################################
            if (!replacementFound)
            {
                return false;
            }
        }
        catch (Exception e)
        {
            System.out.printf("\n\nreplaceTxtInSQLFile() Error Writing / Reading to file \n%s", e);
            return false;
        }

        //########################################
        // Renaming File
        //########################################
        try
        {
            Files.move(Paths.get(tempFilePath), Paths.get(sqlFilePath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nreplaceTxtInSQLFile() Error Replacing Temp File \n%s", e);
            return false;
        }
    }

    /**
     * In most files there's atleast one insert value which avoids completely deleting all elements and the file no ending in an ';'
     *
     * @param filePath
     * @param txtToDelete
     * @return
     */
    public boolean deleteTxtInFile(String filePath, String txtToDelete)
    {
        //########################################
        // Creating Temp File
        //########################################
        String[] filenameAndExt = filePath.split("\\.(?=[^\\.]+$)"); // file name [0] & ext [1]
        String tempFilePath = String.format("%sTmp.%s", filenameAndExt[0], filenameAndExt[1]);

        //########################################
        // Reading File & Writing
        //########################################

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)); // resources automatically released in try block / no need for reader.close()
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFilePath), "UTF-8")))
        {
            //#####################################
            // Read For Deleted Txt
            //#####################################
            boolean found = false;

            ArrayList<String> lines = new ArrayList<>();
            Iterator<String> it = reader.lines().iterator();

            while (it.hasNext()) // iterate through txt file &
            {
                String line = it.next().trim();

                if (line.contains(txtToDelete))
                {
                    found = true;
                    continue;
                }
                lines.add(line);
            }

            //#####################################
            // Check if deletion not made, dlt file
            //#####################################
            if (!found) // Check if txt to be found is
            {
                if (new File(tempFilePath).delete()) // Delete temp file, Original file stays the same,
                {
                    System.out.printf("\nError, couldn't deleting temp file either!");
                }
                return false;
            }

            //#####################################
            // Check if deletion not made, dlt file
            //#####################################
            Iterator<String> it2 = lines.iterator();
            while (it2.hasNext())
            {
                String txtToWrite = it2.next();

                if (!it2.hasNext()) // if there isn't another line
                {
                    txtToWrite = txtToWrite.substring(0, txtToWrite.length() - 1); // removes ',' at the end if its there potentially could be ';' too
                    txtToWrite += ";"; // replaces with ';' because this is the new last line now

                    writer.write(txtToWrite);
                    break;
                }
                writer.write(txtToWrite + System.getProperty("line.separator"));
            }
        }
        catch (Exception e)
        {
            System.out.printf("\n\ndeleteTxtInFile() Error Writing / Reading to file \n%s", e);
            return false;
        }

        //########################################
        // Renaming File
        //########################################
        try
        {
            Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\ndeleteTxtInFile() Error Replacing Temp File \n%s", e);
            return false;
        }
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

        if (getMultiColumnQuery(sql)!=null) // database exists
        {
            return true;
        }
        return false;
    }

    //##################################################################################################################
    // Uploading Data to DB Methods
    //##################################################################################################################

    /**
     * This method can upload one statement or, multiple queries within a single String after each statement in the string is separated by a ;
     */
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
                    :DriverManager.getConnection(db_Connection_Address, userName, password);

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
      If one query fails the whole queries fails
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
    public Boolean uploadData_Batch_Independently(String[] queries) // HELLO Can't this method and the one below refactored
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

    public Boolean uploadData_Batch_Independently(ArrayList<String> queries)
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

    //##################################################################################################################
    //
    //##################################################################################################################
    public static void main(String[] args)
    {
        String filePath = "src/main/java/Resources/Database/Scripts/Editable_DB_Scripts/5.) Stores.sql";

        //        String txt = "(1, 'Grams','None Of The Above',1, 0,0,0,0,0,0,0,0,0,0,0,0)";
        String
                txt = "(\'Tesco\')",
                txt2 = "(\'NENWNW\')";

        MyJDBC db = new MyJDBC("root", "password", "gymapp00001", "src/main/java/Resources/Database_Scripts/DB_Scripts");

        // Add text to file
/*
        if (db.writeTxtToSQLFile(filePath, txt))
        {
            System.out.println("\n\nSuccessful");
        }
        else
        {
            System.out.println("\n\nFail");
        }*/

        // Text to replace
       /* //
        if (db.replaceTxtInSQLFile(filePath, true, txt, txt2))
        {
            System.out.println("\n\n Sucessful");
        }
        else
        {
            System.out.println("\n\n Fail");
        }*/

        // Txt to Delete

        if (db.deleteTxtInFile(filePath, txt2))
        {
            System.out.println("\n\nSuccessful");
        }
        else
        {
            System.out.println("\n\nUn-Successful");
        }
    }
}

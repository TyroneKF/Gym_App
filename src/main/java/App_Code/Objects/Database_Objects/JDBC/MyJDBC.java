package App_Code.Objects.Database_Objects.JDBC;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.text.Collator;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.stream.Collectors;

public class MyJDBC
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private String
            databaseName, // must be in lowercase
            userName,
            password,
            initial_db_connection = "jdbc:mysql://localhost:3306",
            db_Connection_Address = initial_db_connection;
    
    private Connection connection;
    
    private final String
            users_DB_Script_Path = "/data/database_scripts/2.) Users.sql",
            line_Separator = "############################################################################################################################",
            middle_line_Separator = "###########################################################################################";
    
    private boolean
            db_Connection_Status = false;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MyJDBC(Boolean productionMode, String host, String port, String userName, String password, String databaseName,
                  String db_Script_Folder_Address, String script_List_Name, String databaseNamesFileName)
    {
        //####################################################################
        //  Setting Variables
        //####################################################################
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName.toLowerCase();
        initial_db_connection = String.format("jdbc:mysql://%s:%s", host, port);
        db_Connection_Address = String.format("%s/%s", initial_db_connection, databaseName);
        
        try
        {
            //#################################################################
            //  Checking DB & User Credentials Are Valid
            //#################################################################
            System.out.printf("\n\n%s \nTesting User Credentials: '%s@%s' & DB: %s \n%s ", line_Separator, userName, host, databaseName, line_Separator);
            
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());  //Registering the Driver
            connection = DriverManager.getConnection(db_Connection_Address, userName, password); // if connection fails, error is thrown, script stops here
            
            System.out.println("\nUser & Database Credentials are valid !");
            db_Connection_Status = true;
            
            //#################################################################
            //  Checking Table Count of Current DB Against Tables in File Count
            //#################################################################
            System.out.printf("\n\nValidating Database Setup (Tables) inside of DB '%s', checking if all the tables are initialized.", databaseName);
            
            String
                    path = String.format("%s/%s", db_Script_Folder_Address, databaseNamesFileName), // don't include a / between the files
                    sql_Statement = String.format("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '%s' AND table_name IN (", databaseName);
            
            int tablesCount = 0; // increments and identifies how many tables there are in the DB
            
            InputStream listStream = getClass().getResourceAsStream(path);
            if (listStream == null)
            {
                System.out.printf("\n\nMyJDBC() error, reading file with path: \n%s", path);
                return;
            }
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(listStream, StandardCharsets.UTF_8))) // resources automatically released in try block / no need for reader.close()
            {
                Iterator<String> it = br.lines().iterator();
                while (it.hasNext())
                {
                    sql_Statement += String.format("'%s',", it.next());
                    tablesCount++;
                }
                
                // last item remove last comma and replace with ); to end SQL query off
                sql_Statement = String.format("%s);", StringUtils.substring(sql_Statement, 0, sql_Statement.length() - 1));
            }
            catch (Exception e)
            {
                System.err.printf("\n\nMyJDBC() error, reading file: '%s'! \n\n%s", databaseNamesFileName, e);
                return;
            }
            
            //#################################################################
            //  Db Check How Many Tables There Are VS Expected Count
            //#################################################################
            ArrayList<String> queryResults = get_Single_Column_Query_AL(sql_Statement);
            if (queryResults == null) // error return invalid results
            {
                System.err.printf("\n\nMyJDBC() Error, executing SQL Statement == NULL: \n\n%s", sql_Statement);
                return;
            }
            
            // Get DB Value for how many tables are in DB
            int db_Tables_Count = Integer.parseInt(queryResults.get(0));
            
            if (tablesCount == db_Tables_Count)
            {
                System.out.printf("\n\n%s \nDatabase '%s' Setup is OK !! \n%s ", line_Separator, databaseName, line_Separator);
                
                db_Connection_Status = true;
                return;
            }
            
            System.out.printf("\n\nDatabase '%s' expected %s tables DB has %s", databaseName, tablesCount, db_Tables_Count);
            
            System.out.printf("\n\n%s \nDatabase '%s' Tables need to be setup !! \n%s ", line_Separator, databaseName, line_Separator);
        }
        catch (SQLException e) // Create Database
        {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            int errorCode = e.getErrorCode();// 2003 = Can't connect to MySQL server, 0	= General connection failure
            
            if (errorCode == 1045) // Access denied (bad username or password)
            {
                System.err.printf("\n\nAccess denied: Incorrect Username ('%s') or Password. \nTry re-running the prerequisites script to setup Credentials!\n\n%s", userName, line_Separator);
                return;
            }
            else if (errorCode == 2003) { System.out.println("\n\nMYSQL ERROR: Issues Connecting to MYSQL"); }
            else if (errorCode == 1049) // Unknown database
            {
                String msg = String.format("""
                        \n\nDatabase Access Denied: '%s' (Unknown database).
                        \nAttempting to recreate Database shortly, if program is not in production Mode.
                        \nHowever, this may denied as maybe user '%s'@'%s' doesn't have the correct mysql privileges to do so!
                        Re-execute the pre-requisites script to ensure proper user privileges!""", databaseName, userName, host);
                
                if (productionMode)
                {
                    System.out.println(msg); return;
                } // if  not in testing mode, pre-requisites script needs to be run to give user permissions / creating db app side won't fix the broader issue
                
                // In non production mode, try and create the DB assuming the users account already has privileges in MYSQL
                try
                {
                    System.err.println("MyJDBC MYSQL ERROR: Connecting to DB, attempting to recreate DB !");
                    connection = DriverManager.getConnection(initial_db_connection, userName, password);
                    
                    String sqlScript = String.format("CREATE DATABASE IF NOT EXISTS %s;", databaseName);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(sqlScript);
                }
                catch (SQLException x)
                {
                    System.err.printf("/n/nMyJDBC MYSQL ERROR: Creating Database '%s' \n%s \n\n%s", databaseName, x, msg);
                    return;
                }
            }
            else
            {
                System.err.printf("\n\nMyJDBC UNKNOWN SQL Error Exception: \n\n%s \nSQL State: %s \n\n%s \n\n", message, sqlState, line_Separator);
                return;
            }
        }

         /*
            (Heads UP) This segment of code is only executed if the tables in the DB don't match the expected number
            meaning the DB hasn't been set up prior  or, if the DB initialization failed first time around.
         */
        
        // ############################################
        // Edit Insert Users Script with Users Details
        // ############################################
        String usersQueryScript = "";
        try (InputStream scriptFile = getClass().getResourceAsStream(users_DB_Script_Path))
        {
            if (scriptFile == null)
            {
                System.err.printf("\n\nMyJDBC: Error opening file in path: \n'%s'", users_DB_Script_Path);
                return;
            }
            
            usersQueryScript = new String(scriptFile.readAllBytes(), StandardCharsets.UTF_8);
            usersQueryScript = usersQueryScript.replace("@USERNAME@", userName); // swap username scriptFile the script with the replacement txt
        }
        catch (Exception e)
        {
            System.err.printf("\n\nMyJDBC MYSQL ERROR: locating / reading script : '%s' ! \n\nError MSG: \n%s", usersQueryScript, e);
            return;
        }
        
        // #################################################
        // Execute Users.SQL Users Script
        // #################################################
        try
        {
            System.out.printf("\n\nUsers Script Query: \n%s", usersQueryScript);
            
            // ######################
            // Upload Query
            // ######################
            String fullUrl = db_Connection_Address + "?autoReconnect=true&allowMultiQueries=true";
            connection = DriverManager.getConnection(fullUrl, userName, password);
            
            Statement statement = connection.createStatement();
            statement.executeUpdate(usersQueryScript);
            
            connection = DriverManager.getConnection(db_Connection_Address, userName, password); // reset back to default patterns
            
            // ######################
            // Print success msg
            // ######################
            System.out.printf("\n\nUser '%s' successfully created / exists !", userName);
        }
        catch (SQLException x)
        {
            String message = x.getMessage();
            int errorCode = x.getErrorCode();// 2003 = Can't connect to MySQL server, 0	= General connection failure
            
            // 1045 : Access denied (bad username or password) | 2003 : Issues connecting to MySQL | 1049 : Unknown DB
            System.err.printf("\n\nMyJDBC Running users script | Error Code: %s \n\n%s", errorCode, message);
            return;
        }
        
        // #################################################
        // Re-setup Database
        // #################################################
        System.out.printf("\n\n\n%s\nAttempting to create Database Structure! \n%s", line_Separator, line_Separator);
        
        if (! (run_SQL_Script_Folder(db_Connection_Address, db_Script_Folder_Address, script_List_Name)))
        {
            System.err.printf("\n\n%s \nFailed creating DB & Initializing Data! \n%s", line_Separator, line_Separator);
            return;
        }
        
        System.out.printf("\n\n%s \nSuccessfully, Authenticated using User & DB Credentials! \n%s", line_Separator, line_Separator);
        
        // #################################################
        // Set DB Variables
        // #################################################
        db_Connection_Status = true;
    }
    
    public MyJDBC(String userName, String password, String databaseName, String db_Script_Folder_Address, String script_List_Name)
    {
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName.toLowerCase();
        
        //##############################################
        //  Check if DB has already been connected
        //##############################################
        System.out.printf("\n\nChecking if DB '%s' EXISTS! ", databaseName);
        
        //##############################################
        // Setup Database data
        //##############################################
        System.out.printf("\n\n%s \nCreating DB tables! \n%s", middle_line_Separator, middle_line_Separator);
        if (! (run_SQL_Script_Folder(initial_db_connection, db_Script_Folder_Address, script_List_Name)))
        {
            System.err.printf("\n\n%s \nFailed creating DB & Initializing Data! \n%s", line_Separator, line_Separator);
            return;
        }
        
        System.out.printf("\n\n%s \nSuccessfully, created DB & Initialized Data! \n%s", line_Separator, line_Separator);
        
        db_Connection_Status = true;
        db_Connection_Address = String.format("%s/%s", db_Connection_Address, databaseName);
    }
    
    //##################################################################################################################
    // Changes to TXT files / SQL Backup Files  Methods
    //##################################################################################################################
    public boolean run_SQL_Script_Folder(String db_Connection_Path, String db_Script_Folder_Address, String script_List_Name)
    {
        // ####################################################
        //  Variables
        // ####################################################
        String path = String.format("%s/%s", db_Script_Folder_Address, script_List_Name);
        String methodName = "run_SQL_Script_Folder()";
        
        // ####################################################
        //  Get Folder Path
        // ####################################################
        InputStream listStream = getClass().getResourceAsStream(path);
        
        if (listStream == null)
        {
            System.err.printf("\n\nrun_SQL_Script_Folder() Error Loading = NULL \n%s", path);
            return false;
        }
        
        // ####################################################
        //  Reading Script List & Executing Each Script in List
        // ####################################################
        // Resources automatically released in try block / no need for reader.close()
        try (
                BufferedReader file_Names_Reader = new BufferedReader(new InputStreamReader(listStream, StandardCharsets.UTF_8));
                Connection connection = DriverManager.getConnection(db_Connection_Path, userName, password)
        )
        {
            //for(String file: file_Names_Reader.readAllLines())
            Iterator<String> it = file_Names_Reader.lines().iterator() ;
            while(it.hasNext())
            {
                //##########################################################
                // Get Path of File
                //##########################################################
                String fileName = it.next();
                System.out.printf("\nMyJDBC.java %s Executing script: %s \n\n", methodName, fileName);
                
                try (InputStream scriptStream = getClass().getResourceAsStream(String.format("%s/%s", db_Script_Folder_Address, fileName)))
                {
                    if (scriptStream == null)
                    {
                        System.err.printf("\nrun_SQL_Script_Folder() Script not found: '%s'", fileName);
                        return false;
                    }
                    
                    //##########################################################
                    // Execute File Script
                    //##########################################################
                    try (
                            Reader file_Reader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8);
                    )
                    {
                        ScriptRunner runner = new ScriptRunner(connection);
                        runner.setStopOnError(true);
                        runner.runScript(file_Reader);
                        
                        System.out.printf("\nMyJDBC.java %s successfully executed script: %s", methodName, fileName);
                    }
                }
                
                //##########################################################
                // Error Handling
                //##########################################################
                catch (Exception e)
                {
                    handleException_File(e, methodName);
                    return false;
                }
            }
            
            return true;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_File(e, methodName);
            return false;
        }
    }
    
    private boolean run_SQL_Script(String sql_Script_Address)
    {
        return false;
    }
    
    //############################################################################ ######################################
    // Write Methods With BackUp Files
    //##################################################################################################################
    public boolean write_Txt_To_SQL_File(String sqlFilePath, String txt_To_Write_To_SQL_File)
    {
        String methodName = "write_Txt_To_SQL_File()";
        
        //##########################################################
        // Creating Temp File
        //##########################################################
        String[] filenameAndExt = sqlFilePath.split("\\.(?=[^\\.]+$)"); // file name [0] & ext [1]
        String tempFilePath = String.format("%sTmp.%s", filenameAndExt[0], filenameAndExt[1]);
        
        //##########################################################
        // Reading File & Writing
        //##########################################################
        try (BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath)); // resources automatically released in try block / no need for reader.close()
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFilePath), "UTF-8")))
        {
            boolean first = true;
            
            Iterator<String> it = reader.lines().iterator();
            while (it.hasNext()) // iterate through txt file
            {
                if (first) { first = false; writer.write(it.next().trim()); continue; }
                
                String line = System.lineSeparator() + it.next();
                
                // IF Last Line : Format  last line to regular line as there's a new last line being appended afterward
                if (! it.hasNext()) { line = line.replace(";", ","); }
                
                writer.write(line);
            }
            
            //#############################
            // Write Last Line (New ADD)
            //#############################
            writer.write(System.lineSeparator() + txt_To_Write_To_SQL_File + ";");
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_File(e, methodName);
            return false;
        }
        
        //##########################################################
        // Renaming File
        //##########################################################
        return rename_File(tempFilePath, sqlFilePath, methodName);
    }
    
    public boolean replace_Txt_In_SQL_File(String sqlFilePath, boolean multiValues, String txt_To_Find, String
            txt_Replacement)
    {
        String methodName = "replace_Txt_In_SQL_File()";
        
        //##########################################################
        // Creating Temp File
        //##########################################################
        String[] filenameAndExt = sqlFilePath.split("\\.(?=[^\\.]+$)"); // file name [0] & ext [1]
        String tempFilePath = String.format("%sTmp.%s", filenameAndExt[0], filenameAndExt[1]);
        
        //##########################################################
        // Reading File & Writing
        //##########################################################
        try (BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath)); // resources automatically released in try block / no need for reader.close()
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFilePath), "UTF-8")))
        {
            boolean first = true, replacementFound = false;
            
            Iterator<String> it = reader.lines().iterator();
            while (it.hasNext()) // iterate through txt file
            {
                // ###########################
                // First Line Avoid Editing
                // ###########################
                if (first) { first = false; writer.write(it.next().trim()); continue; }
                
                // ###########################
                // General Line
                // ###########################
                String currentLine = System.lineSeparator() + it.next().trim();
                
                /**
                 *  Triggered in conditions:
                 *    1.) If replacement has not been found & Current Line contains search txt execute
                 *    2.) If replacement has been found and multi-values & line contains search txt execute
                 */
                if ((! replacementFound || multiValues) && currentLine.contains(txt_To_Find))
                {
                    System.out.printf("\n\nReplacement made: %s", currentLine);
                    
                    currentLine = currentLine.contains(";") ? txt_Replacement + ";" : txt_Replacement + ",";
                    
                    replacementFound = true;
                }
                
                writer.write(currentLine);// Writing Line to File
            }
            
            //################################
            // Not Found & Error Delete Temp
            //################################
            if (! replacementFound && ! new File(tempFilePath).delete())
            {
                System.err.printf("\nMyJDBC.java %s \nError, SearchTxt Not Found ! Error, deleting Temp File \n' %s ' !",
                        methodName, tempFilePath);
                
                return false;
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_File(e, methodName);
            return false;
        }
        
        //##########################################################
        // Renaming File
        //##########################################################
        return rename_File(tempFilePath, sqlFilePath, methodName);
    }
    
    /**
     * In most files there's at least one insert value which avoids completely deleting all elements and the file no ending in an ';'
     *
     * @param filePath
     * @param txtToDelete
     * @return
     */
    public boolean delete_Txt_In_File(String filePath, String txtToDelete)
    {
        String methodName = "delete_Txt_In_File()";
        
        //#################################################################################
        // Creating Temp File
        //#################################################################################
        String[] filenameAndExt = filePath.split("\\.(?=[^\\.]+$)"); // file name [0] & ext [1]
        String tempFilePath = String.format("%sTmp.%s", filenameAndExt[0], filenameAndExt[1]);
        
        //#################################################################################
        // Reading File & Deleting
        //#################################################################################
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)); // resources automatically released in try block / no need for reader.close()
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFilePath), StandardCharsets.UTF_8)))
        {
            //###########################################
            // Find TXT & Delete it
            //###########################################
            boolean first = true, found = false;
            
            List<String> lines = reader.lines().collect(Collectors.toList()); // Read File into memory
            
            Iterator<String> it1 = lines.iterator();
            while (it1.hasNext())
            {
                if (first) { first = false; continue; }
                
                String line = it1.next().trim();
                
                if (line.contains(txtToDelete))
                {
                    found = true; it1.remove();
                }
            }
            
            //############################################
            // Not Found & Error Delete Temp
            //############################################
            if (! found && ! new File(tempFilePath).delete())
            {
                System.err.printf("\nMyJDBC.java %s \nError, SearchTxt Not Found ! Error, deleting Temp File \n' %s ' !",
                        methodName, tempFilePath);
                
                return false;
            }
            
            //###########################################
            // Add ";" at the end of the file
            //###########################################
            first = true;
            
            Iterator<String> it = lines.iterator();
            while (it.hasNext()) // iterate through txt file
            {
                // ###########################
                // First Line Avoid Editing
                // ###########################
                if (first) { first = false; writer.write(it.next().trim()); continue; }
                
                // ###########################
                // General TXT
                // ###########################
                String currentLine = System.lineSeparator() + it.next().trim();
                
                //###########################
                // Special Case Last Line
                //###########################
                if (! it.hasNext())
                {
                    currentLine = currentLine.substring(0, currentLine.length() - 1); // removes ',' at the end if its there potentially could be ';' too
                    currentLine += ";"; // replaces with ';' because this is the new last line now
                }
                
                //###########################
                //  Write
                //###########################
                writer.write(currentLine);// Writing Line to File
            }
        }
        //#################################################################################
        // Error Handling
        //#################################################################################
        catch (Exception e)
        {
            handleException_File(e, methodName);
            return false;
        }
        
        //#################################################################################
        // Renaming File
        //#################################################################################
        return rename_File(tempFilePath, filePath, methodName);
    }
    
    private boolean rename_File(String tempFilePath, String filePath, String methodName)
    {
        //##########################################################
        // Renaming File
        //##########################################################
        try
        {
            // Replaces first file with the second
            Files.move(Paths.get(tempFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        //#################################
        // Error Handling
        //##################################
        catch (Exception e)
        {
            handleException_File(e, methodName);
            return false;
        }
    }
    
    //##################################################################################################################
    // Uploading Data to DB Methods
    //##################################################################################################################
    
    /**
     * This method can upload one statement or, multiple queries within a single String after each statement in the string is separated by a ;
     */
    public boolean upload_Data(String query, boolean multipleQueries)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "upload_Data()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        String fullAddress = multipleQueries
                ? db_Connection_Address + "?autoReconnect=true&allowMultiQueries=true"
                : db_Connection_Address;
        
        try (
                Connection connection = multipleQueries ? DriverManager.getConnection(fullAddress, userName, password)
                        : DriverManager.getConnection(db_Connection_Address, userName, password);
                
                Statement statement = connection.createStatement();
        )
        {
            statement.executeUpdate(query);
            
            return true;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query);
            return false;
        }
    }
    
    /**
     * If one query fails the whole queries fails
     * The changes made by a previous query in the list isn't visible to the query after it, the updates are made altogether
     */
    public boolean upload_Data_Batch_Altogether(String[] queries)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "upload_Data_Batch_Altogether()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Statement statement = connection.createStatement();)
        {
            connection.setAutoCommit(false); //Setting auto-commit false
            
            //################################
            // Creating Batch
            //################################
            for (String query : queries)
            {
                statement.addBatch(query);
            }
            
            //################################
            //Executing the Batch
            //################################
            statement.executeBatch();
            connection.commit(); // Commit Changes beyond current driver
            
            return true; // Return Output
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, queries);
            return false;
        }
    }
    
    /*
      Each query upload is executed separately and the query after, it can notice the changes
     */
    public boolean upload_Data_Batch_Independently(String[] queries)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "upload_Data_Batch_Independently()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Statement statement = connection.createStatement();)
        {
            connection.setAutoCommit(false); //Setting auto-commit false
            
            for (String query : queries)  // Creating Batch
            {
                statement.executeUpdate(query);
            }
            
            connection.commit();
            return true;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, queries);
            return false;
        }
    }
    
    //##################################################################################################################
    // DB Get Methods
    //##################################################################################################################
    
    /**
     * Give this method an SQL command and this method will return an arraylist storing other arraylists which are rows
     * of the SQL query
     *
     * @return ArrayList of ArrayLists storing the output of the SQL request
     */
    public ArrayList<ArrayList<String>> get_Multi_Column_Query(String query)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "get_Multi_Column_Query()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Execute Query
        //##########################################################
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            ArrayList<ArrayList<String>> queryResultsList = new ArrayList<ArrayList<String>>();  //creating an ArrayList to store all the rows of the query data
            
            ResultSetMetaData rs_MetaData = resultSet.getMetaData();
            int columnSize = rs_MetaData.getColumnCount(); // Get size of query
            
            while (resultSet.next())  // For each row of the query, compile results
            {
                ArrayList<String> tempList = new ArrayList<>(); // storing  all the columns results of a record
                
                // Add each column of the query to an overall string which represents the query row
                for (int i = 1; i <= columnSize; i++)
                {
                    tempList.add(resultSet.getString(i));
                }
                queryResultsList.add(tempList);
            }
            return queryResultsList;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query);
            return null;
        }
    }
    
    public ArrayList<ArrayList<Object>> get_Multi_Column_Query_Object(String query)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "get_Multi_Column_Query_Object()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            ArrayList<ArrayList<Object>> queryResultsList = new ArrayList<ArrayList<Object>>();  //creating an ArrayList to store all the rows of the query data
            
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnSize = rsmd.getColumnCount();  // Get size of query
            
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
            return queryResultsList;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query);
            return null;
        }
    }
    
    public ArrayList<String> get_Single_Column_Query_AL(String query)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "get_Single_Column_Query_AL()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            //############################################
            // Get number of  Columns in each query row
            //############################################
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnSize = rsmd.getColumnCount();
            
            if (columnSize > 1) // if query has multiple columns this method cannot produce a 2d list results,
            {
                System.err.printf("\n\n!!! MyJDBC.java %s \nQuery size bigger than one column, use multi-line query !!!", methodName);
                
                JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output ", "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                
                return null;
            }
            
            //############################################
            // Storing query data in ArrayList
            //############################################
            ArrayList<String> queryData = new ArrayList<>(); // storing  all the records 1 column results
            
            while (resultSet.next()) // for each row of the query
            {
                String result = resultSet.getString(1); // resultset is the row
                queryData.add(result);
            }
            return queryData;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query);
            return null;
        }
    }
    
    public TreeSet<String> get_Single_Col_Alphabetically_Sorted(String query)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "get_Single_Col_Alphabetically_Sorted()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            //############################################
            // Get number of  Columns in each query row
            //############################################
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnSize = rsmd.getColumnCount();
            
            if (columnSize > 1) // if query has multiple columns this method cannot produce a 2d list results,
            {
                System.err.printf("\n\n!!! MyJDBC.java %s \nQuery size bigger than one column, use multi-line query !!!", methodName);
                
                JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output ", "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                
                return null;
            }
            
            //############################################
            // Storing query data in String[]
            //############################################
            TreeSet<String> queryData = new TreeSet<String>(Collator.getInstance());
            
            while (resultSet.next()) // for each row of the query
            {
                queryData.add(resultSet.getString(1));
            }
            return queryData;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query);
            return null;
        }
    }
    
    public ArrayList<ArrayList<Object>> get_TableData_Objects_AL(String query, String tableName)
    {
        //#########################################################################
        // Check DB Status
        //#########################################################################
        String methodName = "get_TableData_Objects_AL()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //#########################################################################
        // Query Setup
        //#########################################################################
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            //#####################################################################
            // Getting Query Data Info
            //#####################################################################
            ArrayList<ArrayList<Object>> data = new ArrayList<>();
            ArrayList<String> table_Col_DataTypes = get_Column_DataTypes_AL(tableName);
            
            int noOfColumnsInTable = table_Col_DataTypes.size();
            
            //#####################################################################
            // Compile Results
            //#####################################################################
            while (resultSet.next())
            {
                ArrayList<Object> rowData = new ArrayList<>();
                
                for (int col = 1; col <= noOfColumnsInTable; col++) // Filter Through Query Result Data
                {
                    String colData = resultSet.getString(col);
                    String colDataType = table_Col_DataTypes.get(col - 1); // Index in regular list start at 0
                    
                    //######################################
                    // Convert Data To Appropriate Datatype
                    //######################################
                    switch (colDataType)
                    {
                        case "varchar":
                            rowData.add(colData); break;
                        case "tinyint":
                            rowData.add(colData.equals("1")); break;
                        case "int":
                            rowData.add(Integer.valueOf(colData)); break;
                        case "decimal":
                            rowData.add(new BigDecimal(colData)); break;
                        case "bigint":
                            rowData.add(resultSet.getLong(col)); break;
                        case "datetime":
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime result = LocalDateTime.parse(colData, format);
                            rowData.add(result.toString().replaceAll("T", " "));
                            break;
                        case "time":
                            // parses "HH:mm:ss" & Removes :00 the seconds from the time
                            rowData.add(LocalTime.parse(colData).truncatedTo(ChronoUnit.MINUTES)); break;
                        default:
                            throw new Exception(String.format("\n\n MyJDBC.java @getTableDataObject() Error With DataType '%s' = ' %s ' !", colDataType, colData));
                    }
                }
                data.add(rowData);
            }
            return data;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query);
            return null;
        }
    }
    
    //##################################################################################################################
    //  MetaData Methods (Done)
    //##################################################################################################################
    public ArrayList<String> get_Column_DataTypes_AL(String tableName)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        if (! is_DB_Connected("get_Column_DataTypes_AL()")) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        String columnDataTypesQuery = String.format("""
                SELECT data_type
                FROM information_schema.columns
                WHERE table_schema = '%s'
                AND table_name = '%s'
                ORDER BY ordinal_position;""", databaseName, tableName);
        
        //##########################################################
        // Return Query
        //##########################################################
        return get_Single_Column_Query_AL(columnDataTypesQuery);
    }
    
    public ArrayList<String> get_Column_Names_AL(String tableName)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        if (! is_DB_Connected("get_Column_Names_AL()")) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        String columnNamesQuery = String.format("""
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = '%s'
                AND table_name = '%s'
                ORDER BY ordinal_position;""", databaseName, tableName);
        
        //##########################################################
        // Return Query
        //##########################################################
        return get_Single_Column_Query_AL(columnNamesQuery);
    }
    
    //##################################################################################################################
    // Validation Methods
    //##################################################################################################################
    public boolean get_DB_Connection_Status()
    {
        return db_Connection_Status;
    }
    
    private boolean is_DB_Connected(String methodName)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.err.printf("\n\nMyJDBC.java : %s \nDB couldn't successfully connect to DB '%s'!", methodName, databaseName);
            return false;
        }
        
        return true;
    }
    
    //##################################################################################################################
    // Quick Methods
    //##################################################################################################################
    
    
    
    
    //###########################################
    // SQL Methods Error Handling
    //###########################################
    private void handleException_MYSQL(Exception e, String methodName, Object query)
    {
        if (e instanceof SQLException x)
        {
            print_SQL_ERR_MSG(x, methodName, query);
        }
        else
        {
            print_Exception_ERR_MSG(e, methodName, query);
        }
    }
    
    private void print_SQL_ERR_MSG(SQLException e, String methodName, Object query)
    {
        
        System.err.printf("\n\nMyJDBC.java @%s SQL ERROR \nQuery: %s \nMessage: %s \nSQLState: %s \nErrorCode: %d\n",
                methodName, query.toString(), e.getMessage(), e.getSQLState(), e.getErrorCode());
        
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_Exception_ERR_MSG(Exception e, String methodName, Object query)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\n%s \n%s", methodName, query != null ? query.toString() : "", e);
        
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    //###########################################
    // File Methods for Error Handling
    //###########################################
    private void handleException_File(Exception e, String methodName)
    {
        if (e instanceof FileNotFoundException x)
        {
            print_File_Not_Found_ERR_MSG(x, methodName);
        }
        else if (e instanceof IOException x)
        {
            print_IO_Exception_ERR_MSG(x, methodName);
        }
        else
        {
            print_Exception_ERR_MSG(e, methodName);
        }
    }
    
    private void print_Exception_ERR_MSG(Exception e, String methodName)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\n%s", methodName, e);
        
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_File_Not_Found_ERR_MSG(Exception e, String methodName)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\nFile not found: %s%n", methodName, e.getMessage());
        
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_IO_Exception_ERR_MSG(Exception e, String methodName)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\nI/O error while processing files: %s%n", methodName, e.getMessage());
        
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
}

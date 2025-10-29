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
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//######################################
import java.sql.Connection;
import java.sql.DriverManager;

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
            databaseNamesFile = "",
            db_Connection_Address = initial_db_connection;
    
    private Connection connection;
    
    private final String
            users_DB_Script_Path = "/data/database_scripts/2.) Users.sql",
            line_Separator = "############################################################################################################################",
            middle_line_Separator = "###########################################################################################";
    
    private boolean
            db_Connection_Status = false,
            override = true; // ERROR surrounding this
    
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
        this.initial_db_connection = String.format("jdbc:mysql://%s:%s", host, port);
        this.databaseNamesFile = databaseNamesFileName;
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
                
                override = false;
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
        override = false;
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
        
        override = false;
        db_Connection_Status = true;
        db_Connection_Address = String.format("%s/%s", db_Connection_Address, databaseName);
        
    }
    
    //##################################################################################################################
    // Changes to TXT files / SQL Backup Files  Methods
    //##################################################################################################################
    public boolean run_SQL_Script_Folder(String db_Connection_Path, String db_Script_Folder_Address, String script_List_Name)
    {
        // ####################################################
        //  Getting Script_List Object
        // ####################################################
        String path = String.format("%s/%s", db_Script_Folder_Address, script_List_Name);
        
        InputStream listStream = getClass().getResourceAsStream(path);
        
        if (listStream == null)
        {
            System.err.printf("\n\nrun_SQL_Script_Folder() Error Loading = NULL \n%s", path);
            return false;
        }
        
        // ####################################################
        //  Reading Script List & Executing Each Script in List
        // ####################################################
        try (BufferedReader br = new BufferedReader(new InputStreamReader(listStream, StandardCharsets.UTF_8))) // resources automatically released in try block / no need for reader.close()
        {
            connection = DriverManager.getConnection(db_Connection_Path, userName, password);
            
            Iterator<String> it = br.lines().iterator();
            while (it.hasNext())
            {
                String fileName = it.next();
                System.out.printf("\nrun_SQL_Script_Folder() Executing script: %s \n\n", fileName);
                
                try // Execute Script
                {
                    InputStream scriptStream = getClass().getResourceAsStream(String.format("%s/%s", db_Script_Folder_Address, fileName));
                    
                    if (scriptStream == null)
                    {
                        System.err.printf("\nrun_SQL_Script_Folder() Script not found: '%s'", fileName);
                        return false;
                    }
                    
                    //InputStream scriptStream = new FileInputStream(String.format("%s/%s", db_Script_Folder_Address, fileName));
                    Reader reader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8);
                    
                    // Creating Script Runner to stop on errors
                    ScriptRunner runner = new ScriptRunner(connection);
                    runner.setStopOnError(true);
                    runner.runScript(reader);
                    
                    System.out.printf("\nrun_SQL_Script_Folder() successfully executed script: %s", fileName);
                }
                catch (Exception e)
                {
                    System.err.printf("\n\nrun_SQL_Script_Folder(): error executing file: %s \n\n%s\n\n" +
                            "", fileName, e.getMessage());
                    
                    throw new Exception(String.format("\nrun_SQL_Script_Folder() ERROR:  %s", fileName));
                }
            }
            
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\nrun_SQL_Script_Folder() Error Writing / Reading to file \n%s", e);
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
                
                String line = count == 1 ? it.next() : System.getProperty("line.separator") + it.next();
                
                if (! it.hasNext()) // last line
                {
                    line = line.replace(";", ","); // replace ";" with ","
                }
                
                writer.write(line);
            }
            
            writer.write(System.getProperty("line.separator") + txt_To_Write_To_SQL_File + ";"); // Add new line to file
            
        }
        catch (Exception e)
        {
            System.err.printf("\n\nwriteTxtToSQLFile() Error Writing / Reading to file \n%s", e);
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
            System.err.printf("\n\nwriteTxtToSQLFile() Error Replacing Temp File \n%s", e);
            return false;
        }
    }
    
    public boolean replace_Txt_In_SQL_File(String sqlFilePath, boolean multiValues, String txt_To_Find, String
            txt_Replacement)
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
                if (replacementFound && ! multiValues)
                {
                    currentLine = count == 1 ? currentLine : System.getProperty("line.separator") + currentLine;
                    writer.write(currentLine);
                    continue;
                }
                if (currentLine.contains(txt_To_Find)) // check if current line is the line to be deleted
                {
                    System.out.printf("\n\nReplacement made: %s", currentLine);
                    
                    currentLine = currentLine.contains(";") ? txt_Replacement + ";" : txt_Replacement + ",";
                    
                    replacementFound = true;
                }
                
                currentLine = count == 1 ? currentLine : System.getProperty("line.separator") + currentLine;
                writer.write(currentLine);// Writing Line to File
            }
            
            //########################################
            // If replacement not found, exit
            //########################################
            if (! replacementFound)
            {
                return false;
            }
        }
        catch (Exception e)
        {
            System.err.printf("\n\nreplaceTxtInSQLFile() Error Writing / Reading to file \n%s", e);
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
            System.err.printf("\n\nreplaceTxtInSQLFile() Error Replacing Temp File \n%s", e);
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
    public boolean delete_Txt_In_File(String filePath, String txtToDelete)
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
            if (! found) // Check if txt to be found is
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
                
                if (! it2.hasNext()) // if there isn't another line
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
            System.err.printf("\n\ndeleteTxtInFile() Error Writing / Reading to file \n%s", e);
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
            System.err.printf("\n\ndeleteTxtInFile() Error Replacing Temp File \n%s", e);
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
        
        if (get_Multi_Column_Query(sql) != null) // database exists
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
    public Boolean upload_Data(String query, boolean multipleQueries)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  @uploadData() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }
        try
        {
            //Query Setup
            connection = multipleQueries ? DriverManager.getConnection(db_Connection_Address += "?autoReconnect=true&amp;allowMultiQueries=true", userName, password)
                    : DriverManager.getConnection(db_Connection_Address, userName, password);
            
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            
            connection = DriverManager.getConnection(db_Connection_Address, userName, password); // reset back to default patterns
            return true;
        }
        catch (Exception e)
        {
            System.err.printf("\n\n  @uploadData() \nQuery: %s \n%s", query, e);
            JOptionPane.showMessageDialog(null, "Database Error, uploading Query:\n\nCheck Output ", "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return false;
    }
    
    /**
      If one query fails the whole queries fails
      The changes made by a previous query in the list isn't visible to the query after it, the updates are made altogether
     */
    public Boolean upload_Data_Batch_Altogether(String[] queries)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  uploadData_Batch_Altogether() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }
        
        try
        {
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
            System.err.printf("\n\n @uploadData_Batch() \n\nQuery:\n ");
            for (String query : queries)
            {
                System.err.printf("\n\n%s", query);
            }
            System.err.printf("\n\n%s", e);
            
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return false;
    }
    
    /*
      Each query upload is executed separately and the query after, it can notice the changes
     */
    public Boolean upload_Data_Batch_Independently(String[] queries) // HELLO Can't this method and the one below refactored
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  uploadData_Batch_Independently() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }
        try
        {
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
            System.err.printf("\n\n @uploadData_Batch() \n\nQuery:\n ");
            for (String query : queries)
            {
                System.err.printf("\n\n%s", query);
            }
            System.err.printf("\n\n%s", e);
            
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
    }
    
    public Boolean upload_Data_Batch_Independently(ArrayList<String> queries)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  uploadData_Batch_Independently() DB couldn't successfully connect to DB '%s'!", databaseName);
            return false;
        }
        try
        {
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
            System.err.printf("\n\n @uploadData_Batch() \n\nQuery:\n ");
            for (String query : queries)
            {
                System.err.printf("\n\n%s", query);
            }
            System.err.printf("\n\n%s", e);
            
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
    }
    
    //##################################################################################################################
    // DB Get Methods
    //##################################################################################################################
    
    /**
      Give this method an SQL command and this method will return an arraylist storing other arraylists which are rows
      of the SQL query

      @param the sql statement
      @return ArrayList of ArrayLists storing the output of the SQL request
     */
    public ArrayList<ArrayList<String>> get_Multi_Column_Query(String query)
    {
        if (! (get_DB_Connection_Status()) && ! (override))
        {
            System.out.printf("\n\n  getMultiColumnQuery() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
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
                
                return queryResultsList;
            }
        }
        catch (Exception e)
        {
            System.err.printf("\n\n@getQueryResults()\n ERROR from query: \n\n'' %s '' \n\nException Msg: \n\n'' %s ''", query, e);
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            //System.exit(1);
            
        }
        return null;
    }
    
    public ArrayList<ArrayList<Object>> get_Multi_Column_Query_Object(String query)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  get_Multi_ColumnQuery_Object() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        
        try
        {
            //Query Setup
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
                return queryResultsList;
            }
        }
        catch (Exception e)
        {
            System.err.printf("\n\n  @getQueryResults() ERROR from query %s \n\n  %s", query, e);
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            //System.exit(1);
        }
        
        return null;
    }
    
    public String[] get_Single_Column_Query(String query)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\ngetSingleColumnQuery() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        
        try
        {
            //Query Setup
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
                
                Integer rowCount = get_Rows_In_Query(query); // get row count of query to this method "query"
                
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
        }
        catch (Exception e)
        {
            System.err.printf("\n\n@getSingleColumnQuery() ERROR from query \n\n'%s' \n\n%s\n\n", query, e);
            // e.printStackTrace();
            
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            //System.exit(1);
        }
        
        return null;
    }
    
    public ArrayList<String> get_Single_Column_Query_AL(String query)
    {
        try
        {
            //############################################
            // Execute Query & Get Results
            //############################################
            Statement statement = connection.createStatement(); // Query Setup
            
            ResultSet resultSet = statement.executeQuery(query);  // Fetching Query Results
            
            //############################################
            // Get number of  Columns in each query row
            //############################################
            if (! resultSet.isBeforeFirst()) // checks if any data wasn't returned, exit
            {
                System.err.printf("\n\ngetSingleColumnQuery_ArrayList() Returned NULL using query: \n\n%s", query);
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            
            //############################################
            // Get number of  Columns in each query row
            //############################################
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnSize = rsmd.getColumnCount();
            
            if (columnSize > 1) // if query has multiple columns this method cannot produce a 2d list results,
            {
                System.err.printf("\n\n!!! getSingleColumnQuery_ArrayList() Query size bigger than one column, use multi-line query !!!");
                JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
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
        catch (Exception e)
        {
            System.err.printf("\n\n@getSingleColumnQuery_ArrayList() ERROR from query: \n\n'%s' \n\n%s\n\n", query, e);
            
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            
            return null;
        }
    }
    
    public Collection<String> get_SingleColumnQuery_AlphabeticallyOrderedTreeSet(String query)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getSingleColumnQuery_AlphabeticallyOrderedTreeSet() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
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
                
                Integer rowCount = get_Rows_In_Query(query); // get row count of query to this method "query"
                
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
        }
        catch (Exception e)
        {
            System.err.printf("\n\n@getSingleColumnQuery_AlphabeticallyOrderedTreeSet() ERROR from query \n\n'%s' \n\n%s\n", query, e);
            // e.printStackTrace();
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        }
        return null;
    }
    
    //################################################################
    public Object[][] get_TableData_Objects(String query, String tableName)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getTableDataObject() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        try
        {
            //Query Setup
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
                
                String[] columnDataTypes = get_Column_DataTypes(tableName);
                // System.out.println("\n\n"+Arrays.toString(columnDataTypes));
                
                int noOfColumns = columnDataTypes.length;
                
                Integer rowsInQuery = get_Rows_In_Query(query);
                
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
                                System.err.printf("\n@getTableDataObject() \nUn-Accounted table data type! \n\n%s", e);
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
            System.err.printf("\n\n@getTableDataObject() ERROR from query: \n'%s' \n\n %s", query, e);
        }
        return null;
    }
    
    public ArrayList<ArrayList<Object>> get_TableData_Objects_AL(String query, String tableName)
    {
        ResultSet resultSet = null;
        Integer rowsInQuery = 0;
        int noOfColumnsInTable = 0;
        ArrayList<String> tableColumnDataTypes = null;
        
        try
        {
            //####################################################################
            //Fetching Query
            //####################################################################
            Statement statement = connection.createStatement(); //Query Setup
            resultSet = statement.executeQuery(query);
            
            // check if resultSet is not empty
            if (! resultSet.isBeforeFirst()) { throw new Exception("\n\nQuery returned null"); }
            
            //#####################################################################
            // Getting Query Data Info
            //#####################################################################
            tableColumnDataTypes = get_Column_DataTypes_AL(tableName);
            //System.out.printf("\n\nQuery: %s \nColumn DataTypes: %s", query, tableColumnDataTypes);
            
            noOfColumnsInTable = tableColumnDataTypes.size();
            rowsInQuery = get_Rows_In_Query(query);
            
            if (rowsInQuery == null) { throw new Exception("\n\nRows in Query returned null"); }
        }
        catch (Exception e)
        {
            System.err.printf("\n\n@getTableDataObject() ERROR from query: \n'%s' \n\n %s", query, e);
            return null;
        }
        
        //####################################################################
        // Creating DataSet to Be Returned
        //####################################################################
        try
        {
            ArrayList<ArrayList<Object>> data = new ArrayList<>();
            
            for (int row = 0; row < rowsInQuery; row++)
            {
                resultSet.next(); // go to next row in query results
                
                ArrayList<Object> rowData = new ArrayList<>();
                
                // Filter Through Query Result Data 
                for (int col = 0; col < noOfColumnsInTable; col++)
                {
                    String colData = resultSet.getString(col + 1);
                    String colDataType = tableColumnDataTypes.get(col);
                    
                   // System.out.printf("\n\nRow: %s | Column: %s \nData: %s \nDataType : %s", row, col, colData, colDataType);
    
                    //######################################
                    // Convert Data To Appropriate Datatype
                    //######################################
                    switch (colDataType)
                    {
                        case "varchar":
                            rowData.add(colData);
                            break;
                        case "tinyint":
                            rowData.add(colData.equals("1"));
                            break;
                        case "int":
                            rowData.add(Integer.valueOf(colData));
                            break;
                        case "decimal":
                            rowData.add(new BigDecimal(colData));
                            break;
                        case "bigint": //HELLO convert to long
                            rowData.add(colData);
                            break;
                        case "datetime":
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime result = LocalDateTime.parse(colData, format);
                            rowData.add(result.toString().replaceAll("T", " "));
                            break;
                        case "time":
                            
                            // Remove :00 the seconds from the time
                            String time = String.format("%s", colData)
                                    .replaceFirst(".$", "")
                                    .replaceFirst(".$", "")
                                    .replaceFirst(".$", "");
                            
                            rowData.add(time);
                            break;
                        
                        default:
                            throw new Exception(String.format("\n\n@getTableDataObject() Error With DataType '%s' = ' %s ' !", colDataType, colData));
                    }
                }
                data.add(rowData);
            }
            return data;
        }
        catch (Exception e)
        {
            System.err.printf("\n@getTableDataObject() \nUn-Accounted table data type! \n\n%s", e);
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }
    
    //##################################################################################################################
    //  Get data about a query methods
    //##################################################################################################################
    public String[] get_Column_DataTypes(String tableName)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getColumnDataTypes() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        
        String columnDataTypesQuery = String.format("""                    
                select data_type
                from information_schema.columns
                where table_schema = '%s'
                and table_name = '%s'
                order by ordinal_position;""", databaseName, tableName);
        
        return get_Single_Column_Query(columnDataTypesQuery);
    }
    
    public ArrayList<String> get_Column_DataTypes_AL(String tableName)
    {
        if (! (get_DB_Connection_Status()))
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
        
        return get_Single_Column_Query_AL(columnDataTypesQuery);
    }
    
    public ArrayList<String> get_Column_Names_AL(String tableName)
    {
        if (! (get_DB_Connection_Status()))
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
        return get_Single_Column_Query_AL(columnNamesQuery);
    }
    
    public Integer get_Rows_In_Query(String query)
    {
        if (! (get_DB_Connection_Status()))
        {
            System.out.printf("\n\n  getRowsInQuery() DB couldn't successfully connect to DB '%s'!", databaseName);
            return null;
        }
        
        try
        {
            //Query Setup
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
            System.err.printf("\n\n@getRowsInQuery() ERROR \n%s\n%s", query, e);
            JOptionPane.showMessageDialog(null, String.format("Database Error:\n\nCheck Output "), "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
            //System.exit(1);
        }
        
        return null;
    }
}

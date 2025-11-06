package App_Code.Objects.Database_Objects.JDBC;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

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
    
    private final String
            line_Separator = "############################################################################################################################",
            middle_line_Separator = "###############################################################";
    
    private boolean db_Connection_Status = false;
    
    private HikariDataSource dataSource; // shared connection pool
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MyJDBC(String host, String port, String userName, String password, String databaseName,
                  String db_Script_Folder_Address, String script_List_Name)
    {
        //####################################################################
        //  Setting Variables
        //####################################################################
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName.toLowerCase();
        initial_db_connection = String.format("jdbc:mysql://%s:%s", host, port);
        db_Connection_Address = String.format("%s/%s", initial_db_connection, databaseName);
        
        String
                methodName = "Constructor()",
                errorMSG = "Error, initializing DB!";
        
        //####################################################################
        //  Attempt DP Connection
        //####################################################################
        try
        {
            // User Feedback
            System.out.printf("\n\n%s \nTesting User Credentials: '%s@%s' & DB: %s \n%s\n\n",
                    line_Separator, userName, host, databaseName, line_Separator);
            
            // Connect TO DB
            reconnect_DB_Connection(db_Connection_Address); // Throws an Error if configuration fails
            
            // Set Configuration Variable
            db_Connection_Status = true;
        }
        catch (Exception x)
        {
            // ############################################
            // Configure Error & Create DB IF Needed
            // ############################################
            if (x.getCause() instanceof SQLException e) // Hikari throws an HikariPool$PoolInitializationException Exception
            {
                // ############################################
                // Catch Exception ERROR
                // ############################################
                int errorCode = e.getErrorCode();// 2003 = Can't connect to MySQL server, 0	= General connection failure
                
                /**
                 *  1045: Access denied (bad username or password)
                 *  2003 : Cannot connect to MySQL
                 *  1049 : Unknown database
                 */
                switch (errorCode)
                {
                    case 1045 -> errorMSG = "Error, incorrect DB User / Password Details in .ENV File!";
                    case 2003 -> errorMSG = "Error, cannot connect to MySQL Server!";
                    case 1049 ->  // Unknown DB / Hasn't been created yet
                    {
                        // #################################################
                        // Re-setup Database
                        // #################################################
                        System.out.printf("\n\n%s\nAttempting to create Database Structure! \n%s", line_Separator, line_Separator);
                        
                        if (create_DB(db_Script_Folder_Address, script_List_Name))
                        {
                            db_Connection_Status = true;
                            return; // Exit method if DB creation succeeded
                        }
                        
                        errorMSG = "Error, Creating DB SCHEMA!";
                    }
                    default -> errorMSG = "Error, Unknown SQL DB Error!";
                }
            }
            
            // ############################################
            // Remove Connection
            // ############################################
            handleException_MYSQL(x, methodName, null, errorMSG);
            close_Connection();
        }
    }
    
    private void reconnect_DB_Connection(String connection_Address) throws Exception
    {
        // ####################################################
        //  Close Connection
        // ####################################################
        close_Connection();
        
        // ####################################################
        //  Create Pool Connection Configuration
        // ####################################################
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl(connection_Address);
        config.setUsername(userName);
        config.setPassword(password);
        config.setMaximumPoolSize(10); // how many connections to keep open
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);  // close idle connections after 60s
        config.setConnectionTimeout(30000); // wait 30s max for a connection
        config.setLeakDetectionThreshold(2000); // optional for debugging
        
        config.setInitializationFailTimeout(5000); // fail if connection can't connect within 5s
        
        // ####################################################
        //  Create Connection & Test
        // ####################################################
        dataSource = new HikariDataSource(config); // Connection pool which provides a connection
        
        System.out.printf("\n\n\n%s\nConnection pool initialized successfully!\n%s", line_Separator, line_Separator);
    }
    
    public boolean create_DB(String db_Script_Folder_Address, String script_List_Name)
    {
        // #############################################################
        //  Variables
        // #############################################################
        String
                path = String.format("%s/%s", db_Script_Folder_Address, script_List_Name),
                methodName = "run_SQL_Script_Folder()",
                errorMSG = String.format("Error, running scripts : '%s'!", script_List_Name),
                update_User_Query = "UPDATE users SET user_name = ? WHERE user_id = 1;";
        
        // #############################################################
        //  Get Folder Path
        // #############################################################
        try (Connection connection = dataSource.getConnection(); // if connection fails, error is thrown, script stops here)
             InputStream listStream = getClass().getResourceAsStream(path)
        )
        {
            if (listStream == null)
            {
                throw new Exception(String.format("\n\nrun_SQL_Script_Folder() Error Loading = NULL \n%s", path));
            }
            
            // #############################################################
            //  Connect to LocalHost To Create DB
            // #############################################################
            reconnect_DB_Connection(initial_db_connection);
            
            // ############################################################
            //  Create DB Schema Through Scripts
            // #############################################################
            try (BufferedReader file_Names_Reader = new BufferedReader(new InputStreamReader(listStream, StandardCharsets.UTF_8));
                 PreparedStatement statement = connection.prepareStatement(update_User_Query, Statement.RETURN_GENERATED_KEYS)
            )
            {
                connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
                
                // #######################################
                // Create DB Schema
                // #######################################
                Iterator<String> it = file_Names_Reader.lines().iterator();
                while (it.hasNext())
                {
                    //############################
                    // Get Path of File
                    //###########################
                    String fileName = it.next();
                    System.out.printf("\n\n\n%s \nMyJDBC.java %s Executing script: %s\n%s\n\n", line_Separator, methodName, fileName, line_Separator);
                    
                    try (InputStream scriptStream = getClass().getResourceAsStream(String.format("%s/%s", db_Script_Folder_Address, fileName)))
                    {
                        if (scriptStream == null)
                        {
                            throw new Exception(String.format("\nMyJDBC.java %s Script not found: '%s'", methodName, fileName));
                        }
                        
                        //###########################
                        // Execute File Script
                        //###########################
                        try (Reader file_Reader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8))
                        {
                            ScriptRunner runner = new ScriptRunner(connection);
                            runner.setStopOnError(true);
                            runner.runScript(file_Reader);
                            
                            System.out.printf("\n\n%s\nSuccessfully executed script: %s\n%s", middle_line_Separator, fileName, middle_line_Separator);
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                }
                
                // #######################################
                // Replace User Credentials in  DB
                // #######################################
                statement.setString(1, userName);
                statement.executeUpdate();
                
                // ########################################
                // Commit Changes
                // ########################################
                connection.commit(); // Commit Changes beyond current driver
                System.out.printf("\n\n\n%s \nUser & Database Credentials are valid !\n%s", line_Separator, line_Separator);
            }
            
            //##########################################################
            // Error Handling
            //##########################################################
            catch (Exception e)
            {
                rollBack_Connection(connection, methodName, null);
                throw e;
            }
        }
        catch (Exception e)
        {
            handleException_File(e, methodName, errorMSG);
            return false;
        }
        
        // #############################################################
        //  Return DB to Correct Path
        // #############################################################
        try
        {
            reconnect_DB_Connection(db_Connection_Address);
            return true;
        }
        catch (Exception e)
        {
            handleException_File(e, methodName, "Error, Changing DB Path to DB!");
            return false;
        }
    }
    
    //################################################
    // Script Reading Methods
    //################################################
    public boolean run_SQL_Script_Folder(String db_Script_Folder_Address, String script_List_Name)
    {
        // ####################################################
        //  Variables
        // ####################################################
        String path = String.format("%s/%s", db_Script_Folder_Address, script_List_Name);
        String methodName = "run_SQL_Script_Folder()";
        String errorMSG = String.format("Error, running script a scripts in path: \n\n%s", path);
        
        // ####################################################
        //  Get Folder Path
        // ####################################################
        try (Connection connection = dataSource.getConnection(); // if connection fails, error is thrown, script stops here
             InputStream listStream = getClass().getResourceAsStream(path)
        )
        {
            if (listStream == null)
            {
                throw new Exception(String.format("\n\nrun_SQL_Script_Folder() Error Loading = NULL \n%s", path));
            }
            
            // ####################################################
            //  Connect to LocalHost To Create DB
            // ####################################################
            reconnect_DB_Connection(initial_db_connection);
            
            // ####################################################
            //  Reading Script List & Executing Each Script in List
            // ####################################################
            // Resources automatically released in try block / no need for reader.close()
            try (BufferedReader file_Names_Reader = new BufferedReader(new InputStreamReader(listStream, StandardCharsets.UTF_8)))
            {
                connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
                
                Iterator<String> it = file_Names_Reader.lines().iterator();
                while (it.hasNext())
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
                            throw new Exception(String.format("\nMyJDBC.java %s Script not found: '%s'", methodName, fileName));
                        }
                        
                        //##########################################################
                        // Execute File Script
                        //##########################################################
                        try (Reader file_Reader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8))
                        {
                            ScriptRunner runner = new ScriptRunner(connection);
                            runner.setStopOnError(true);
                            runner.runScript(file_Reader);
                            
                            System.out.printf("\nMyJDBC.java %s successfully executed script: %s", methodName, fileName);
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                    //##########################################################
                    // Error Handling
                    //##########################################################
                    catch (Exception e)
                    {
                        throw e;
                    }
                }
                
                connection.commit(); // Commit Changes beyond current driver
                return true;
            }
            //##########################################################
            // Error Handling
            //##########################################################
            catch (Exception e)
            {
                rollBack_Connection(connection, methodName, null);
                throw e;
            }
        }
        catch (Exception e)
        {
            handleException_File(e, methodName, errorMSG);
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
    public boolean write_Txt_To_SQL_File(String sqlFilePath, String txt_To_Write_To_SQL_File, String errorMSG)
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
            handleException_File(e, methodName, errorMSG);
            return false;
        }
        
        //##########################################################
        // Renaming File
        //##########################################################
        return rename_File(tempFilePath, sqlFilePath, methodName, errorMSG);
    }
    
    public boolean replace_Txt_In_SQL_File(String sqlFilePath, boolean multiValues, String txt_To_Find, String
            txt_Replacement, String errorMSG)
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
            handleException_File(e, methodName, errorMSG);
            return false;
        }
        
        //##########################################################
        // Renaming File
        //##########################################################
        return rename_File(tempFilePath, sqlFilePath, methodName, errorMSG);
    }
    
    /**
     * In most files there's at least one insert value which avoids completely deleting all elements and the file no ending in an ';'
     *
     * @param filePath
     * @param txtToDelete
     * @return
     */
    public boolean delete_Txt_In_File(String filePath, String txtToDelete, String errorMSG)
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
            handleException_File(e, methodName, errorMSG);
            return false;
        }
        
        //#################################################################################
        // Renaming File
        //#################################################################################
        return rename_File(tempFilePath, filePath, methodName, errorMSG);
    }
    
    private boolean rename_File(String tempFilePath, String filePath, String methodName, String errorMSG)
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
            handleException_File(e, methodName, errorMSG);
            return false;
        }
    }
    
    //##################################################################################################################
    // Uploading Data to DB Methods
    //##################################################################################################################
    
    /**
     * Only works on auto-increment ID's
     *
     * @param query            = "INSERT INTO employees (name, position) VALUES (?, ?)";
     * @param insertParameters = The ? Parameters
     * @return
     *
     *
     */
    public Integer insert_And_Get_ID(String query, Object[] insertParameters, String errorMSG)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String
                methodName = "insert_And_Get()",
                query_Combined = String.format("%s \n%s", query, Arrays.toString(insertParameters));
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Connection connection = dataSource.getConnection(); // Get a Connection from pool
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        )
        {
            connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
            
            //##################################
            // Parameters Count Checks
            //##################################
            int
                    expected_Param_Count = (int) query.toLowerCase().chars().filter(ch -> ch == '?').count(),
                    actual_Param_Count = insertParameters.length;
            
            if (expected_Param_Count != actual_Param_Count)
            {
                throw new Exception(String.format("Parameter Counts don't match! \nExpected: %s \nReceived: %s",
                        expected_Param_Count, actual_Param_Count));
            }
            if (actual_Param_Count == 0)
            {
                throw new Exception("Insert Method Requires Parameters!! Method was supplied with 0!!");
            }
            
            //##################################
            // Prepare Statements
            //##################################
            for (int pos = 1; pos <= actual_Param_Count; pos++)
            {
                statement.setString(pos, String.valueOf(insertParameters[pos - 1]));
            }
            
            //##################################
            // Execute Insert Query
            //##################################
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) { throw new Exception("\nNo Rows Inserted!"); }
            
            try (ResultSet rs = statement.getGeneratedKeys())
            {
                if (! rs.next()) // Cannot Get ID, Throw ERROR
                {
                    throw new SQLException("Insert succeeded, but no generated ID was returned.");
                }
                
                connection.commit();// Commit Changes beyond current driver
                return Math.toIntExact(rs.getLong(1));
            }
            catch (Exception e)
            {
                rollBack_Connection(connection, methodName, query_Combined);
                throw e;
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query_Combined, errorMSG);
            return null;
        }
    }
    
    /**
     * This method can upload one statement or, multiple queries within a single String after each statement in the string is separated by a ;
     */
    
    public boolean upload_Data(String query, String errorMSG)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "upload_Data()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Connection connection = dataSource.getConnection(); // Get a Connection from pool
             Statement statement = connection.createStatement();
        )
        {
            connection.setAutoCommit(false); // Update as OneBatch
            
            statement.executeUpdate(query);
            connection.commit();
            
            connection.setAutoCommit(true);
            
            return true;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query, errorMSG);
            return false;
        }
    }
    
    /**
     * If one query fails the whole queries fails
     * The changes made by a previous query in the list isn't visible to the query after it, the updates are made altogether
     */
    public boolean upload_Data_Batch_Altogether(String[] queries, String errorMSG)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "upload_Data_Batch_Altogether()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##########################################################
        // Query Setup
        try (Connection connection = dataSource.getConnection(); // Get a Connection from pool
             Statement statement = connection.createStatement()
        )
        {
            try
            {
                connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
                
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
                statement.executeBatch(); // Execute Batch Commits
                connection.commit(); // Commit Changes beyond current driver
                
                return true; // Return Output
                
            }
            catch (Exception e)
            {
                rollBack_Connection(connection, methodName, queries);
                throw e;
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, queries, errorMSG);
            return false;
        }
    }
    
    public boolean upload_Data_Batch_Altogether2(LinkedHashMap<String, String[]> queries_And_Params, String errorMSG)
    {
        //#############################################################################
        // Check DB Status
        //#############################################################################
        String methodName = "upload_Data_Batch_Altogether()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##############################################################################
        // Execute
        //#############################################################################
        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
            
            //###############################################
            // For Loop For Queries & Params
            //###############################################
            for (Map.Entry<String, String[]> entry : queries_And_Params.entrySet())
            {
                //#########################
                // Entry Values
                //#########################
                String query = entry.getKey();
                String[] insertParameters = entry.getValue();
                
                boolean skipParams = insertParameters == null;
                
                //#########################
                // Execute Queries
                //#########################
                try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    // Setup Params if statement has any
                    if (! skipParams)
                    {
                        // Prepare Statements
                        for (int pos = 1; pos <= insertParameters.length; pos++)
                        {
                            statement.setString(pos, String.valueOf(insertParameters[pos - 1]));
                        }
                    }
                    
                    //Executing the Statement
                    statement.executeUpdate();
                }
                
                catch (Exception e)
                {
                    rollBack_Connection(connection, methodName, queries_And_Params);
                    throw e;
                }
            }
            //###############################################
            //Executing the Batch
            //###############################################
            connection.commit(); // Commit Changes beyond current driver
            return true; // Return Output
            
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, null, errorMSG);
            return false;
        }
    }
    
    /*
      Each query upload is executed separately and the query after, it can notice the changes
     */
    public boolean upload_Data_Batch_Independently(String[] queries, String errorMSG)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "upload_Data_Batch_Independently()";
        
        if (! is_DB_Connected(methodName)) { return false; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (Connection connection = dataSource.getConnection(); // Creates a connection pool
             Statement statement = connection.createStatement()
        )
        {
            try
            {
                connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
                
                for (String query : queries)  // Executing Each Statement
                {
                    statement.executeUpdate(query);
                }
                
                connection.commit(); // Commit Completely
                
                return true; // Return Values
            }
            catch (Exception e)
            {
                rollBack_Connection(connection, methodName, queries);
                throw e;
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, queries, errorMSG);
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
    public ArrayList<ArrayList<String>> get_Multi_Column_Query(String query, String errorMSG)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "get_Multi_Column_Query()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Execute Query
        //##########################################################
        try (
                Connection connection = dataSource.getConnection(); // Get a Connection from pool
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            ResultSetMetaData rs_MetaData = resultSet.getMetaData();
            int columnSize = rs_MetaData.getColumnCount(); // Get size of query
            
            ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
            while (resultSet.next())  // For each row of the query, compile results
            {
                ArrayList<String> tempList = new ArrayList<>(); // storing  all the columns results of a record
                
                // Add each column of the query to an overall string which represents the query row
                for (int i = 1; i <= columnSize; i++)
                {
                    tempList.add(resultSet.getString(i));
                }
                arrayList.add(tempList);
            }
            return arrayList;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query, errorMSG);
            return null;
        }
    }
    
    public ArrayList<ArrayList<Object>> get_TableData_Objects_AL(String query, String tableName, String errorMSG)
    {
        //#########################################################################
        // Check DB Status
        //#########################################################################
        String methodName = "get_TableData_Objects_AL()";
        
        if (! is_DB_Connected(methodName)) { System.out.printf("\n\nConnection No"); return null; }
        
        //#########################################################################
        // Query Setup
        //#########################################################################
        try (
                Connection connection = dataSource.getConnection(); // Get a Connection from pool
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query))
        {
            if (! resultSet.isBeforeFirst()) { return null; } // checks if any data was returned
            
            //#####################################################################
            // Getting Query Data Info
            //#####################################################################
            ArrayList<ArrayList<Object>> data = new ArrayList<>();
            ArrayList<String> table_Col_DataTypes = get_Column_DataTypes_AL(tableName);
            
            if (table_Col_DataTypes == null)
            {
                throw new Exception(String.format("get_Column_DataTypes_AL() for '%s' table returned null", tableName));
            }
            
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
                            throw new Exception(String.format("\n\nError With DataType '%s' = ' %s ' !", colDataType, colData));
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
            handleException_MYSQL(e, methodName, query, errorMSG);
            return null;
        }
    }
    
    //######################################################
    // Get Single Row Query
    //######################################################
    public ArrayList<String> get_Single_Column_Query_AL(String query, String errorMSG)
    {
        return (ArrayList<String>) get_Single_Column_Query(query, new ArrayList<>(), errorMSG);
    }
    
    public TreeSet<String> get_Single_Col_Alphabetically_Sorted(String query, String errorMSG)
    {
        return (TreeSet<String>) get_Single_Column_Query(query, new TreeSet<>(Collator.getInstance()), errorMSG);
    }
    
    private Collection<String> get_Single_Column_Query(String query, Collection<String> collection, String errorMSG)
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        String methodName = "get_Single_Col_Alphabetically_Sorted()";
        
        if (! is_DB_Connected(methodName)) { return null; }
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (
                Connection connection = dataSource.getConnection(); // Get a Connection from pool
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)
        )
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
            while (resultSet.next()) // for each row of the query
            {
                collection.add(resultSet.getString(1));
            }
            return collection;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, methodName, query, errorMSG);
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
        return get_Single_Column_Query_AL(columnDataTypesQuery, String.format("Error, getting DataTypes get_Column_DataTypes_AL() for Table: %s", tableName));
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
        return get_Single_Column_Query_AL(columnNamesQuery, String.format("Error, getting DataTypes get_Column_Names_AL() for Table: %s", tableName));
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void close_Connection()
    {
        if (dataSource != null && ! dataSource.isClosed())
        {
            dataSource.close();
        }
    }
    
    private void rollBack_Connection(Connection connection, String methodName, Object queries)
    {
        //#############################
        // Exit
        //#############################
        if (connection == null) { return; }
        
        //#############################
        // Rollback
        //#############################
        try
        {
            // IF Rollback wasn't executed return
            if (connection.getAutoCommit()) { return; }
            
            // Execute Rollback
            connection.rollback();
            System.err.println("\n\nRollback successful for method: " + methodName);
        }
        catch (SQLException x)
        {
            System.err.println("\n\nRollback failed in " + methodName + ": " + x.getMessage());
            
            handleException_MYSQL(x, methodName, queries, null);
        }
        finally
        {
            try {
                connection.close(); // tells Hikari to evict this bad connection
            } catch (SQLException ignore) {}
        }
    }
    
    //###########################################
    // SQL Methods Error Handling
    //###########################################
    private void handleException_MYSQL(Exception e, String methodName, Object query, String errorMSG)
    {
        //#########################
        //
        //#########################
        if (e instanceof SQLException x)
        {
            print_SQL_ERR_MSG(x, methodName, query);
        }
        else
        {
            print_Exception_ERR_MSG(e, methodName, query);
        }
        
        //#########################
        // Display MSGS
        //#########################
        if (errorMSG == null) { return; }
        
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
        
        JOptionPane.showMessageDialog(null, errorMSG, "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_SQL_ERR_MSG(SQLException e, String methodName, Object query)
    {
        System.err.printf("\n\nMyJDBC.java @%s SQL ERROR \n\nQuery: %s \n\nMessage: %s \n\nSQLState: %s \n\nErrorCode: %d\n\n",
                methodName, query != null ? query.toString() : "", e.getMessage(), e.getSQLState(), e.getErrorCode());
    }
    
    private void print_Exception_ERR_MSG(Exception e, String methodName, Object query)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\n%s \n\n%s", methodName, query != null ? query.toString() : "", e.getMessage());
    }
    
    //###########################################
    // File Methods for Error Handling
    //###########################################
    private void handleException_File(Exception e, String methodName, String errorMSG)
    {
        //#########################
        // Switch Exceptions
        //#########################
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
        
        //#########################
        // Display MSGS
        //#########################
        JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!", "Alert Message: ",
                JOptionPane.INFORMATION_MESSAGE);
        
        JOptionPane.showMessageDialog(null, errorMSG, "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_Exception_ERR_MSG(Exception e, String methodName)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\n%s", methodName, e.getMessage());
    }
    
    private void print_File_Not_Found_ERR_MSG(FileNotFoundException e, String methodName)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\nFile not found: %s%n", methodName, e.getMessage());
    }
    
    private void print_IO_Exception_ERR_MSG(IOException e, String methodName)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\nI/O error while processing files: %s%n", methodName, e.getMessage());
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
}

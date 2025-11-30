package App_Code.Objects.Database_Objects.JDBC;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.javatuples.Pair;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.text.Collator;
import java.time.LocalTime;
import java.util.*;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MyJDBC
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private String
            class_Name = "MyJDBC.java",
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
                method_Name = "Constructor()",
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
            handleException_MYSQL(x, method_Name, null, errorMSG);
            close_Connection();
        }
    }
    
    // #######################################################
    // DB Setup Methods
    // #######################################################
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
                method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName()),
                errorMSG = String.format("Error, running scripts : '%s'!", script_List_Name),
                update_User_Query = "UPDATE users SET user_name = ? WHERE user_id = 1;";
        
        // #############################################################
        //  Connect to LocalHost To Create DB
        // #############################################################
        try
        {
            reconnect_DB_Connection(initial_db_connection);
        }
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, null, "Error, Connecting TO Initial DB!!");
            return false;
        }
        
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
                    System.out.printf("\n\n\n%s \nMyJDBC.java %s Executing script: %s\n%s\n\n", line_Separator, method_Name, fileName, line_Separator);
                    
                    try (InputStream scriptStream = getClass().getResourceAsStream(String.format("%s/%s", db_Script_Folder_Address, fileName)))
                    {
                        if (scriptStream == null)
                        {
                            throw new Exception(String.format("\nMyJDBC.java %s Script not found: '%s'", method_Name, fileName));
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
                rollBack_Connection(connection, method_Name, null);
                throw e;
            }
        }
        catch (Exception e)
        {
            handleException_File(e, method_Name, errorMSG);
            close_Connection();
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
            handleException_File(e, method_Name, "Error, Changing DB Path to App DB Path!");
            return false;
        }
    }
    
    //##################################################################################################################
    // Script Reading :  Methods
    //##################################################################################################################
    public boolean run_SQL_Script_Folder(String db_Script_Folder_Address, String script_List_Name)
    {
        // ####################################################
        //  Variables
        // ####################################################
        String
                path = String.format("%s/%s", db_Script_Folder_Address, script_List_Name),
                method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName()),
                errorMSG = String.format("Error, running script a scripts in path: \n\n%s", path);
        
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
                    System.out.printf("\nMyJDBC.java %s Executing script: %s \n\n", method_Name, fileName);
                    
                    try (InputStream scriptStream = getClass().getResourceAsStream(String.format("%s/%s", db_Script_Folder_Address, fileName)))
                    {
                        if (scriptStream == null)
                        {
                            throw new Exception(String.format("\nMyJDBC.java %s Script not found: '%s'", method_Name, fileName));
                        }
                        
                        //##########################################################
                        // Execute File Script
                        //##########################################################
                        try (Reader file_Reader = new InputStreamReader(scriptStream, StandardCharsets.UTF_8))
                        {
                            ScriptRunner runner = new ScriptRunner(connection);
                            runner.setStopOnError(true);
                            runner.runScript(file_Reader);
                            
                            System.out.printf("\nMyJDBC.java %s successfully executed script: %s", method_Name, fileName);
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
                rollBack_Connection(connection, method_Name, null);
                throw e;
            }
        }
        catch (Exception e)
        {
            handleException_File(e, method_Name, errorMSG);
            return false;
        }
    }
    
    private boolean run_SQL_Script(String sql_Script_Address)
    {
        return false;
    }
    
    //##################################################################################################################
    // File Writing : Methods
    //##################################################################################################################
    public boolean write_Txt_To_SQL_File(String sqlFilePath, String txt_To_Write_To_SQL_File, String errorMSG)
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
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
            handleException_File(e, method_Name, errorMSG);
            return false;
        }
        
        //##########################################################
        // Renaming File
        //##########################################################
        return rename_File(tempFilePath, sqlFilePath, method_Name, errorMSG);
    }
    
    public boolean replace_Txt_In_SQL_File(String sqlFilePath, boolean multiValues, String txt_To_Find, String
            txt_Replacement, String errorMSG)
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
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
                        method_Name, tempFilePath);
                
                return false;
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_File(e, method_Name, errorMSG);
            return false;
        }
        
        //##########################################################
        // Renaming File
        //##########################################################
        return rename_File(tempFilePath, sqlFilePath, method_Name, errorMSG);
    }
    
    /*
     * In most files there's at least one insert value which avoids completely deleting all elements and the file no ending in an ';'
     *
     * @param filePath
     * @param txtToDelete
     * @return
     */
    public boolean delete_Txt_In_File(String filePath, String txtToDelete, String errorMSG)
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
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
                        method_Name, tempFilePath);
                
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
            handleException_File(e, method_Name, errorMSG);
            return false;
        }
        
        //#################################################################################
        // Renaming File
        //#################################################################################
        return rename_File(tempFilePath, filePath, method_Name, errorMSG);
    }
    
    private boolean rename_File(String tempFilePath, String filePath, String method_Name, String errorMSG)
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
            handleException_File(e, method_Name, errorMSG);
            return false;
        }
    }
    
    //##################################################################################################################
    // Uploading Data : Methods
    //##################################################################################################################
    public boolean upload_Data(String query, Object[] insertParameters, String errorMSG) // Singular Query Upload Methods
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //##############################################################################
        // Execute
        //#############################################################################
        try (Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
            
            //###############################################
            // For Loop Through Params & Execute  Query
            //###############################################
            boolean skipParams = insertParameters == null;
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
            {
                // Setup Params if statement has any
                if (! skipParams)
                {
                    // Prepare Statements
                    for (int pos = 1; pos <= insertParameters.length; pos++)
                    {
                        Object object = insertParameters[pos - 1];
                        
                        // Set Statement Params etc; statement.setString(x , y)
                        set_Statement_Params(statement, pos, object);
                    }
                }
                
                //###############################################
                // Execute Statement & Commit Connection
                //###############################################
                statement.executeUpdate();
                connection.commit(); // Commit Changes beyond current driver
                return true; // Return Output
                
            }
            catch (Exception e)
            {
                rollBack_Connection(connection, method_Name, query); // Rollback, in case it's not automatically done
                throw e;
            }
            finally
            {
                connection.setAutoCommit(false); // Reset Commit, in case it's not automatically done
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, query, errorMSG);
            return false;
        }
    }
    
    // #######################################################
    // Upload Batch : Methods
    // #######################################################
    // Internal Method
    private void upload_Data_Batch_Internally
    (Connection connection, String origin_Method_Name, LinkedHashSet<Pair<String, Object[]>> queries_And_Params) throws Exception
    {
        //###############################################
        // Set Settings For Connection & Variables
        //###############################################
        String
                query = "",
                method_Name = String.format("%s ---> %s()", origin_Method_Name, new Object() { }.getClass().getEnclosingMethod().getName());
        
        Object[] insertParameters;
        
        connection.setAutoCommit(false); // Prevents each query from being singularly uploaded & is only made not temp when committed
        
        int query_Pos = 1;
        //###############################################
        // For Loop For Queries & Params
        //###############################################
        for (Pair<String, Object[]> entry : queries_And_Params)
        {
            //#########################
            // Entry Values
            //#########################
            query = entry.getValue0();
            insertParameters = entry.getValue1();
            
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
                        Object object = insertParameters[pos - 1];
                        set_Statement_Params(statement, pos, object); // Set Statement Params etc; statement.setString(x , y)
                    }
                }
                
                //Executing the Statement
                statement.executeUpdate();
            }
            catch (Exception e)
            {
                print_Internal_Method_Err_MSG(method_Name, query, insertParameters, e);
                
                throw e;
            }
            
            //#########################
            // Increase
            //#########################
            query_Pos++;
        }
    }
    
    // #########################
    // Upload Batch : Methods
    // #########################
    public boolean upload_Data_Batch(LinkedHashSet<Pair<String, Object[]>> queries_And_Params, String errorMSG)
    {
        //###############################################################
        // Check DB Status
        //###############################################################
        String
                query = "",
                method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //############################################################
        // Execute
        //############################################################
        try (Connection connection = dataSource.getConnection())
        {
            upload_Data_Batch_Internally(connection, method_Name, queries_And_Params);  // Upload Batch
            
            connection.commit(); // Commit Changes beyond current driver
            return true; // Return Output
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, query, errorMSG);
            return false;
        }
    }
    
    
    /***
     * @param upload_Queries_And_Params
     * @param errorMSG
     * @return
     *
     * DataTypes of params need to be converted to their expected Type  before being passed in as a param in java equivalent
     *      * to MYSQL's type
     *      * Although all params can be passed as strings the statement type conversion should match the type the schema expects
     *      * as MYSQL does this internally and if it fails will cause the following errors:
     *      * .) silent truncation,
     *      * .) rounding errors,
     *      * .)  or outright SQL exceptions.
     *      *
     *      * So it's better to convert before MYSQL handles it.
     *
     *
     */
    public Fetched_Results upload_And_Get_Batch(LinkedHashSet<Pair<String, Object[]>> upload_Queries_And_Params,
                                                LinkedHashSet<Pair<String, Object[]>> get_Queries_And_Params, String errorMSG)
    {
        //###############################################################
        // Check DB Status & Variables
        //###############################################################
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        
        //###############################################################
        // Variables
        //###############################################################
        Fetched_Results fetched_Results = new Fetched_Results();
        
        //##############################################################
        // Execute Upload Params
        //##############################################################
        try (Connection connection = dataSource.getConnection())
        {
            try
            {
                // Upload Statements
                upload_Data_Batch_Internally(connection, method_Name, upload_Queries_And_Params);  // Upload Batch
                
                // Execute Fetch Statements
                for (Pair<String, Object[]> fetch_Obj : get_Queries_And_Params)  // Execute Get Statements
                {
                    String query = fetch_Obj.getValue0();
                    Object[] params = fetch_Obj.getValue1();
                    
                    // Add Fetch Results To Object made for storing multiple queries
                    fetched_Results.add_2D_Result(get_2D_Object_AL_Internally(connection, method_Name, query, params));
                }
                
                // Commit Changes beyond current driver
                connection.commit();
                
                // Return Output
                return fetched_Results;
            }
            catch (Exception e)
            {
                rollBack_Connection(connection, method_Name, null); // Rollback, in case it's not automatically done
                throw e;
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, null, errorMSG);
            return null; // Return Output
        }
    }
    
    //##################################################################################################################
    // DB Get Methods
    //##################################################################################################################
    
    /**
     * Allows Multiple Types Of Collections
     * <p>
     * <T, C extends Collection<T>> C getData(Supplier<C> factory) {
     * C collection = factory.get();  // new collection created inside
     * collection.add(...);
     * return collection;
     * }
     * <p>
     * ArrayList<String> list = getData(ArrayList::new);
     * TreeSet<String> set = getData(TreeSet::new);
     *
     */
    
    private <T, C extends Collection<T>> C get_Single_Column_Internally
    (String query, Object[] params, String method_Name, String errorMSG, Class<T> type, Supplier<C> collectionType) throws Exception
    {
        //##########################################################
        // Check DB Status
        //##########################################################
        method_Name = String.format("%s -> %s()", method_Name, new Object() { }.getClass().getEnclosingMethod().getName());
        
        // Create List Object from Scratch inside method
        C collection = collectionType.get();
        
        //##########################################################
        // Query Setup
        //##########################################################
        try (
                Connection connection = dataSource.getConnection(); // Get a Connection from pool
                PreparedStatement statement = connection.prepareStatement(query)
        )
        {
            //############################################
            // Prepare Params
            //############################################
            if (params != null)
            {
                for (int pos = 1; pos <= params.length; pos++)
                {
                    Object object = params[pos - 1];
                    set_Statement_Params(statement, pos, object); // Set Statement Params etc; statement.setString(x , y)
                }
            }
            
            //############################################
            // Execute Query
            //############################################
            ResultSet resultSet = statement.executeQuery();
            
            if (! resultSet.isBeforeFirst()) { return collection; } // checks if any data was returned
            
            //############################################
            // Catch Exception (Multiple Columns)
            //############################################
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnSize = rsmd.getColumnCount();
            
            if (columnSize > 1) // if query_And_Params has multiple columns this method cannot produce a 2d list results,
            {
                throw new Exception("\n\nQuery size has multiple Columns, use multi-line query_And_Params !!!");
            }
            
            //############################################
            // Storing query_And_Params data in String[]
            //############################################
            while (resultSet.next())
            {
                collection.add(type.cast(resultSet.getObject(1)));
            }
            return collection;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            query = String.format("\n\n%s \n%n%s", query, Arrays.toString(params));
            
            handleException_MYSQL(e, method_Name, query, errorMSG);
            throw new Exception(throw_Exception_Msg(method_Name));
        }
    }
    
    //######################################################
    // Different Types Of Single Column Collections
    //######################################################
    public ArrayList<String> get_Single_Col_Query_String(String query, Object[] params, String errorMSG) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        return get_Single_Column_Internally(query, params, method_Name, errorMSG, String.class, ArrayList :: new);
    }
    
    public ArrayList<Object> get_Single_Col_Query_Obj(String query, Object[] params, String errorMSG) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        return get_Single_Column_Internally(query, params, method_Name, errorMSG, Object.class, ArrayList :: new);
    }
    
    public ArrayList<Integer> get_Single_Col_Query_Int(String query, Object[] params, String errorMSG) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        return get_Single_Column_Internally(query, params, method_Name, errorMSG, Integer.class, ArrayList :: new);
    }
    
    public TreeSet<String> get_Single_Col_Query_Ordered_TS(String query, Object[] params, String errorMSG)
    {
        try
        {
            String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
            return get_Single_Column_Internally(
                    query,
                    params,
                    method_Name,
                    errorMSG,
                    String.class,
                    () -> new TreeSet<>(Collator.getInstance())
            );
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    //######################################################
    //  MetaData Methods (Done)
    //######################################################
    public ArrayList<String> get_Column_Names_AL(String tableName) throws Exception
    {
        // Setup Method_Name for diagnosis
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        // Query Setup
        String columnNamesQuery = """
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = ?
                AND table_name = ?
                ORDER BY ordinal_position;""";
        
        
        // Setup Params
        Object[] params = new Object[]{ databaseName, tableName };
        String errorMSG = String.format("Error, getting DataTypes get_Column_Names_AL() for Table: %s", tableName);
        
        // Execute
        ArrayList<String> colum_Names = get_Single_Col_Query_String(columnNamesQuery, params, errorMSG);
        
        // Output
        if (colum_Names == null || colum_Names.isEmpty()) // Table Names cannot be null
        {
            throw new Exception(String.format("\n\nError %s -> %s = Empty Result", class_Name, method_Name));
        }
        
        return colum_Names;
    }
    
    //##################################################################################################################
    // Multi Methods
    //##################################################################################################################
    private <T> ArrayList<ArrayList<T>> get_2D_ArrayList_Internally(Connection connection, String method_Name, String query, Object[] params,
                                                                    Class<T> typeCast) throws Exception
    {
        //#########################################################################
        // Variables
        //#########################################################################
        ArrayList<ArrayList<T>> collection = new ArrayList<>();
        
        method_Name = String.format("%s ---> %s()", method_Name, new Object() { }.getClass().getEnclosingMethod().getName());
        
        //#########################################################################
        // Query Setup
        //#########################################################################
        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            //############################################
            // Prepare Params
            //############################################
            if (params != null)
            {
                for (int pos = 1; pos <= params.length; pos++)
                {
                    Object object = params[pos - 1];
                    set_Statement_Params(statement, pos, object); // Set Statement Params etc; statement.setString(x , y)
                }
            }
            
            //############################################
            // Execute Query
            //############################################
            ResultSet resultSet = statement.executeQuery();
            
            // checks if any data was returned
            if (! resultSet.isBeforeFirst()) { return collection; }
            
            //#####################################################################
            // Getting Query Data Info
            //#####################################################################
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnSize = rsmd.getColumnCount();
            
            //#####################################################################
            // Compile Results
            //#####################################################################
            while (resultSet.next())
            {
                ArrayList<T> rowData = new ArrayList<>();
                
                for (int col = 1; col <= columnSize; col++) // Filter Through Query Result Data
                {
                    Object obj = resultSet.getObject(col);
                    
                    rowData.add(typeCast.cast(obj)); // Converts Object to type and casts it
                }
                collection.add(rowData);
            }
            return collection;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            print_Internal_Method_Err_MSG(method_Name, query, params, e);
            throw e;
        }
    }
    
    private ArrayList<ArrayList<Object>> get_2D_Object_AL_Internally(Connection connection, String method_Name, String query, Object[] params) throws Exception
    {
        return get_2D_ArrayList_Internally(connection, method_Name, query, params, Object.class);
    }
    
    //#######################################
    // Different Types
    //#######################################
    public ArrayList<ArrayList<Object>> get_2D_Query_AL_Object(String query, Object[] params, String errorMSG)
    {
        //#############################
        // Query Setup
        //#############################
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        try (Connection connection = dataSource.getConnection()) // Get a Connection from pool
        {
            return get_2D_Object_AL_Internally(connection, method_Name, query, params);
        }
        //#############################
        // Error Handling
        //#############################
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, query, errorMSG);
            return null;
        }
    }
    
    public ArrayList<ArrayList<String>> get_2D_Query_AL_String(String query, Object[] params, String errorMSG)
    {
        //#############################
        // Query Setup
        //#############################
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        try (Connection connection = dataSource.getConnection()) // Get a Connection from pool
        {
            return get_2D_ArrayList_Internally(connection, method_Name, query, params, String.class);
        }
        //#############################
        // Error Handling
        //#############################
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, query, errorMSG);
            return null;
        }
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
    
    // -1 = Failed , 0 = Empty, 1 = Not Empty
    public <T> boolean expected_Query_Output(int x, Collection<T> c) throws Exception
    {
        return is_Results_Empty(c) == x;
    }
    
    public int is_Results_Empty(Collection<?> c) throws Exception
    {
        if (c == null) { return - 1; } // Failed
        else if (c.isEmpty()) { return 0; } // Empty
        else { return 1; } // Not empty
    }
    
    // Param Methods
    private void set_Statement_Params(PreparedStatement statement, int pos, Object object) throws Exception
    {
        // Switches based on Object type
        switch (object)
        {
            // NULL
            case null -> throw new Exception(String.format(
                    "Received untyped NULL at position %d.%n" +
                            "Convert nulls explicitly using Null_MYSQL_Field, e.g.%n" +
                            "add(new Pair<>(query, new Object[]{ new Null_MYSQL_Field(Types.INTEGER), PDID }));",
                    pos
            ));
            
            // Expect Self Created  NULL Type
            case Null_MYSQL_Field nullMysqlField -> statement.setNull(pos, nullMysqlField.getSqlType());
            
            case String s -> statement.setString(pos, s); // String
            case Integer i -> statement.setInt(pos, i); // Integer
            case Boolean b -> statement.setBoolean(pos, b); // Boolean
            case BigDecimal bigDecimal -> statement.setBigDecimal(pos, bigDecimal); // BigDecimal
            case Timestamp timestamp -> statement.setTimestamp(pos, timestamp);  // TimeStamp / LocalDateTime
            case LocalDateTime localDateTime -> // Local Date Time
                    statement.setTimestamp(pos, Timestamp.valueOf(localDateTime));
            case LocalTime localTime -> statement.setTime(pos, Time.valueOf(localTime)); // LocalTime
            case Float f -> statement.setFloat(pos, f);
            case Double d -> statement.setDouble(pos, d);
            
            // Exception clause
            default -> throw new Exception(String.format("Unable to configure param dataType of object being '%s' - %s"
                    , object.toString(), object.getClass().getSimpleName()));
        }
    }
    
    //###############################################################################
    // Validation Methods
    //###############################################################################
    public boolean get_DB_Connection_Status()
    {
        return db_Connection_Status;
    }
    
    //##############################################################################
    // Error Handling Methods
    //##############################################################################
    private void rollBack_Connection(Connection connection, String method_Name, Object queries)
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
            if (connection.getAutoCommit()) { return; } // IF Rollback wasn't executed return
            
            connection.rollback(); // Execute Rollback
            System.err.printf("\n\n%s \nRollback successful for method: %s \n%s\n\n\n", middle_line_Separator, method_Name, middle_line_Separator);
        }
        catch (SQLException x)
        {
            System.err.printf("\n\n%s \nRollback failed for method: %s \n\n\n", middle_line_Separator, method_Name, middle_line_Separator);
            
            // Change Method Name to this method & Display Error
            method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
            handleException_MYSQL(x, method_Name, queries, null);
        }
        finally
        {
            try
            {
                connection.close(); // tells Hikari to evict this bad connection
            }
            catch (SQLException ignore) { }
        }
    }
    
    //###########################################
    // SQL Methods Error Handling
    //###########################################
    private void handleException_MYSQL(Exception e, String method_Name, Object query, String errorMSG)
    {
        //#########################
        //
        //#########################
        if (e instanceof SQLException x)
        {
            print_SQL_ERR_MSG(x, method_Name, query);
        }
        else
        {
            print_Exception_ERR_MSG(e, method_Name, query);
        }
        
        //#########################
        // Display MSGS
        //#########################
        if (errorMSG == null) { return; }
        
        /*JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!",
                "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);*/
        
        JOptionPane.showMessageDialog(null, errorMSG, "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_SQL_ERR_MSG(SQLException e, String method_Name, Object query)
    {
        System.err.printf("\n\n%s\nMyJDBC.java @%s SQL ERROR \n%s \n\nQuery: \n\"\"\"\n %s\n\"\"\" \n\nError Message: \n\n\"\"\" \n\n%s \n\n\"\"\" \n\nSQLState: %s \n\nErrorCode: %d\n\n",
                line_Separator, method_Name, line_Separator, query != null ? query.toString() : "", e.getMessage(), e.getSQLState(), e.getErrorCode());
    }
    
    private void print_Exception_ERR_MSG(Exception e, String method_Name, Object query)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\n%s \n\n%s", method_Name, query != null ? query.toString() : "", e.getMessage());
    }
    
    //###########################################
    // File Methods for Error Handling
    //###########################################
    private void handleException_File(Exception e, String method_Name, String errorMSG)
    {
        //#########################
        // Switch Exceptions
        //#########################
        if (e instanceof FileNotFoundException x)
        {
            print_File_Not_Found_ERR_MSG(x, method_Name);
        }
        else if (e instanceof IOException x)
        {
            print_IO_Exception_ERR_MSG(x, method_Name);
        }
        else
        {
            print_Exception_ERR_MSG(e, method_Name);
        }
        
        //#########################
        // Display MSGS
        //#########################
        /*JOptionPane.showMessageDialog(null, "\n\nDatabase Error: \nCheck Output !!", "Alert Message: ",
                JOptionPane.INFORMATION_MESSAGE);*/
        
        JOptionPane.showMessageDialog(null, errorMSG, "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_Exception_ERR_MSG(Exception e, String method_Name)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\n%s", method_Name, e.getMessage());
    }
    
    private void print_File_Not_Found_ERR_MSG(FileNotFoundException e, String method_Name)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\nFile not found: %s%n", method_Name, e.getMessage());
    }
    
    private void print_IO_Exception_ERR_MSG(IOException e, String method_Name)
    {
        System.err.printf("\n\nMyJDBC.java @%s Exception ERROR \n\nI/O error while processing files: %s%n", method_Name, e.getMessage());
    }
    
    //###########################################
    // Method Catch Print Query + Params
    //###########################################
    private void print_Internal_Method_Err_MSG(String method_Name, String query, Object[] params, Exception e)
    {
        System.err.printf("""
                \n\n%s
                MyJDBC.java %s Error
                %s
                \nQuery : \n\n\"\"\" \n\n%s \n\n\"\"\"
                \nParams: \n%s%n
                \nError MSG:  \n\n\"\"\" \n%s \n\"\"\"""", line_Separator, method_Name, line_Separator, query, Arrays.toString(params), e);
    }
    
    private String throw_Exception_Msg(String method_Name)
    {
        return String.format("%s -> %s Error, Query Failed", class_Name, method_Name);
    }
}

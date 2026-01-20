package App_Code.Objects.Database_Objects.MyJDBC;

import App_Code.Objects.Database_Objects.Fetched_Results;
import App_Code.Objects.Database_Objects.Null_MYSQL_Field;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import org.javatuples.Pair;
import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.Collator;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.flywaydb.core.Flyway;
import org.sqlite.SQLiteConfig;

public class MyJDBC_Sqlite  // remove extends eventually
{
    //##################################################################################################################
    // Variable
    //##################################################################################################################
    private final String class_Name;
    private final String db_Connection_Address;
    
    private final String
            line_Separator = "############################################################################################################################",
            middle_line_Separator = "###############################################################";
    
    private boolean db_Connection_Status = false;
    
    private HikariDataSource dataSource; // shared connection pool
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public MyJDBC_Sqlite()
    {
        //#############################################
        // Variables
        //#############################################
        class_Name = this.getClass().getSimpleName();
        // Sqlite ignores username / password
        
        //db_Connection_Address = "jdbc:sqlite:file:./data/gym_app00001;";  // Production
        db_Connection_Address = "jdbc:sqlite:C:/sqlite-data/gym_app00001.db"; // Debugging
        
    }
    
    public void begin_migration() throws Exception
    {
        //#############################################
        // Configure Hikari
        //#############################################
        /*
            If the DB file does not exist:
                 . H2 creates it
                 . Creates the user automatically
        */
        try
        {
            
            Flyway flyway = Flyway.configure()
                    .dataSource(db_Connection_Address, null, null)
                    .baselineOnMigrate(true)
                    .locations("classpath:db/migration/sqlite")
                    .load();
            
            flyway.migrate();
        }
        catch (Exception e)
        {
            handleException_MYSQL(e, get_Method_Name(), null, "Error, initializing DB!");
            close_Connection();
            
            throw new Exception("Failed Migration!");
        }
        
        //#############################################
        // Configure Hikari
        //#############################################
        reconnect_Hikari_DB_Connection(db_Connection_Address);
        
        db_Connection_Status = true;
    }
    
    // #######################################################
    // DB Setup Methods
    // #######################################################
    private void reconnect_Hikari_DB_Connection(String connection_Address)
    {
        // ####################################################
        //  Close Connection
        // ####################################################
        close_Connection();
        
        // ####################################################
        //  SQLite-specific config
        // ####################################################
        SQLiteConfig sqliteConfig = new SQLiteConfig();
        sqliteConfig.enforceForeignKeys(true);
        sqliteConfig.setJournalMode(SQLiteConfig.JournalMode.WAL);
        sqliteConfig.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        sqliteConfig.setBusyTimeout(5000);
        
        // ####################################################
        //  Hikari configuration
        // ####################################################
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(connection_Address);
        hikariConfig.setMaximumPoolSize(1); // correct for SQLite
        hikariConfig.setConnectionTestQuery("SELECT 1");
        
        // 🔑 THIS IS THE IMPORTANT LINE
        hikariConfig.setDataSourceProperties(sqliteConfig.toProperties());
        
        /*
        
               HikariConfig config = new HikariConfig();
            
            // SQLite connection PRAGMAs:
            // Required SQLite connection initialization for correctness, concurrency, and stability
            // - foreign_keys        : Enforces FK constraints (OFF by default in SQLite)
            // - journal_mode = WAL  : Improves concurrency and reduces locking
            // - synchronous = NORMAL: Balanced durability vs performance
            // - busy_timeout        : Waits for locks instead of failing immediately
            config.setJdbcUrl(connection_Address);
            config.setMaximumPoolSize(1); // sqlite can only handle 1 connection / not good at concurrency
            config.setConnectionTestQuery("SELECT 1");
            config.setConnectionInitSql(
                    "PRAGMA foreign_keys = ON;" +
                            "PRAGMA journal_mode = WAL;" +
                            "PRAGMA synchronous = NORMAL;" +
                            "PRAGMA busy_timeout = 5000;"
            );
         */
        
        // ####################################################
        //  Create pool
        // ####################################################
        dataSource = new HikariDataSource(hikariConfig);
        
        System.out.printf("\n\n\n%s\nConnection pool initialized successfully!\n%s", line_Separator, line_Separator);
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
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
            {
                // Setup Params if statement has any
                if (process_Params(insertParameters))
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
                handleException_MYSQL(e, method_Name, query, errorMSG);
                rollBack_Connection(connection, method_Name, query); // Rollback, in case it's not automatically done
                throw new Exception();
            }
            finally
            {
                connection.setAutoCommit(false); // Reset Commit, in case it's not automatically done
            }
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception _)
        {
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
            
            //#########################
            // Execute Queries
            //#########################
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
            {
                // Setup Params if statement has any
                if (process_Params(insertParameters))
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
                                                LinkedHashSet<Pair<String, Object[]>> get_Queries_And_Params, String error_msg)
    {
        //###############################################################
        // Check DB Status & Variables
        //###############################################################
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //##############################################################
        // Execute Upload Params
        //##############################################################
        try (Connection connection = dataSource.getConnection())
        {
            try
            {
                upload_Data_Batch_Internally(connection, method_Name, upload_Queries_And_Params); // Upload Statements
                
                return get_Fetched_Results_Internally(connection, get_Queries_And_Params);  // Fetch Queries
            }
            catch (Exception e)
            {
                rollBack_Connection(connection, method_Name, null); // Rollback, in case it's not automatically done
                handleException_MYSQL(e, method_Name, null, error_msg);
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            return null;
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
    (String query, Object[] params, String method_Name, String errorMSG, boolean allow_No_Results, Class<T> type, Supplier<C> collectionType) throws Exception
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
            if (process_Params(params))
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
            
            if (! resultSet.isBeforeFirst()) // checks if any data was returned
            {
                if (allow_No_Results) { return collection; }
                throw new Exception("Failed Query -> Empty Results");
            }
            
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
                if (type == Object.class) // If Class Type is Object Normalise DB Data As Expecting Multiple Data Types
                {
                    collection.add(type.cast(map_Object_To_Domain_Type(resultSet.getObject(1))));
                    continue;
                }
                
                collection.add(type.cast(resultSet.getObject(1))); // Else Apply Type Cast Specifically Provided
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
            throw new Exception("");
        }
    }
    
    //######################################################
    // Different Types Of Single Column Collections
    //######################################################
    public ArrayList<String> get_Single_Col_Query_String(String query, Object[] params, String errorMSG, boolean allow_No_Results) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        return get_Single_Column_Internally(query, params, method_Name, errorMSG, allow_No_Results, String.class, ArrayList :: new);
    }
    
    public ArrayList<Object> get_Single_Col_Query_Obj(String query, Object[] params, String errorMSG, boolean allow_No_Results) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        return get_Single_Column_Internally(query, params, method_Name, errorMSG, allow_No_Results, Object.class, ArrayList :: new);
    }
    
    public ArrayList<Integer> get_Single_Col_Query_Int(String query, Object[] params, String errorMSG, boolean allow_No_Results) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        return get_Single_Column_Internally(query, params, method_Name, errorMSG, allow_No_Results, Integer.class, ArrayList :: new);
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
                    false,
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
        //##########################################################
        // Query Setup
        //##########################################################
        // Setup Method_Name for diagnosis
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        String error_MSG = "Failed Getting Table Column Names";
        String column_Names_Query = String.format("PRAGMA table_info(%s);", tableName);  // Query doesn't work in prepared statement
        
        //##########################################################
        // Query Execution
        //##########################################################
        try (
                Connection connection = dataSource.getConnection(); // Get a Connection from pool
                PreparedStatement statement = connection.prepareStatement(column_Names_Query)
        )
        {
            //############################################
            // Execute Query
            //############################################
            ResultSet resultSet = statement.executeQuery();
            
            if (! resultSet.isBeforeFirst()) // checks if any data was returned
            {
                throw new Exception("Failed Query -> Empty Results On Table");
            }
            
            //############################################
            // Storing query_And_Params data in String[]
            //############################################
            ArrayList<String> column_names_AL = new ArrayList<>();
            
            while (resultSet.next())
            {
                column_names_AL.add(resultSet.getString("name"));
            }
            return column_names_AL;
        }
        //##########################################################
        // Error Handling
        //##########################################################
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, column_Names_Query, error_MSG);
            throw new Exception(String.format("Failed Getting Column Names for Table %s", tableName));
        }
    }
    
    //##################################################################################################################
    // Multi Methods
    //##################################################################################################################
    private <T> ArrayList<ArrayList<T>> get_2D_ArrayList_Internally
    (Connection connection, String method_Name, String query, Object[] params, Class<T> typeCast, boolean allow_No_Results) throws Exception
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
            if (process_Params(params))
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
            
            if (! resultSet.isBeforeFirst()) // checks if any data was returned
            {
                if (allow_No_Results) { return collection; }
                throw new Exception("Failed Query -> Empty Results");
            }
            
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
                    Object obj = resultSet.getObject(col); // Get Object & Cast to Object
                    
                    if (typeCast == Object.class) // If TypeCast is Objects We're Expecting Multiple Types Then Cast
                    {
                        rowData.add(typeCast.cast(map_Object_To_Domain_Type(obj)));
                        continue;
                    }
                    
                    rowData.add(typeCast.cast(obj)); // Converts Object to provided type and casts it
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
    
    private Fetched_Results get_Fetched_Results_Internally(Connection connection, LinkedHashSet<Pair<String, Object[]>> fetch_query_and_params) throws Exception
    {
        //###############################################################
        // Variables
        //###############################################################
        Fetched_Results fetched_Results = new Fetched_Results();
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        String query = null;
        Object[] params = null;
        
        //##############################################################
        // Execute Upload Params
        //##############################################################
        try
        {
            for (Pair<String, Object[]> entry : fetch_query_and_params)  // Execute Get Statements
            {
                query = entry.getValue0();
                params = entry.getValue1();
                
                // Add Fetch Results To Object made for storing multiple queries
                fetched_Results.add_2D_Result(get_2D_Object_AL_Internally(connection, method_Name, query, params, false));
            }
            
            return fetched_Results;  // Return Output
        }
        catch (Exception e)
        {
            print_Internal_Method_Err_MSG(method_Name, query, params, e);
            throw e;
        }
    }
    
    //#######################################
    // 2D Objects
    //#######################################
    private ArrayList<ArrayList<Object>> get_2D_Object_AL_Internally(Connection connection, String method_Name, String query, Object[] params, boolean allow_No_Results) throws Exception
    {
        return get_2D_ArrayList_Internally(connection, method_Name, query, params, Object.class, allow_No_Results);
    }
    
    public ArrayList<ArrayList<Object>> get_2D_Query_AL_Object(String query, Object[] params, String errorMSG, boolean allow_No_Results) throws Exception
    {
        //#############################
        // Query Setup
        //#############################
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        try (Connection connection = dataSource.getConnection()) // Get a Connection from pool
        {
            return get_2D_Object_AL_Internally(connection, method_Name, query, params, allow_No_Results);
        }
        //#############################
        // Error Handling
        //#############################
        catch (Exception e) // Exception needs to be handled here the methods above just pass on Exception & Half print error msg
        {
            handleException_MYSQL(e, method_Name, query, errorMSG);
            throw new Exception(""); // Exception Already been handled but, notify external method calling this method
        }
    }
    
    //#######################################
    // Fetched Results
    //#######################################
    public Fetched_Results get_Fetched_Results(LinkedHashSet<Pair<String, Object[]>> fetch_query_and_params, String error_msg)
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        //##############################################################
        // Execute Upload Params
        //##############################################################
        try (Connection connection = dataSource.getConnection())
        {
            return get_Fetched_Results_Internally(connection, fetch_query_and_params);
        }
        catch (Exception e)
        {
            handleException_MYSQL(e, method_Name, null, error_msg);
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
            case Boolean b -> statement.setInt(pos, b ? 1 : 0); // Boolean are represented as Integers
            case BigDecimal bigDecimal -> // BigDecimal to Integer
            {
                /*long long_conversion = bigDecimal
                        .movePointRight(2)
                        .longValueExact();
                
                statement.setLong(pos, long_conversion);  */
                
                statement.setBigDecimal(pos, bigDecimal);
            }
            case Timestamp timestamp -> statement.setTimestamp(pos, timestamp);  // TimeStamp / LocalDateTime
            case LocalDateTime localDateTime -> // Local Date Time
                    statement.setTimestamp(pos, Timestamp.valueOf(localDateTime));
            case LocalTime localTime ->
                    statement.setString(pos, localTime.format(DateTimeFormatter.ofPattern("HH:mm"))); // LocalTime converted to Glob String
            case Float f -> statement.setFloat(pos, f);
            case Double d -> statement.setDouble(pos, d);
            
            // Exception clause
            default -> throw new Exception(String.format("Unable to configure param dataType of object being '%s' - %s"
                    , object.toString(), object.getClass().getSimpleName()));
        }
    }
    
    private Object map_Object_To_Domain_Type(Object db_Object)
    {
        switch (db_Object)
        {
            case null -> { return null; }
            case Double d -> { return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP); }
            case Object o -> { return o; }
        }
    }
    
    //###############################################################################
    // Validation Methods
    //###############################################################################
    public boolean get_DB_Connection_Status()
    {
        return db_Connection_Status;
    }
    
    private boolean process_Params(Object[] params) throws Exception
    {
        if (params == null) { return false; }
        
        if (params.length == 0) { throw new Exception("Params are Empty New Object[]{}"); }
        
        return true;
    }
    
    //##############################################################################
    // Error Handling Methods
    //##############################################################################
    /*
     * Will only work on DML Transactions:
     * INSERT
     * UPDATE
     * DELETE
     * REPLACE
     * LOAD DATA (when using InnoDB and not in autocommit)
     * SELECT … FOR UPDATE / LOCK IN SHARE MODE
     *
     * DDL Transaction this won't work on:
     * CREATE TABLE
     * DROP TABLE
     * ALTER TABLE
     * TRUNCATE TABLE
     * RENAME TABLE
     * CREATE INDEX
     * DROP INDEX
     */
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
        
        JOptionPane.showMessageDialog(null, errorMSG, "Alert Message: ", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void print_SQL_ERR_MSG(SQLException e, String method_Name, Object query)
    {
        System.err.printf("\n\n%s\n%s ->  @%s SQL ERROR \n%s \n\nQuery: \n\"\"\"\n %s\n\"\"\" \n\nError Message: \n\n\"\"\" \n\n%s \n\n\"\"\" \n\nSQLState: %s \n\nErrorCode: %d\n\n",
                line_Separator, get_Class_Name(), method_Name, line_Separator, query != null ? query.toString() : "", e.getMessage(), e.getSQLState(), e.getErrorCode());
    }
    
    private void print_Exception_ERR_MSG(Exception e, String method_Name, Object query)
    {
        System.err.printf("\n\n%s \n%s -> @%s ERROR \n%s \n\nQuery: \n\"\"\"\n%s\n\"\"\" \n\nError Message: \n\n\"\"\"\n  %s \n\"\"\" ",
                line_Separator, get_Class_Name(), method_Name, line_Separator, query != null ? query.toString() : "", e);
    }
    
    //###########################################
    // Method Catch Print Query + Params
    //###########################################
    private void print_Internal_Method_Err_MSG(String method_Name, String query, Object[] params, Exception e)
    {
        System.err.printf("""
                \n\n%s
                MyJDBC_MySQL.java %s Error
                %s
                \nQuery : \n\n\"\"\" \n\n%s \n\n\"\"\"
                \nParams: \n%s%n
                \nError MSG:  \n\n\"\"\" \n%s \n\"\"\"""", line_Separator, method_Name, line_Separator, query, Arrays.toString(params), e);
    }
    
    //###############################################
    // Debugging Print Statements
    //###############################################
    protected String get_Class_Name()
    {
        return class_Name;
    }
    
    protected String get_Method_Name()
    {
        return Thread.currentThread().getStackTrace()[1].getMethodName();
    }
    
    protected String get_Class_And_Method_Name()
    {
        return String.format("%s -> @%s", get_Class_Name(), get_Method_Name());
    }
}

package App_Code.Objects.Database_Objects.JDBC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Query_Results
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private HashMap<Integer, ArrayList<Object>> results_1D_Map = new HashMap<>();
    private HashMap<Integer, ArrayList<ArrayList<Object>>> results_2D_Map = new HashMap<>();
    
    private int size = 0;
    private String class_Name = "Query_Results ->";
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Query_Results()
    { }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void add_1D_Result(ArrayList<Object> results) throws Exception
    {
        check_Null(results);   // Check
        
        results_1D_Map.put(size, results);  // Add
        size++;
    }
    
    public void add_2D_Result(ArrayList<ArrayList<Object>> results) throws Exception
    {
        check_Null(results);   // Check
        
        results_2D_Map.put(size, results); // Add
        size++;
    }
    
    //################################
    // Validation Checks
    //################################
    private void check_Null(Collection<?> collection) throws Exception
    {
        String methodName = String.format("%s %s()", class_Name, new Object() { }.getClass().getEnclosingMethod().getName());
        
        if (collection == null) { throw new Exception(String.format("%s : Cannot Add a NULL Item", methodName)); }
    }
    
    private void check_Index_In_Bounds(int index, String method_Name) throws Exception
    {
        if (index > size - 1 || index < 0)
        {
            String errorMSG = (String.format("\n\n%s %s ERROR \nIndex Out of Bounds [%s] of %s",
                    class_Name, method_Name, index, size - 1));
            
            throw new Exception(errorMSG);
        }
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void clear_Results()
    {
        results_1D_Map.clear();
        results_2D_Map.clear();
    }
    
    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    
    public int no_Of_Fetched_Results()
    {
        return size;
    }
    
    public boolean is_Empty()
    {
        return no_Of_Fetched_Results() == 0;
    }
    
    public ArrayList<ArrayList<Object>> get_Fetched_Result_2D_AL(int index) throws Exception
    {
        // Check 2D results
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        check_Index_In_Bounds(index, method_Name);
        
        // Check 2D results
        if (results_2D_Map.containsKey(index)) { return results_2D_Map.get(index); }
        
        throw new Exception(String.format("%s : Error, retrieving Item with index  ; %s", method_Name, index));
    }
    
    public ArrayList<Object> get_Result_1D_AL(int index) throws Exception
    {
        // Pre-Checks
        String method_Name = String.format("%s %s()", class_Name, new Object() { }.getClass().getEnclosingMethod().getName());
        
        check_Index_In_Bounds(index, method_Name);
        
        // Check 1D & 2D results
        if (results_2D_Map.containsKey(index)) { return results_2D_Map.get(index).getFirst(); }
        
        else if (results_1D_Map.containsKey(index)) { return results_1D_Map.get(index); }
        
        throw new Exception(String.format("\n\n%s : Error, retrieving Item with index  ; %s", method_Name, index));
    }
    
    public Object get_1D_Result_Into_Object(int index) throws Exception
    {
        String method_Name = String.format("%s %s()", class_Name, new Object() { }.getClass().getEnclosingMethod().getName());
        try
        {
            return get_Result_1D_AL(index).getFirst();
        }
        catch (Exception e)
        {
            throw new Exception(String.format("%s -> %s", method_Name, e));
        }
    }
}

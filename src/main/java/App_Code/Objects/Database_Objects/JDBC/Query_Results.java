package App_Code.Objects.Database_Objects.JDBC;

import java.util.ArrayList;

public class Query_Results
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private ArrayList<ArrayList<ArrayList<Object>>> outputs = new ArrayList<>(); // Each row in the output is a query result
    
    private int size = 0;
    private String class_Name = "Query_Results ";
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Query_Results()
    { }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void add_Result(ArrayList<ArrayList<Object>> query_Results) throws Exception
    {
        String methodName = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        if (query_Results == null)
        {
            String errorMSG = (String.format("\n\n%s %s ERROR \nQuery Returned Null Results", class_Name, methodName));
            throw new Exception(errorMSG);
        }
        
        outputs.add(query_Results);
        size++;
    }
    
    public void remove_Fetched_Result(int index) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        check_Index_In_Bounds(index, method_Name);
        
        outputs.remove(index);
        size--;
    }
    
    public ArrayList<ArrayList<Object>> get_Fetched_Result(int index) throws Exception
    {
        String method_Name = String.format("%s()", new Object() { }.getClass().getEnclosingMethod().getName());
        
        check_Index_In_Bounds(index, method_Name);
        
        return outputs.get(index);
    }
    
    private void check_Index_In_Bounds(int index, String method_Name) throws Exception
    {
        if (index > size - 1 || index < 0)
        {
            String errorMSG = (String.format("\n\n%s %s ERROR \nIndex Out of Bounds [%s] of %s",
                    class_Name, method_Name, index, size-1));
            
            throw new Exception (errorMSG);
        }
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
       return no_Of_Fetched_Results() > 0;
    }
}

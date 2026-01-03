package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients;

import App_Code.Objects.Data_Objects.ID_Objects.Storable_Ingredient_IDS.Storable_IDS_Parent;
import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_MySQL;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Combo_Boxes.Field_JCombo_Storable_ID;
import App_Code.Objects.Gui_Objects.Text_Fields.Parent.Field_JTxtField_Parent;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Form;
import App_Code.Objects.Data_Objects.Field_Bindings.Ingredients_Form_Binding;
import org.javatuples.Pair;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;

public class Edit_Ingredients_Form extends Ingredients_Form
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    protected Integer ingredient_ID = null;
    protected ArrayList<Object> data_AL;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredients_Form(Container parentContainer, MyJDBC_MySQL db, Shared_Data_Registry sharedDataRegistry, String btn_Txt)
    {
        super(parentContainer, db, sharedDataRegistry, btn_Txt);
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    @Override
    public void clear_Ingredients_Form()
    {
        super.clear_Ingredients_Form();
        ingredient_ID = null;
        data_AL = null;
    }
    
    
    //##########################################
    // Validation methods
    //##########################################
    @Override
    protected boolean is_Ingredient_Name_In_DB() throws Exception
    {
        //##################################
        // IS Ingredient Name Null or Empty
        //####################################
        String ingredient_Name = ((Field_JTxtField_Parent<?>) field_Items_Map.get("name").get_Gui_Component()).get_Text();
        
        if (ingredient_Name == null || ingredient_Name.isEmpty()) { throw new Exception("No ingredient Created!"); }
        
        //##################################
        // Create Query
        //####################################
        String errorMSG = "Error, Failed Validating Ingredient Name in DB!";
        String query = "SELECT ingredient_id FROM ingredients_info WHERE Ingredient_Name = ? AND ingredient_id <> ?;";
        
        Object[] params = new Object[]{ ingredient_Name, ingredient_ID };
        
        //##################################
        // Execute
        //####################################
        return ! db.get_Single_Col_Query_Int(query, params, errorMSG, true).isEmpty();
    }
    
    private boolean has_Field_Value_Changed_From_DB_Data(String key) throws Exception
    {
        // Get Desired Keys Object on the Form Value
        Object field_Value_On_Form = get_Component_Field_Value(key);
        
        // Get Desired Keys Equivalent Value in DATA
        int query_Pos = field_Items_Map.get(key).get_Field_Query_Pos(); // Query Position for field Value
        Object field_Value_In_DB_Data = data_AL.get(query_Pos);
        
        //########################
        // Exit Clauses
        //########################
        if (field_Value_In_DB_Data == null) { return false; } // Form Value cant be null due to validation but, db can
        
        if (field_Value_On_Form instanceof Storable_IDS_Parent storable_ID_Obj)
        {
            // case BigInteger x -> { return ! (storable_ID_Obj.get_ID()).equals(x.intValueExact()); }
            if (field_Value_In_DB_Data instanceof Integer x)
            {
                return ! x.equals(storable_ID_Obj.get_ID());
            }
            throw new Exception(String.format("\n\n%s Error ID Object for Key '%s' \nUnexpected value: %s \nClass Type : %s", get_Class_And_Method_Name(), key,
                    field_Value_In_DB_Data, field_Value_In_DB_Data.getClass()));
        }
        
        if (field_Value_On_Form.getClass() != field_Value_In_DB_Data.getClass()) // Type MisMatch
        {
            throw new Exception(String.format("""
                            \n\n %s Error Different Object Types
                            
                            \nForm
                            Class : %s
                            Value : %s
                            
                            \nDB
                            Class : %s
                            Value : %s""",
                    get_Class_And_Method_Name(),
                    field_Value_On_Form.getClass(), field_Value_On_Form.toString(),
                    field_Value_In_DB_Data.getClass(), field_Value_In_DB_Data.toString()
            ));
        }
        
        //########################
        // Compare
        //########################
        switch (field_Value_On_Form)
        {
            case BigDecimal form_Value -> { return form_Value.compareTo(((BigDecimal) field_Value_In_DB_Data)) != 0; }
            case BigInteger form_Value -> { return ! form_Value.equals(((BigInteger) field_Value_In_DB_Data)); }
            case Integer form_Value -> { return ! form_Value.equals(((Integer) field_Value_In_DB_Data)); }
            case String form_Value -> { return ! form_Value.equals(((String) field_Value_In_DB_Data)); }
            default -> throw new IllegalStateException(String.format("\n\n%s Error \nUnexpected value: %s ",
                    get_Class_And_Method_Name(), field_Value_On_Form));
        }
    }
    
    protected boolean has_Ingredient_Type_Changed() throws Exception
    {
        return has_Field_Value_Changed_From_DB_Data("type");
    }
    
    protected boolean has_Ingredient_Name_Changed() throws Exception
    {
        return has_Field_Value_Changed_From_DB_Data("name");
    }
    
    //##########################################
    // Update methods
    //##########################################
    public void set_Data(ArrayList<Object> data_AL) throws Exception
    {
        // Clear Data
        clear_Ingredients_Form();
        
        // Set Ingredient ID & remove from Arraylist
        ingredient_ID = (Integer) data_AL.getFirst();
        this.data_AL = data_AL;
        
        // Set Data for each component
        for (Ingredients_Form_Binding<?> field_Binding : field_Items_Map.values())
        {
            int pos_In_Query = field_Binding.get_Field_Query_Pos(); // Position of data associated with binding in query
            Object data = data_AL.get(pos_In_Query); // Get data from source
            Component component = field_Binding.get_Gui_Component(); // Get Component from binding
            
            // Set Data depending on component
            switch (component)
            {
                case Field_JCombo_Storable_ID<?> jComboBox -> jComboBox.set_Item_By_ID((int) data);
                case Field_JTxtField_Parent<?> jTxtField -> jTxtField.setText(data.toString());
                default ->
                        throw new Exception(String.format("\n\n%s Unexpected value: %s ", get_Class_And_Method_Name(), component));
            }
        }
        
        set_Salt_JC_To_Grams(); // Set Salt to Grams
    }
    
    @Override
    public void add_Update_Queries(LinkedHashSet<Pair<String, Object[]>> queries_And_Params) throws Exception
    {
        //##########################
        // Variables
        //##########################
        int size = field_Items_Map.size();
        
        StringBuilder
                insert_Header = new StringBuilder("UPDATE ingredients_info SET "),
                values = new StringBuilder();
        
        Object[] params = new Object[size + 1]; // Include where condition param
        
        //##########################
        // Create Update Query
        //##########################
        int pos = 0;
        for (Ingredients_Form_Binding<?> field_Binding : field_Items_Map.values())
        {
            //######################################
            // Variables
            //######################################
            Component component = field_Binding.get_Gui_Component(); // Get Component from binding
            
            //######################################
            // Compare Data - Skip if They're Equal
            //######################################
            int pos_In_Query = field_Binding.get_Field_Query_Pos(); // Position of data associated with binding in query
            Object stored_Data = data_AL.get(pos_In_Query); // Get data from source
            
            switch (component)
            {
                case Field_JCombo_Storable_ID<?> jc ->
                {
                    if (jc.does_Selected_Item_ID_Equal((int) stored_Data)) { continue; }
                }
                case Field_JTxtField_Parent<?> jt -> { if (jt.is_Field_Data_Equal_To(stored_Data)) { continue; } }
                default ->
                        throw new Exception(String.format("\n\n%s Unexpected value:  %s", get_Class_And_Method_Name(), field_Binding.get_Gui_Component()));
            }
            
            //######################################
            // Create Update Data
            //#####################################
            // Add to Values
            values.append(String.format("\n%s = ?,", field_Binding.get_Mysql_Field_Name()));
            
            // Add to Params
            switch (field_Binding.get_Gui_Component())
            {
                case Field_JCombo_Storable_ID<?> jc -> params[pos] = jc.get_Selected_Item_ID();
                case Field_JTxtField_Parent<?> jt -> params[pos] = jt.get_Text_Casted_To_Type();
                default ->
                        throw new Exception(String.format("\n\n%s Unexpected value:  %s", get_Class_And_Method_Name(), field_Binding.get_Gui_Component()));
            }
            
            pos++; // Increase pos
        }
        
        //##########################
        // Format Update Data
        //##########################
        if (values.isEmpty()) { return; } // If Nothing Changed Exit
        
        // Edit Ending of Query
        values.deleteCharAt(values.length() - 1); // delete last char ','
        values.append("\nWHERE ingredient_id = ?;");
        params[pos] = ingredient_ID;
        
        // Remove null values from values not changed
        params = Arrays.stream(params)
                .filter(Objects :: nonNull)
                .toArray(Object[] :: new);
        
        System.out.printf("\n\nInsert Headers: \n%s \n\nValues: \n%s  \n\nParams: \n%s%n", insert_Header, values, Arrays.toString(params));
        
        //##########################
        // Add To Results
        //##########################
        StringBuilder update_Query = insert_Header.append(values);
        queries_And_Params.add(new Pair<>(update_Query.toString(), params));
    }
}

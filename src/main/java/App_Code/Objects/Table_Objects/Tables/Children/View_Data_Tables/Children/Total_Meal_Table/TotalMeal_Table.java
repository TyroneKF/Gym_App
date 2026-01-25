package App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Children.Total_Meal_Table;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Table_Objects.Tables.Children.View_Data_Tables.Parent.MyJTable_Display_Data;
import App_Code.Objects.Table_Objects.MealManager;
import java.util.ArrayList;
import java.util.Collections;

public class TotalMeal_Table extends MyJTable_Display_Data
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private MealManager mealManager;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public TotalMeal_Table
    (
            MyJDBC_Sqlite db,
            MealManager mealManager,
            Shared_Data_Registry shared_data_registry,
            ArrayList<Object> data
    )
    {
        //##########################################
        // Super
        //##########################################
        super(
                db,
                shared_data_registry,
                mealManager.get_Collapsible_JP_Obj().get_South_JPanel(),
                new ArrayList<>(Collections.singletonList(data)),
                shared_data_registry.get_Total_Meal_Table_Column_Names(),
                "draft_meal_in_plan_id",
                "Total Meal Table",
                "draft_meals_in_plan",
                "draft_gui_total_meal_view",
                shared_data_registry.get_Total_Meal_Table_Column_Names(),
                null,
                shared_data_registry.get_Total_Meal_Table_Cols_To_Hide()
        );
        
        //###########################################
        // Variables
        //###########################################
        this.mealManager = mealManager;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void set_Value_On_Table(Object data, int row, int col)
    {
        super.update_Table_Cell_Value(data, row, col);
    }
    
    public ArrayList<Object> update_Table_And_Get_Data() throws Exception
    {
        ArrayList<Object> data = get_Table_Update_Data();
        
        super.update_Table_Row(data, update_Row);
        
        return data;
    }
    
    @Override
    protected Object[] get_Params()
    {
        return new Object[]{ mealManager.get_Draft_Meal_ID() };
    }
}


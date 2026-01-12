package App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Children;

import App_Code.Objects.Database_Objects.MyJDBC.MyJDBC_Sqlite;
import App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Parent.MyJTable_DisplayData;
import App_Code.Objects.Tables.MealManager;

import java.util.ArrayList;
import java.util.Collections;

public class TotalMeal_Table extends MyJTable_DisplayData
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final int meal_In_Plan_ID;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public TotalMeal_Table
    (
            MyJDBC_Sqlite db,
            MealManager mealManager,
            int meal_In_Plan_ID,
            ArrayList<Object> data
    )
    {
        //##########################################
        // Super
        //##########################################
        super(
                db,
                mealManager.get_Collapsible_JP_Obj().get_South_JPanel(),
                new ArrayList<>(Collections.singletonList(data)),
                mealManager.get_Total_Meal_Table_Column_Names(),
                "draft_meal_in_plan_id",
                "Total Meal Table",
                "draft_meals_in_plan",
                "draft_gui_total_meal_view",
                mealManager.get_Total_Meal_Table_Column_Names(),
                null,
                mealManager.get_TotalMeal_Table_Cols_To_Hide()
        );
        
        //##########################################
        // Variables
        //###########################################
        this.meal_In_Plan_ID = meal_In_Plan_ID;
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public Object get_Value_On_Model_Data(int row, int col)
    {
        return super.get_Value_On_Model_Data(row, col);
    }
    
    public void set_Value_On_Table(Object data, int row, int col)
    {
        super.update_Table_Cell_Value(data, row, col);
    }
    
    @Override
    protected Object[] get_Params()
    {
        return new Object[]{ meal_In_Plan_ID };
    }
}


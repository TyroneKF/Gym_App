package App_Code.Objects.Tables.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Tables.MealManager;

import java.util.ArrayList;

public class TotalMeal_Table extends MyJTable_DisplayData
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private Integer meal_In_Plan_ID;
    private MealManager mealManager;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public TotalMeal_Table(MyJDBC db, MealManager mealManager, int meal_In_Plan_ID, ArrayList<ArrayList<Object>> data)
    
    {
        //##########################################
        // Super
        //##########################################
        super(db,
                mealManager.get_Collapsible_JP_Obj().get_South_JPanel(),
                data,
                mealManager.get_Total_Meal_Table_Column_Names(),
                mealManager.get_Plan_ID(),
                mealManager.get_Temp_PlanID(),
                "total_meal_view",
                mealManager.get_TotalMeal_Table_UnEditable_Cells(),
                null,
                mealManager.get_TotalMeal_Table_Cols_To_Hide()
        );
        
        //##########################################
        // Variables
        //###########################################
        this.meal_In_Plan_ID = meal_In_Plan_ID;
        this.mealManager = mealManager;
        
        super.query = String.format("SELECT * FROM %s WHERE meal_in_plan_id = ? AND plan_id = ?;", tableName);
        super.params = new Object[]{ meal_In_Plan_ID, temp_PlanID };
    }
    
   /* @Override
    protected  void set_Variables()
    {
        *//*  String tableName, ArrayList<ArrayList<Object>> savedData, ArrayList<String> columnNames,
            ArrayList<String> unEditableColumnNames, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide*//*
          
    }
    */
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public void update_TotalMeal_Table()
    {
        super.update_Table();
    }
    
    public Object get_Value_On_Table(int row, int col)
    {
        return super.getValueOnTable(row, col);
    }
    
    public void set_Value_On_Table(Object data, int row, int col)
    {
        super.update_Table_Cell_Value(data, row, col);
    }
}


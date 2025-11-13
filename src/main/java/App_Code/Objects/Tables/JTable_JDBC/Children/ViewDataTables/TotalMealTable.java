package App_Code.Objects.Tables.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer mealInPlanID;
    private String mealName;
    
    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj, ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames, int planID, int temp_PlanID,
                          Integer MealInPlanID, String mealName, String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                          ArrayList<String> columnsToHide)
    {
        super(db, collapsibleObj.get_Centre_JPanel(), data, columnNames, planID, temp_PlanID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        super.query = String.format("SELECT * FROM %s WHERE meal_in_plan_id = ? AND plan_id = ?;", tableName);
        super.params = new Object[]{ MealInPlanID, temp_PlanID };
        
        this.mealInPlanID = MealInPlanID;
        this.mealName = mealName;
    }
    
    public void updateTotalMealTable()
    {
        super.updateTable();
    }
    
    public Object get_ValueOnTable(int row, int col)
    {
        return super.getValueOnTable(row, col);
    }
    
    public void set_Value_On_Table(Object data, int row, int col)
    {
        super.update_Table_Cell_Value(data, row, col);
    }
}


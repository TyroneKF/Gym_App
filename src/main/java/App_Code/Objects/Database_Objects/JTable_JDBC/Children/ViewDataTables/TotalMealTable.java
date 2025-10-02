package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MealManager;
import App_Code.Objects.Database_Objects.MealManagerRegistry;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer mealInPlanID;
    private String mealName;
    private MealManagerRegistry mealManagerRegistry;
    private MealManager mealManager;
    

    public TotalMealTable(MyJDBC db, MealManager mealManager, CollapsibleJPanel collapsibleObj, ArrayList<ArrayList<Object>> data, ArrayList<String> columnNames, int planID, int temp_PlanID,
                          Integer MealInPlanID, String mealName, String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                          ArrayList<String> columnsToHide)
    {

        super(db, collapsibleObj.getCentreJPanel(), data, columnNames, planID, temp_PlanID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        super.query = String.format("SELECT * FROM %s WHERE meal_in_plan_id = %s AND plan_id = %s;", tableName, MealInPlanID, temp_PlanID);

        this.mealInPlanID = MealInPlanID;
        this.mealName = mealName;
        
        this.mealManager = mealManager;
        this.mealManagerRegistry = mealManager.getMealManagerRegistry();
    }

    public void updateTotalMealTable()
    {
        super.updateTable();
        
        // Replace data in Collections
        //mealManagerRegistry.replaceMealManagerDATA(mealManager);
    }
    
    public BigDecimal get_ValueOnTable(int row, int col )
    {
        return (BigDecimal) super.getValueOnTable(row, col);
    }
    
    public ArrayList<Object> getTableData()
    {
        return getCurrentData().get(0);
    }
}


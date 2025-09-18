package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables;
import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;


public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer MealInPlanID;
    private String mealName;

    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj, Object[][] data, String[] columnNames, int planID, int temp_PlanID,
                          Integer MealInPlanID, String mealName, String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering,
                          ArrayList<String> columnsToHide)
    {

        super(db, collapsibleObj.getCentreJPanel(), data, columnNames, planID, temp_PlanID, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        super.query = String.format("SELECT * FROM %s WHERE meal_in_plan_id = %s AND plan_id = %s;", tableName, MealInPlanID, temp_PlanID);

        this.MealInPlanID = MealInPlanID;
        this.mealName = mealName;
    }

    public void updateTotalMealTable()
    {
        super.updateTable();
    }

    public boolean updateTotalMealTableModelData()
    {
        return super.updateTableModelData();
    }

    public BigDecimal getValueOnTable(String column_name)
    {
        column_name = column_name.toLowerCase(Locale.ROOT);

        if(columnNamesAndPositions.containsKey(column_name)) System.out.printf("\n\nYes %s is in the list", column_name);

        return  columnNamesAndPositions.containsKey(column_name)   ?
                (BigDecimal) getTable().getValueAt(0,columnNamesAndPositions.get(column_name)[1])  // Index of Column after columns are hidden
                : null;
    }
}


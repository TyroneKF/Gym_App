package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.TotalMealTable.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.TotalMealTable.TotalMealTable;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import java.util.ArrayList;

public class DividedMealsTable extends TotalMealTable
{
    Integer divMealSectionsID;

    private DividedMealsTable(MyJDBC db, CollapsibleJPanel collapsibleObj, String databaseName, Object[][] data, String[] columnNames, int planID, Integer MealInPlanID, String mealName, String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide)
    {
        super(db, collapsibleObj, databaseName, data, columnNames, planID, MealInPlanID, mealName, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
    }

    public DividedMealsTable(MyJDBC db, CollapsibleJPanel collapsibleObj, String databaseName, Object[][] data, String[] columnNames, int planID, Integer MealInPlanID, Integer divMealSectionsID, String mealName, String tableName, ArrayList<String> unEditableColumns, ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide)
    {
        super(db, collapsibleObj, databaseName, data, columnNames, planID, MealInPlanID, mealName, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        this.divMealSectionsID = divMealSectionsID;
    }

    @Override
    public void updateTotalMealTable()
    {
        String query = String.format("SELECT *  FROM divided_meal_sections_calculations WHERE DivMealSectionsID = %s AND PlanID = %s;", divMealSectionsID, super.temp_PlanID);
        super.updateTable("DividedMealsTable", query, 1);
    }

    @Override
    public boolean updateTotalMealTableModelData()
    {
        String query = String.format("SELECT * FROM divided_meal_sections_calculations WHERE DivMealSectionsID = %s AND PlanID = %s;", divMealSectionsID, super.temp_PlanID);
        return super.updateTableModelData("divided_meal_sections_calculations", query);
    }

}

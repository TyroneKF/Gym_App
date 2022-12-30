package App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.Total_Tables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JTable_JDBC.Children.ViewDataTables.Children.Total_Tables.Parent_Totals_Table;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import java.util.ArrayList;

public class DividedMealsTable extends Parent_Totals_Table
{
    Integer divMealSectionsID;
    private String databaseTableName = "total_meal_view", tableName = "TotalMealTable";

    public DividedMealsTable(MyJDBC db, CollapsibleJPanel collapsibleObj,  Object[][] data, String[] columnNames, int planID, int temp_PlanID,
                             Integer MealInPlanID, Integer divMealSectionsID, String mealName, String tableName, ArrayList<String> unEditableColumns,
                             ArrayList<String> colAvoidCentering, ArrayList<String> columnsToHide)
    {
        super(db, collapsibleObj,  data, columnNames, planID, temp_PlanID, MealInPlanID, mealName, tableName, unEditableColumns, colAvoidCentering, columnsToHide);
        this.divMealSectionsID = divMealSectionsID;
    }

    public void updateTotalMealTable()
    {
        String query = String.format("SELECT *  FROM divided_meal_sections_calculations WHERE DivMealSectionsID = %s AND PlanID = %s;",
                divMealSectionsID, super.temp_PlanID);

        super.updateTable("DividedMealsTable", query, 1);
    }


    public boolean updateTotalMealTableModelData()
    {
        String query = String.format("SELECT * FROM divided_meal_sections_calculations WHERE DivMealSectionsID = %s AND PlanID = %s;",
                divMealSectionsID, super.temp_PlanID);

        return super.updateTableModelData("divided_meal_sections_calculations", query);
    }

}

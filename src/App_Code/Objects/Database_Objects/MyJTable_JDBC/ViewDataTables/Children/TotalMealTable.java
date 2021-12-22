package App_Code.Objects.Database_Objects.MyJTable_JDBC.ViewDataTables.Children;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.MyJTable_JDBC.ViewDataTables.Parent.MyJTable_DisplayData;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TotalMealTable extends MyJTable_DisplayData
{
    private Integer planID, temp_PlanID = 1, mealID, tempPlan_Meal_ID;
    private String mealName;
    private CollapsibleJPanel collapsibleObj;


    public TotalMealTable(MyJDBC db, CollapsibleJPanel collapsibleObj, String databaseName, Object[][] data, String[] columnNames, int planID,
                          Integer mealID, Integer tempPlan_Meal_ID, String mealName, String tableName,
                          ArrayList<Integer> unEditableColumns, ArrayList<Integer> colAvoidCentering, boolean setIconsUp)
    {

        super(db, collapsibleObj.getCentreJPanel(), data, columnNames, planID, tableName, unEditableColumns, colAvoidCentering);

        this.databaseName = databaseName;
        this.mealID = mealID;
        this.mealName = mealName;
        this.planID = planID;
        this.tempPlan_Meal_ID = tempPlan_Meal_ID;
        this.setIconsUp = setIconsUp;
        this.collapsibleObj = collapsibleObj;
    }

    private void update_TotalMeal_Table()
    {
        //##########################################################################
        //   Updating Total  Meal Table
        ///##########################################################################

        String totalMealTableQuery = String.format("SELECT  * FROM total_meal_view   WHERE  MealID = %s; ", tempPlan_Meal_ID);

        ArrayList<ArrayList<Object>> totalMealData = db.get_Multi_ColumnQuery_Object(totalMealTableQuery);

        if (totalMealData == null)
        {
            JOptionPane.showMessageDialog(null, "ERROR: \nUn-able to Update Totals Table!");

            return;
        }
        else
        {
            ArrayList<Object> totalMeal_UpdateData = totalMealData.get(0);
            super.updateTable(this, totalMeal_UpdateData, 0);
        }
    }
}


package com.donty.gymapp.ui.tables.viewData;

import com.donty.gymapp.domain.enums.db_enums.views.totalmeal.Draft_Gui_Total_Meal_Columns;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.ui.tables.viewData.base.MyJTable_Display_Data;
import com.donty.gymapp.ui.components.meal.MealManager;
import java.util.ArrayList;
import java.util.Collections;

public class TotalMeal_Table extends MyJTable_Display_Data<Draft_Gui_Total_Meal_Columns>
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final MealManager mealManager;

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

                shared_data_registry.get_Total_Meal_Table_Meta(),
                shared_data_registry.get_Total_Meal_Column_UI_Rules()
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


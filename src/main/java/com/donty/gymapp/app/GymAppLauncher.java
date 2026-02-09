package com.donty.gymapp.app;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.ui.screens.mealPlan.Meal_Plan_Screen;

import javax.swing.JOptionPane;

public final class GymAppLauncher
{

    private GymAppLauncher()
    {
        // prevent instantiation
    }

    public static void main(String[] args)
    {
        launch();
    }

    public static void launch()
    {
        MyJDBC_Sqlite db = new MyJDBC_Sqlite();

        try
        {
            db.begin_migration();
            if (! db.get_DB_Connection_Status())
            {
                throw new IllegalStateException("Failed Initialization!");
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "ERROR, Cannot Connect To Database!");
            return;
        }

        new Meal_Plan_Screen(db);
    }
}


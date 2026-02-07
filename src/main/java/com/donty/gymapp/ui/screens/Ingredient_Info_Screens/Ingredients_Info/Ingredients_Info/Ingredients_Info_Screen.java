
package com.donty.gymapp.ui.screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info;

import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.gui.base.Screen_JFrame;
import com.donty.gymapp.ui.screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;
import com.donty.gymapp.ui.screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Edit_Ingredients_Screen;
import com.donty.gymapp.ui.screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Ingredient_Types.Ingredients_Types_Screen;
import com.donty.gymapp.ui.screens.Ingredient_Info_Screens.Stores_And_Ingredient_Types.Stores.Ingredient_Stores_Screen;
import com.donty.gymapp.ui.screens.mealPlanScreen.Meal_Plan_Screen;
import javax.swing.*;
import java.awt.*;

public class Ingredients_Info_Screen extends Screen_JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    // Objects
    private Meal_Plan_Screen meal_Plan_Screen;
    
    // Screen Objects
    private Ingredients_Screen ingredients_Screen;
    private Edit_Ingredients_Screen edit_Ingredients_Screen;
    private Frame main_frame;
    
    // Booleans
    private boolean updateIngredientInfo = false;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Ingredients_Info_Screen(MyJDBC_Sqlite db, Meal_Plan_Screen meal_Plan_Screen, Shared_Data_Registry shared_Data_Registry)
    {
        //########################################################
        // Super Constructor
        //########################################################
        super(db, false, "Add/Edit Ingredients Screen", 800, 880, 0, 0);
        
        //########################################################
        // Variables
        //########################################################
        // Objects
        this.meal_Plan_Screen = meal_Plan_Screen;
        this.main_frame = meal_Plan_Screen.getFrame();
        
        //########################################################
        // Frame Set-Up
        //########################################################
        set_Resizable(false);
        setFrameVisibility(true); // Make Frame Visible
        
        // Create ContentPane
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
       
        // Creating TabbedPane
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
      
        // Creating Add Ingredients Screen
        ingredients_Screen = new Ingredients_Screen(this, db, shared_Data_Registry);
        tp.add("Add Ingredients", ingredients_Screen);
        
        // Creating Edit Ingredients Screen
        edit_Ingredients_Screen = new Edit_Ingredients_Screen(this, db, shared_Data_Registry);
        tp.add("Edit Ingredients", edit_Ingredients_Screen);
        
        // Creating Edit Ingredient Types Screen
        Ingredients_Types_Screen ingredient_Types_Screen = new Ingredients_Types_Screen(db, shared_Data_Registry, this, shared_Data_Registry.get_All_Ingredient_Types_AL());
        tp.add("Edit Ingredient Types", ingredient_Types_Screen);
        
        // Creating Edit Ingredients Stores Screen
        Ingredient_Stores_Screen stores_Screen = new Ingredient_Stores_Screen(db, shared_Data_Registry, this, shared_Data_Registry.get_Stores_AL());
        tp.add("Edit Ingredient Stores", stores_Screen);
        
        // Resize GUI
        resizeGUI();
    }
    
    @Override
    public void window_Closed_Event()
    {
        meal_Plan_Screen.remove_Ingredients_Info_Screen();
        meal_Plan_Screen.update_Ingredients_Name_And_Types_In_JTables(updateIngredientInfo);
        closeJFrame();
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
    // Reset Methods
    public void reset_Add_Screen()
    {
    
    }
    
    public void reset_Edit_Screen()
    {
    
    }
    
    //#############################
    // Updates
    //#############################
    // Update Methods
    public void update_Stores_JC()
    {
        ingredients_Screen.reload_Stores_JC();
        edit_Ingredients_Screen.reload_Stores_JC();
    }
    
    //############
    // Type JC
    //############
    public void update_All_Types_JC()
    {
        update_Add_Types();
        update_Edit_Types();
    }
    
    public void update_Add_Types()
    {
        ingredients_Screen.reload_Ingredient_Type_JC();
    }
    
    public void update_Edit_Types()
    {
        edit_Ingredients_Screen.reload_Ingredient_Type_JC();
    }

    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Update_IngredientInfo(boolean status)
    {
        updateIngredientInfo = status;
    }
}


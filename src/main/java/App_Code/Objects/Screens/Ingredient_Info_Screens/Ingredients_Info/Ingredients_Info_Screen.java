
package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.Shared_Data_Registry;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredients_Screen;

import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Edit_Ingredients_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.*;


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
    public Ingredients_Info_Screen(MyJDBC db, Meal_Plan_Screen meal_Plan_Screen, Shared_Data_Registry shared_Data_Registry)
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
        
        //####################################################
        // Create ContentPane
        //####################################################
        getScrollPaneJPanel().setLayout(new GridLayout(1, 1));
        
        //##################################################
        // Creating TabbedPane
        //##################################################
        JTabbedPane tp = new JTabbedPane();
        getScrollPaneJPanel().add(tp);
        
        //##################################################
        // Creating Add Ingredients Screen
        //##################################################
        ingredients_Screen = new Ingredients_Screen(this, db, shared_Data_Registry);
        tp.add("Add Ingredients", ingredients_Screen);
        
        
        //##################################################
        // Creating Edit Ingredients Screen
        //##################################################
        edit_Ingredients_Screen = new Edit_Ingredients_Screen(this, db, shared_Data_Registry);
        tp.add("Edit Ingredients", edit_Ingredients_Screen);

         /*
        //#################################################
        // Creating Edit Ingredient Types Screen
        //##################################################
        Ingredients_Types_Screen edit_IngredientTypes = new Ingredients_Types_Screen(db, shared_Data_Registry, this, ingredientsTypesList);
        tp.add("Edit Ingredient Types", edit_IngredientTypes);
        
        //#################################################
        // Creating Edit Ingredients Stores Screen
        //##################################################
        Ingredient_Stores_Screen edit_Stores_Screen = new Ingredient_Stores_Screen(db, shared_Data_Registry, this, storesNamesList);
        tp.add("Edit Ingredient Stores", edit_Stores_Screen);
    */
        // ################################################################
        // Make Frame Visible
        // ################################################################
        setFrameVisibility(true);
        resizeGUI();
    }
    
    @Override
    public void window_Closed_Event()
    {
        meal_Plan_Screen.remove_Ingredients_Info_Screen();
        meal_Plan_Screen.updateIngredientsNameAndTypesInJTables(updateIngredientInfo);
        closeJFrame();
    }
    
    //##################################################################################################################
    // Update Methods
    //##################################################################################################################
    public void update_Types_JC()
    {
        ingredients_Screen.reload_Ingredient_Type_JC();
        edit_Ingredients_Screen.reload_Ingredient_Type_JC();
    }
    
    public void update_Stores_JC()
    {
       ingredients_Screen.reload_Stores_JC();
       edit_Ingredients_Screen.reload_Stores_JC();
    }
    
    //##################################################################################################################
    // Mutator Methods
    //##################################################################################################################
    public void set_Update_IngredientInfo(boolean status)
    {
        updateIngredientInfo = status;
    }
}


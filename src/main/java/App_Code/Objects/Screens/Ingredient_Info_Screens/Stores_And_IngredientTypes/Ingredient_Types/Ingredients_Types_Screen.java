package App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Ingredient_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Edit_Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Add_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Edit_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Stores_And_IngredientTypes.Parent_Screen;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class Ingredients_Types_Screen extends Parent_Screen
{
    
    public Ingredients_Types_Screen(MyJDBC db, Ingredients_Info_Screen ingredient_Info_Screen, Collection<String> jComboBox_List)
    {
        //####################################
        // Super Constructor
        //####################################
        super(db, ingredient_Info_Screen,
                "ingredients types",
                "Add Ingredients Type",
                "Edit Ingredients Type",
                jComboBox_List,
                "src/main/java/Documentation_And_Scripts/Database/Scripts/Editable_DB_Scripts/5.) IngredientTypes.sql");
    }
    
    @Override
    protected void set_Screen_Variables()
    {
        add_Screen = new Add_Ingredient_Type(db,this, collapsible_BTN_Txt1, 250, 50);
        edit_Screen = new Edit_Ingredient_Type(db,this, collapsible_BTN_Txt2, 250, 50);
    }

    public class Add_Ingredient_Type extends Add_Screen
    {
        public Add_Ingredient_Type(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
        {
            super(db, parent_Screen, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void  set_Screen_Variables()
        {
            super.main_Label = "Add Ingredient Type Name";
    
            super.data_Gathering_Name = "Ingredient Type Name";
            super.db_ColumnName_Field = "ingredient_type_name";
            super.db_TableName = "ingredient_types";
        }

        @Override
        protected void success_Upload_Message()
        {
            String text = String.format("\n\nSuccessfully Added New Ingredient Type: '%s'", jTextField_TXT);
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void failure_Message()
        {
            String text = "\n\nFailed Upload - Couldn't Add New Ingredient Type";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void update_Other_Screens()
        {
            ingredient_Info_Screen.add_Change_Or_Remove_IngredientsTypeName("addKey", jTextField_TXT, null);
            ingredient_Info_Screen.update_IngredientsForm_Type_JComboBoxes();
        }
    }

    public class Edit_Ingredient_Type extends Edit_Screen
    {
        public Edit_Ingredient_Type(MyJDBC db, Parent_Screen parent_Screen, String btnText, int btnWidth, int btnHeight)
        {
            super(db, parent_Screen, btnText, btnWidth, btnHeight);
        }
    
        protected void set_Screen_Variables()
        {
            super.lable1 = "Select Ingredient Type Name To Edit";
            super.label2 = "Change Ingredient Type Name";
    
            super.data_Gathering_Name = "Ingredient Type Name";
            super.db_ColumnName_Field = "ingredient_type_name";
            super.db_TableName = "ingredientTypes";
    
            super.id_ColumnName = "ingredient_type_id";
            super.fk_Table = "ingredients_info";
            super.remove_JComboBox_Items = new String[]{"None Of The Above", "UnAssigned"};
        }

        @Override
        protected void success_Upload_Message()
        {
            String text = String.format("\n\nSuccessfully Changed Ingredient Type From ' %s ' to ' %s ' !", selected_JComboBox_Item_Txt, jTextField_TXT);
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void failure_Message()
        {
            String text = "\n\nFailed Upload - Couldn't Add New Ingredient Type";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void update_Other_Screens()
        {
            System.out.printf("\n\n##############################################");
            System.out.printf("\nupdateOtherScreens()  \nnewKey: %s \noldKey: %s", jTextField_TXT, selected_JComboBox_Item_Txt); // HELLO REMOVE

            //#################################################################
            // Reset EditCreateForm
            //#################################################################
            Edit_Ingredients_Screen editingIngredientsInfo = ingredient_Info_Screen.get_Edit_Ingredients_Form();
            editingIngredientsInfo.refresh_Interface(true, true);  // reset form`

            //###################################################################
            // Change IngredientsTypeToNames JComboBox & List in  EditCreateForm
            //###################################################################
            if (item_Deleted)
            {
                if (ingredient_Info_Screen.add_Change_Or_Remove_IngredientsTypeName("removeKey", null, selected_JComboBox_Item_Txt)) // change key
                {
                    editingIngredientsInfo.update_IngredientNames_To_Types_JComboBox(); // update IngredientsTypeToNames JComboBox
                }
                else
                {
                    System.out.print("\n\nupdateOtherScreens() error deletingKey "); // HELLO REMOVE
                    return;
                }
            }
            else
            {
                if (ingredient_Info_Screen.add_Change_Or_Remove_IngredientsTypeName("changeKeyName", jTextField_TXT, selected_JComboBox_Item_Txt)) // change key
                {
                    editingIngredientsInfo.update_IngredientNames_To_Types_JComboBox(); // update IngredientsTypeToNames JComboBox
                }
                else
                {
                    System.out.println("\n\n################################# \nupdateOtherScreens() error changingKey \n#################################"); // HELLO REMOVE
                    return;
                }
            }

            //#################################################################
            // Update CreateForm ingredientsForm  Type JComboBoxes
            //##################################################################
            ingredient_Info_Screen.update_IngredientsForm_Type_JComboBoxes();
            ingredient_Info_Screen.set_Update_IngredientInfo(true);
        }

        @Override
        protected ArrayList<String> delete_Btn_Queries(String mysqlVariableReference1, ArrayList<String> queries)
        {
            //#############################################
            //
            //#############################################
            String changeToValue = String.format("(SELECT %s FROM %s WHERE %s = 'UnAssigned')", id_ColumnName, db_TableName, db_ColumnName_Field);

            String query1 = String.format("""                  
                    UPDATE %s
                    SET %s =  %s
                    WHERE %s = %s;""", fk_Table, id_ColumnName, changeToValue, id_ColumnName, mysqlVariableReference1);

            String query2 = String.format("DELETE FROM %s WHERE %s = %s;", db_TableName, id_ColumnName, mysqlVariableReference1);

            //#############################################
            //
            //#############################################
            queries.add(query1);
            queries.add(query2);

            //#############################################
            //
            //#############################################
            return queries;
        }
    }
}

package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Ingredients_Form;

import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Ingredient_Form.Add_Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Edit_Ingredients_Screen;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


//########################################################################
// Ingredients Form
//########################################################################
public class Edit_Ingredients_Form extends Add_Ingredients_Form
{
    //##################################################
    // Variables
    //##################################################
    // DELETE Use Smarter Method
    private String selectedIngredientID, selectedIngredientName;

    private Edit_Ingredients_Screen edit_ingredientsScreen;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Ingredients_Form(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Edit_Ingredients_Screen edit_ingredientsScreen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);
        this.edit_ingredientsScreen = edit_ingredientsScreen;
    }

    //##################################################################################################################
    // DB Info & Loading
    //##################################################################################################################
    public void update_Ingredients_Info_From_DB()
    {
        if (edit_ingredientsScreen.get_Update_Status_Of_Ingredient_Names())
        {
            return;
        }

        //############################################################
        // Get Selected Ingredient Name
        //############################################################
        selectedIngredientName = edit_ingredientsScreen.get_Selected_Ingredient_Name();

        if (selectedIngredientName==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable to grab Ingredient Name to edit it!!");
            return;
        }

        //###############################
        // Get Ingredient info from DB
        //###############################
        String getIngredientInfoString = get_Ingredient_Info_Select_Statement(selectedIngredientName);

        ArrayList<ArrayList<String>> ingredientInfo_Results = db.getMultiColumnQuery(getIngredientInfoString);

        if (ingredientInfo_Results==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable to grab selected ingredient info!");
            return;
        }

        //##############################
        // Get Info & Ingredient ID
        //##############################
        ArrayList<String> ingredientInfoInDB = ingredientInfo_Results.get(0);
        selectedIngredientID = ingredientInfoInDB.get(0);

        //##############################################################################################################
        // Load Data Into GUI & Store Info
        //##############################################################################################################
        int pos = 0; // removes first value being IngrdientID from DB Results which isn't on the form Label
        for (Map.Entry<String, Object[]> entry : ingredientsFormObjectAndValues.entrySet())
        {
            pos++;

            String rowLabel = entry.getKey();
            Component comp = (Component) entry.getValue()[0];
            String fieldValue = ingredientInfoInDB.get(pos);

            // Set DB Value for Row in memory
            set_Ingredients_Form_Object_And_Values(rowLabel, 2, fieldValue);

            if (comp instanceof JComboBox)
            {
                ((JComboBox<String>) comp).setSelectedItem(fieldValue);
            }
            else if (comp instanceof JTextField)
            {
                ((JTextField) comp).setText(fieldValue);
            }
        }

        //##############################
        // Set Salt To Grams
        //##############################
        saltMeasurement_JComboBox.setSelectedItem("g");
    }

    public void load_Ingredients_Form_Data()
    {
        //##############################
        // Set Form With Ingredient Info
        //##############################
        for (Map.Entry<String, Object[]> entry : ingredientsFormObjectAndValues.entrySet())
        {
            String dbFieldValue = (String) entry.getValue()[2];
            Component comp = (Component) entry.getValue()[0];

            if (comp instanceof JComboBox)
            {
                ((JComboBox<String>) comp).setSelectedItem(dbFieldValue);
            }
            else if (comp instanceof JTextField)
            {
                ((JTextField) comp).setText(dbFieldValue);
            }
        }

        //##############################
        // Set Salt To Grams
        //##############################
        saltMeasurement_JComboBox.setSelectedItem("g");
    }

    //##################################################################################################################
    // Override Methods
    //##################################################################################################################
    @Override
    protected void create_Icon_Bar()
    {
        create_Icon_Bar_On_GUI(false);
    }

    @Override
    protected String extra_Validation_Ingredient_Name(String errorTxt, String makeIngredientName)
    {
        makeIngredientName = remove_Space_And_Hidden_Chars(makeIngredientName);
        String previousIngredientName = remove_Space_And_Hidden_Chars(selectedIngredientName);

        System.out.printf("\n\nextra_Validation_IngredientName() \nName 1: %s || Name2: %s", makeIngredientName, previousIngredientName);

        if (!(previousIngredientName.equals(makeIngredientName)))
        {
            if (check_IF_IngredientName_In_DB(makeIngredientName))
            {
                errorTxt += String.format("\n\n@@  Ingredient named %s already exists within the database!", makeIngredientName);
            }
        }

        return errorTxt;
    }

    @Override
    protected void extra_Clear_Ingredients_Form()
    {
        // Reset Variables
        selectedIngredientID = "";
        selectedIngredientName = "";

        // Remove FormField / DB Values in Memory to null
        for (Map.Entry<String, Object[]> info : ingredientsFormObjectAndValues.entrySet())
        {
            String rowLabel = info.getKey();
            Object[] row = info.getValue();

            row[1]=null; row[2]=null;

            ingredientsFormObjectAndValues.put(rowLabel, row);
        }
    }

    @Override
    public String get_Ingredients_Form_Update_String(String ingredientID) // HELLO needs further update methods created for gui
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        String
                tableName = "ingredients_info",
                ingredientIDColName = "ingredient_id",
                setQuery = String.format("UPDATE %s \nSET", tableName);

        //##############################################################################################################
        //
        //##############################################################################################################
        boolean dataChanged = false;
        for (Map.Entry<String, Triplet<String, String, String>> key : ingredientsFormLabelsMapsToValues.entrySet())
        {
            String rowLabel = key.getKey();
            String formFieldValue = (String) ingredientsFormObjectAndValues.get(rowLabel)[1];
            String dbValue = (String) ingredientsFormObjectAndValues.get(rowLabel)[2];
            //            System.out.printf("\n\n%s \nDB: %s Form: %s ",rowLabel, dbValue, formFieldValue);

            //####################################
            //Check if data changed from DB Values
            //####################################
            if(formFieldValue.equals(dbValue)) // if no changes need to be made
            {
                continue;
            }

            dataChanged = true;

            //####################################
            // Get Query Values
            //####################################
            Triplet<String, String, String> value = key.getValue();

            String sqlColumnName = value.getValue1();
            String mysqlColumnDataType = value.getValue2();

            //####################################
            //
            //####################################
            if (rowLabel.equals("Ingredient Type"))
            {
                String ingredientTypeSet = "SELECT ingredient_type_id FROM ingredient_types WHERE ingredient_type_name = \"";
                formFieldValue = String.format("(%s%s\")", ingredientTypeSet, formFieldValue);
            }
            else if (mysqlColumnDataType.equals("String"))
            {
                formFieldValue = String.format("\"%s\"", formFieldValue);
            }

            //####################################
            //
            //####################################
            setQuery += String.format("\n%s = %s,", sqlColumnName, formFieldValue);
        }

        //##############################################################################################################
        // Return results
        //##############################################################################################################
        if (dataChanged)
        {
            //Remove "," from end of last field update to allow where statement to be added
            setQuery =  setQuery.substring(0, setQuery.length() - 1);
            String whereStatement = String.format("\nWHERE %s = %s;", ingredientIDColName, ingredientID);
            setQuery += whereStatement;

            return setQuery;
        }

        System.out.println("\n\nget_IngredientsForm_UpdateString() IngredientsForm No Data Changed");
        return null;
    }

    //##################################################################################################################
    // Accessor Methods
    //##################################################################################################################
    private String get_Ingredient_Info_Select_Statement(String ingredientName)
    {
        try
        {
            //##############################
            // Get Ingredient Info
            //##############################

            String
                    sqlIngredientIDNameCol = "ingredient_id",
                    tableName = "ingredients_info",
                    tableReference = "info",
                    sqlIngredientTypeNameCol = "ingredient_type_name",
                    sqlIngredientTypeTable = "ingredient_types",
                    selectStatement = String.format("SELECT \n%s.%s,", tableReference, sqlIngredientIDNameCol);

            int pos = -1, listSize = ingredientsFormLabelsMapsToValues.size();
            String mysqlIngredientNameKey = "";

            for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
            {
                pos++;
                //####################################
                //
                //####################################
                Triplet<String, String, String> value = entry.getValue();

                String formLabelName = entry.getKey();
                String sqlColumnName = value.getValue1();

                //####################################
                //
                //####################################
                String stringToAdd = "";
                stringToAdd = String.format("\n%s.%s", tableReference, sqlColumnName);

                //
                if (formLabelName.equals("Ingredient Type"))
                {
                    stringToAdd = String.format("""
                                    \n(SELECT t.%s FROM %s t  WHERE t.%s = %s.%s)  AS ingredient_type""",
                            sqlIngredientTypeNameCol, sqlIngredientTypeTable, sqlColumnName, tableReference, sqlColumnName);
                }
                else if (formLabelName.equals("Ingredient Name"))
                {
                    mysqlIngredientNameKey = sqlColumnName;
                }

                selectStatement += stringToAdd;

                //
                if (pos==listSize - 1)
                {
                    selectStatement += String.format("\n\nFROM %s %s", tableName, tableReference);

                    String whereStatement = String.format("\nWHERE %s.%s = \"%s\";", tableReference, mysqlIngredientNameKey, ingredientName);

                    selectStatement += whereStatement;
                    break;
                }
                selectStatement += ",";
            }

            return selectStatement;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError mysqlGetIngredientInfo() \n%s", e);
        }

        return null;
    }

    public String get_Selected_IngredientID()
    {
        return selectedIngredientID;
    }

    public String get_Selected_IngredientName()
    {
        return selectedIngredientName;
    }
}


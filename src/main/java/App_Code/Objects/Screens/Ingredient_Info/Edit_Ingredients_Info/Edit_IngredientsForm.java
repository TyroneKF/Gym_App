package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import org.javatuples.Triplet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


//########################################################################
// Ingredients Form
//########################################################################
public class Edit_IngredientsForm extends Add_IngredientsForm
{
    // DELETE Use Smarter Method
    private String selectedIngredientID, selectedIngredientName;


    private Edit_IngredientsScreen edit_ingredientsScreen;

    //##################################################################################################################
    //
    //##################################################################################################################
    public Edit_IngredientsForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Edit_IngredientsScreen edit_ingredientsScreen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);
        this.edit_ingredientsScreen = edit_ingredientsScreen;
    }

    //##################################################################################################################
    //
    //##################################################################################################################
    @Override
    protected void createIconBar()
    {
        createIconBarOnGUI(false);
    }

    @Override
    protected String extra_Validation_IngredientName(String errorTxt, String makeIngredientName)
    {
        makeIngredientName = removeSpaceAndHiddenChars(makeIngredientName);
        String previousIngredientName = removeSpaceAndHiddenChars(selectedIngredientName);

        System.out.printf("\n\nextra_Validation_IngredientName() \nName 1: %s || Name2: %s", makeIngredientName, previousIngredientName);

        if (!(previousIngredientName.equals(makeIngredientName)))
        {
            if (checkIfIngredientNameInDB(makeIngredientName))
            {
                errorTxt += String.format("\n\n@@  Ingredient named %s already exists within the database!", makeIngredientName);
            }
        }

        return errorTxt;
    }

    @Override
    protected void extraClearIngredientsForm()
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

    private String getIngredientInfoSelectStatement(String ingredientName)
    {
        try
        {
            //##############################
            // Get Ingredient Info
            //##############################

            String
                    sqlIngredientIDNameCol = "IngredientID",
                    tableName = "ingredients_info",
                    tableReference = "info",
                    sqlIngredientTypeNameCol = "Ingredient_Type_Name",
                    sqlIngredientTypeTable = "ingredientTypes",
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
                                    \n(SELECT t.%s FROM %s t  WHERE t.%s = %s.%s)  AS Ingredient_Type""",
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

    public void updateIngredientsInfoFromDB()
    {
        if (edit_ingredientsScreen.getUpdateStatusOfIngredientNames())
        {
            return;
        }

        //############################################################
        // Get Selected Ingredient Name
        //############################################################
        selectedIngredientName = edit_ingredientsScreen.getSelectedIngredientName();

        if (selectedIngredientName==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab Ingredient Name to edit it!!");
            return;
        }

        //###############################
        // Get Ingredient info from DB
        //###############################
        String getIngredientInfoString = getIngredientInfoSelectStatement(selectedIngredientName);

        ArrayList<ArrayList<String>> ingredientInfo_Results = db.getMultiColumnQuery(getIngredientInfoString);

        if (ingredientInfo_Results==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab selected ingredient info!");
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
            setIngredientsFormObjectAndValues(rowLabel, 2, fieldValue);

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

    public void loadIngredientsFormData()
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

    @Override
    protected String get_IngredientsForm_UpdateString(String ingredientID) // HELLO needs further update methods created for gui
    {
        //##############################################################################################################
        //
        //##############################################################################################################
        String
                tableName = "ingredients_info",
                ingredientIDColName = "IngredientID",
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
                String ingredientTypeSet = "SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = \"";
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
    //
    //##################################################################################################################
    public String getSelectedIngredientID()
    {
        return selectedIngredientID;
    }

    public String getSelectedIngredientName()
    {
        return selectedIngredientName;
    }
}


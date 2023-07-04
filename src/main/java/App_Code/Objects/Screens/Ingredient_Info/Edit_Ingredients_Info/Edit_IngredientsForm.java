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
    private String selectedIngredientID, selectedIngredientName;

    private String previousIngredientType, previousIngredientName;

    private Edit_IngredientsScreen edit_ingredientsScreen;
    private ArrayList<Component> formObjects;

    private ArrayList<String>
            dbFormData = new ArrayList<>(),
            currentFormData = new ArrayList<>();



    public Edit_IngredientsForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Edit_IngredientsScreen edit_ingredientsScreen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, btnText, btnWidth, btnHeight);
        this.edit_ingredientsScreen = edit_ingredientsScreen;
    }

    @Override
    protected void createIconBar()
    {
        createIconBarOnGUI(false);
    }

    @Override
    protected String extra_Validation_IngredientName(String errorTxt, String makeIngredientName)
    {
        if (makeIngredientName != null || !(makeIngredientName.equals("")))
        {
            makeIngredientName = removeSpaceAndHiddenChars(makeIngredientName);
            String previousIngredientName = removeSpaceAndHiddenChars(selectedIngredientName);

            System.out.printf("\n\nName 1: %s || Name2: %s", makeIngredientName, previousIngredientName);

            if (!(previousIngredientName.equals(makeIngredientName)))
            {
                if (checkIfIngredientNameInDB(makeIngredientName))
                {
                    errorTxt += String.format("\n\n@@  Ingredient named %s already exists within the database!", makeIngredientName);
                }
            }
        }
        return errorTxt;
    }

    @Override
    protected void extraClearIngredientsForm()
    {
        dbFormData.clear();
        currentFormData.clear();
    }

    public ArrayList<String> getDbFormData()
    {
        return dbFormData;
    }

    public ArrayList<String> getCurrentFormData()
    {
        return currentFormData;
    }

    public void updateIngredientsFormWithInfoFromDB()
    {
        if (edit_ingredientsScreen.getUpdateStatusOfIngredientNames())
        {
            return;
        }

        //############################################################
        // Ingredient ID
        //############################################################
        selectedIngredientName = edit_ingredientsScreen.getSelectedIngredientName();

        if (selectedIngredientName == null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab Ingredient Name to edit it!!");
            return;
        }

        selectedIngredientName = selectedIngredientName; //HELLO what is the use?
        previousIngredientName = selectedIngredientName;

        //###############################
        //
        //###############################
        String getIngredientInfoString = mysqlGetIngredientInfo(selectedIngredientName);

        ArrayList<ArrayList<String>> ingredientInfo_R = db.getMultiColumnQuery(getIngredientInfoString);

        if (ingredientInfo_R == null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab selected ingredient info!");
            return;
        }

        dbFormData = ingredientInfo_R.get(0);

        //##############################
        // Get Ingredient ID
        //##############################
        selectedIngredientID = dbFormData.get(0);

        //##############################################################################################################
        // Get Ingredient ID
        //##############################################################################################################
        loadIngredientsFormData();
    }

    public void loadIngredientsFormData()
    {
        //##############################
        // Set Form With Ingredient Info
        //##############################

        int formObjectsIndex = 0;

        for (int i = 1; i < dbFormData.size(); i++)
        {
            Component comp = formObjects.get(formObjectsIndex); // query size and form objects size arent at the same index
            String value = dbFormData.get(i);

            // setting previous ingredient Type value
            if (formObjectsIndex == getIngredientTypeObjectIndex()) // accounting for id being added
            {
                previousIngredientType = value;
            }

            if (comp instanceof JComboBox)
            {
                ((JComboBox<String>) comp).setSelectedItem(value);
            }
            else if (comp instanceof JTextField)
            {
                ((JTextField) comp).setText(value);
            }
            formObjectsIndex++;
        }

        //##############################
        // Set Salt To Grams
        //##############################
        saltMeasurement_JComboBox.setSelectedItem("g");
    }

    private String mysqlGetIngredientInfo(String ingredientName)
    {
        try
        {
            //############################################################
            // Update IngredientsForm
            //############################################################
            formObjects = getIngredientsFormObjects();

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
                //####################################z
                //z
                //####################################z
                pos++;
                Triplet<String, String, String> value = entry.getValue();

                String formLabelName = entry.getKey();
                String sqlColumnName = value.getValue1();

                //####################################
                //
                //####################################
                String stringToAdd = "";
                stringToAdd = String.format("\n%s.%s", tableReference, sqlColumnName);

                //
                if (pos == ingredientTypeObjectIndex)
                {
                    String ingredientTypeStatement = String.format("""
                                    \n(SELECT t.%s FROM %s t  WHERE t.%s = %s.%s)  AS Ingredient_Type""",
                            sqlIngredientTypeNameCol, sqlIngredientTypeTable, sqlColumnName, tableReference, sqlColumnName);

                    stringToAdd = ingredientTypeStatement;
                }

                selectStatement += stringToAdd;

                //
                if (pos == ingredientNameObjectIndex)
                {
                    mysqlIngredientNameKey = sqlColumnName;
                }

                //
                if (pos == listSize - 1)
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

    protected String get_IngredientsForm_UpdateString(String ingredientID) // HELLO needs further update methods created for gui
    {
        //####################################
        //
        //####################################
        JTextField ingredientName_JTxtF = (JTextField) ingredientsFormObjects.get(getIngredientNameObjectIndex());
        String ingredientName_Txt = ingredientName_JTxtF.getText().trim();


        String
                tableName = "ingredients_info",
                ingredientIDColName = "IngredientID",

                setQuery = String.format("UPDATE %s \nSET", tableName);
        //####################################
        //
        //####################################
        int pos = -1, listSize = ingredientsFormLabelsMapsToValues.size();
        for (Map.Entry<String, Triplet<String, String, String>> entry : ingredientsFormLabelsMapsToValues.entrySet())
        {
            pos++;
            Triplet<String, String, String> value = entry.getValue();

            String sqlColumnName = value.getValue1();
            String mysqlColumnDataType = value.getValue2();

            //####################################
            //
            //####################################
            Component formObject = ingredientsFormObjects.get(pos);
            String formFieldValue = "";

            if (formObject instanceof JTextField)
            {
                formFieldValue = ((JTextField) formObject).getText();
            }
            else if (formObject instanceof JComboBox)
            {
                formFieldValue = ((JComboBox) formObject).getSelectedItem().toString();
            }

            //####################################
            //
            //####################################
            if (pos == ingredientTypeObjectIndex)
            {
                String ingredientTypeSet = "SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = ";
                formFieldValue = String.format("(%s \"%s\")", ingredientTypeSet, formFieldValue);
            }
            else if (mysqlColumnDataType.equals("String"))
            {
                formFieldValue = String.format("\"%s\"", formFieldValue);
            }

            //####################################
            //
            //####################################
            currentFormData.add(formFieldValue);

            //####################################
            //
            //####################################

            if (pos == listSize - 1)
            {
                setQuery += String.format("\n%s = %s", sqlColumnName, formFieldValue);
                String whereStatement = String.format("\nWHERE %s = %s;", ingredientIDColName, ingredientID);
                setQuery += whereStatement;

                break;
            }

            setQuery += String.format("\n%s = %s,", sqlColumnName, formFieldValue);
        }

        //####################################
        // Return results
        //####################################
        System.out.printf("\n\n\nFieldValues: %s ", currentFormData);
        return setQuery;
    }

    //#########################################################################
    //
    //#########################################################################
    public String getPreviousIngredientName()
    {
        return previousIngredientName;
    }

    public String getPreviousIngredientType()
    {
        return previousIngredientType;
    }

    public String getSelectedIngredientID()
    {
        return selectedIngredientID;
    }

    public String getSelectedIngredientName()
    {
        return selectedIngredientName;
    }

    public ArrayList<Component> getFormObjects()
    {
        return formObjects;
    }
}


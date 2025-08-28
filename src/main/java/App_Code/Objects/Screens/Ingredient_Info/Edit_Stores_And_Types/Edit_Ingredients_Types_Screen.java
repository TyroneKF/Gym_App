package App_Code.Objects.Screens.Ingredient_Info.Edit_Stores_And_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Edit_IngredientsScreen;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Edit_Ingredients_Types_Screen extends Parent_For_Types_And_Stores_Screens
{
    protected String collapsibleBTNTXT1 = "Add Ingredients Type", collapsibleBTNTXT2 = "Edit Ingredients Type";

    public Edit_Ingredients_Types_Screen(MyJDBC db, Ingredients_Info_Screen ingredientsInfoScreen, Collection<String> jcomboBoxList)
    {
        super.sqlFilePath = "src/main/java/Documentation_And_Scripts/Database/Scripts/Editable_DB_Scripts/5.) IngredientTypes.sql";
        super.process = "ingredients types";

        this.db = db;
        this.parentIngredientsScreen = ingredientsInfoScreen;
        this.jcomboBoxList = jcomboBoxList;

        createInterface();
    }

    @Override
    protected void createInterface()
    {
        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################

        setLayout(new BorderLayout());

        //###################################################################################
        //   Create Main Centre Screen for Interface
        //##################################################################################
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        add(mainCentreScreen, BorderLayout.CENTER);


        //###########################
        //Add Ingredients Type Form
        //###########################
        super.addScreen = new AddIngredientsType(this, collapsibleBTNTXT1, 250, 50);
        addToContainer(mainCentreScreen, addScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit Ingredients Type Form
        //###########################
        super.editScreen = new EditIngredientType(this, collapsibleBTNTXT2, 250, 50);
        addToContainer(mainCentreScreen, editScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Space Divider
        //###########################
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(630, 50));
        jPanel.setBackground(Color.PINK);
        addToContainer(mainCentreScreen, jPanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        revalidate();
    }

    public class AddIngredientsType extends AddScreen
    {
        public AddIngredientsType(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void createForm()
        {
            super.mainLabel = "Add Ingredient Type Name";

            super.dataGatheringName = "Ingredient Type Name";
            super.dbColumnNameField = "ingredient_type_name";
            super.dbTableName = "ingredientTypes";


            //###################################################################
            // Drawing interface
            //####################################################################
            createAddScreenObjects();
            creatingAdditionalAddScreenObjects(); // for overwrite purposes
            addScreenObjects(); // adding all objects to the screen
        }

        @Override
        protected void successUploadMessage()
        {
            String text = String.format("\n\nSuccessfully Added New Ingredient Type: '%s'", jTextfieldTXT);
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void failureMessage()
        {
            String text = "\n\nFailed Upload - Couldn't Add New Ingredient Type";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void updateOtherScreens()
        {
            parentIngredientsScreen.addChangeOrRemoveIngredientsTypeName("addKey", jTextfieldTXT, null);
            parentIngredientsScreen.updateIngredientsFormTypeJComboBoxes();
        }
    }

    public class EditIngredientType extends EditScreen
    {
        public EditIngredientType(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void createForm()
        {
            super.lable1 = "Select Ingredient Type Name To Edit";
            super.label2 = "Change Ingredient Type Name";

            super.dataGatheringName = "Ingredient Type Name";
            super.dbColumnNameField = "ingredient_type_name";
            super.dbTableName = "ingredientTypes";

            super.idColumnName = "ingredient_type_id";
            super.fkTable = "ingredients_info";
            super.removeJComboBoxItems = new String[]{"None Of The Above", "UnAssigned"};


            //###################################################################
            // Drawing interface
            //####################################################################
            createAddScreenObjects();
            creatingAdditionalAddScreenObjects(); // for overwrite purposes
            addScreenObjects(); // adding all objects to the screen
        }

        @Override
        protected void successUploadMessage()
        {
            String text = String.format("\n\nSuccessfully Changed Ingredient Type From ' %s ' to ' %s ' !", selectedJComboBoxItemTxt, jTextfieldTXT);
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void failureMessage()
        {
            String text = "\n\nFailed Upload - Couldn't Add New Ingredient Type";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void updateOtherScreens()
        {
            System.out.printf("\n\n##############################################");
            System.out.printf("\nupdateOtherScreens()  \nnewKey: %s \noldKey: %s", jTextfieldTXT, selectedJComboBoxItemTxt); // HELLO REMOVE

            //#################################################################
            // Reset EditCreateForm
            //#################################################################
            Edit_IngredientsScreen editingIngredientsInfo = parentIngredientsScreen.getEditIngredientsForm();
            editingIngredientsInfo.refreshInterface(true, true);  // reset form`

            //###################################################################
            // Change IngredientsTypeToNames JComboBox & List in  EditCreateForm
            //###################################################################
            if (itemDeleted)
            {
                if (parentIngredientsScreen.addChangeOrRemoveIngredientsTypeName("removeKey", null, selectedJComboBoxItemTxt)) // change key
                {
                    editingIngredientsInfo.updateIngredientNamesToTypesJComboBox(); // update IngredientsTypeToNames JComboBox
                }
                else
                {
                    System.out.print("\n\nupdateOtherScreens() error deletingKey "); // HELLO REMOVE
                    return;
                }
            }
            else
            {
                if (parentIngredientsScreen.addChangeOrRemoveIngredientsTypeName("changeKeyName", jTextfieldTXT, selectedJComboBoxItemTxt)) // change key
                {
                    editingIngredientsInfo.updateIngredientNamesToTypesJComboBox(); // update IngredientsTypeToNames JComboBox
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
            parentIngredientsScreen.updateIngredientsFormTypeJComboBoxes();
            parentIngredientsScreen.setUpdateIngredientInfo(true);
        }

        @Override
        protected ArrayList<String> deleteBTNQueries(String mysqlVariableReference1, ArrayList<String> queries)
        {
            //#############################################
            //
            //#############################################
            String changeToValue = String.format("(SELECT %s FROM %s WHERE %s = 'UnAssigned')", idColumnName, dbTableName, dbColumnNameField);

            String query1 = String.format("""                  
                    UPDATE %s
                    SET %s =  %s
                    WHERE %s = %s;""", fkTable, idColumnName, changeToValue, idColumnName, mysqlVariableReference1);

            String query2 = String.format("DELETE FROM %s WHERE %s = %s;", dbTableName, idColumnName, mysqlVariableReference1);

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

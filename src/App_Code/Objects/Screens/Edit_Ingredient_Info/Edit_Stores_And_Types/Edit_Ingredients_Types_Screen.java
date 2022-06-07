package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Stores_And_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info.Edit_Ingredients_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Edit_Ingredients_Types_Screen extends Parent_For_Types_And_Stores_Screens
{
    protected String collapsibleBTNTXT1 = "Add Ingredients Type", collapsibleBTNTXT2 = "Edit Ingredients Type";


    public Edit_Ingredients_Types_Screen(MyJDBC db, Edit_Ingredients_Screen add_or_edit_ingredients_screen, Collection<String> jcomboBoxList)
    {
        this.db = db;
        this.add_or_edit_ingredients_screen = add_or_edit_ingredients_screen;
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
            super.dbColumnNameField = "Ingredient_Type_Name";
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
            String text = "\n\nSuccessfully Added New Ingredient Type";
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
            add_or_edit_ingredients_screen.updateIngredientsFormTypeJComboBoxes();
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
            super.dbColumnNameField = "Ingredient_Type_Name";
            super.dbTableName = "ingredientTypes";

            super.idColumnName = "Ingredient_Type_ID";
            super.fkTable = "ingredients_info";
            super.setToNull = false;
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
            String text = String.format("\n\nSuccessfully Changed Ingredient Type From ' %s ' to ' %s ' !", selectedItem, jtextfieldTXT);
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
            //#################################################################
            // Update CreateForm ingredientsForm  Type JComboBoxes
            //##################################################################
            add_or_edit_ingredients_screen.updateIngredientsFormTypeJComboBoxes();

            //#################################################################
            // Reset EditCreateForm
            //#################################################################
            Edit_Ingredients_Screen.EditingCreateForm editingCreateForm = add_or_edit_ingredients_screen.getEditingCreateForm();
            editingCreateForm.refreshInterface(true, true);  // reset form`

            //###################################################################
            // Change IngredientsTypeToNames JComboBox & List in  EditCreateForm
            //###################################################################
            if (itemDeleted)
            {
                if (add_or_edit_ingredients_screen.changeKeyIngredientsTypesList("removeKey", null,  selectedItem)) // change key
                {
                    editingCreateForm.updateIngredientNamesToTypesJComboBox(); // update IngredientsTypeToNames JComboBox
                }
            }
            else
            {
                if (add_or_edit_ingredients_screen.changeKeyIngredientsTypesList("changeKeyName", jtextfieldTXT, selectedItem)) // change key
                {
                    editingCreateForm.updateIngredientNamesToTypesJComboBox(); // update IngredientsTypeToNames JComboBox
                }
            }
        }
    }
}

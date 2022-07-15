package App_Code.Objects.Screens.Ingredient_Info.Edit_Stores_And_Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Edit_Ingredients;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Parent_Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Edit_Ingredients_Types_Screen extends Parent_For_Types_And_Stores_Screens
{
    protected String collapsibleBTNTXT1 = "Add Ingredients Type", collapsibleBTNTXT2 = "Edit Ingredients Type";


    public Edit_Ingredients_Types_Screen(MyJDBC db, Parent_Ingredients_Info_Screen ingredientsInfoScreen, Collection<String> jcomboBoxList)
    {
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
            parentIngredientsScreen.addChangeOrRemoveIngredientsTypeName("addKey", jtextfieldTXT, null);
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
            super.dbColumnNameField = "Ingredient_Type_Name";
            super.dbTableName = "ingredientTypes";

            super.idColumnName = "Ingredient_Type_ID";
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
            String text = String.format("\n\nSuccessfully Changed Ingredient Type From ' %s ' to ' %s ' !", selectedJComboBoxItemTxt, jtextfieldTXT);
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
            System.out.printf("\nupdateOtherScreens()  \nnewKey: %s \noldKey: %s", jtextfieldTXT, selectedJComboBoxItemTxt); // HELLO REMOVE

            //#################################################################
            // Reset EditCreateForm
            //#################################################################
            Edit_Ingredients editingIngredientsInfo = parentIngredientsScreen.getEditIngredientsForm();
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
                if (parentIngredientsScreen.addChangeOrRemoveIngredientsTypeName("changeKeyName", jtextfieldTXT, selectedJComboBoxItemTxt)) // change key
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
    }
}

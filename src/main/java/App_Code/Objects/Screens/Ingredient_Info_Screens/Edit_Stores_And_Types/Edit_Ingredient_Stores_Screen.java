package App_Code.Objects.Screens.Ingredient_Info_Screens.Edit_Stores_And_Types;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Edit_Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Edit_Ingredient_Stores_Screen extends Parent_For_Types_And_Stores_Screens
{
    protected String collapsibleBTNTXT1 = "Add Suppliers", collapsibleBTNTXT2 = "Edit Suppliers";

    public Edit_Ingredient_Stores_Screen(MyJDBC db, Ingredients_Info_Screen add_or_Parent__ingredients_Info_screen, Collection<String> jcomboBoxList)
    {
        //######################################
        //
        //######################################
        super.sqlFilePath = "src/main/java/Documentation_And_Scripts/Database/Scripts/Editable_DB_Scripts/7.) Stores.sql";
        super.process = "ingredients stores";

        //######################################
        //
        //######################################
        this.db = db;
        this.parentIngredientsScreen = add_or_Parent__ingredients_Info_screen;
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
        //Add Ingredients Stores Form
        //###########################
        super.addScreen = new AddStores(this, collapsibleBTNTXT1, 250, 50);
        addToContainer(mainCentreScreen, addScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit Ingredients Stores Form
        //###########################
        super.editScreen = new EditStores(this, collapsibleBTNTXT2, 250, 50);
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

    public class AddStores extends AddScreen
    {
        public AddStores(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void createForm()
        {
            super.mainLabel = "Add Supplier Name";

            super.dataGatheringName = "Supplier Name";
            super.dbColumnNameField = "store_name";
            super.dbTableName = "stores";
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
            String text = "\n\nSuccessfully Added New Supplier!";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void failureMessage()
        {
            String text = "\n\nFailed To Upload - Couldn't Add Supplier";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void updateOtherScreens()
        {
            parentIngredientsScreen.addOrRemoveSupplierFromList("add",jTextfieldTXT, null); // add to list
            parentIngredientsScreen.updateIngredientSuppliersJComboBoxes();
        }
    }

    public class EditStores extends EditScreen
    {
        public EditStores(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void createForm()
        {
            super.lable1 = "Select Supplier Name To Edit";
            super.label2 = "Change Supplier Name";

            super.dataGatheringName = "Supplier Name";
            super.dbColumnNameField = "Store_Name";
            super.dbTableName = "stores";

            super.idColumnName = "store_id";
            super.fkTable = "ingredients_info";
            super.removeJComboBoxItems = new String[]{"No Shop"};

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
            String text = String.format("\n\nSuccessfully Changed Suppliers From ' %s ' to ' %s ' !", selectedJComboBoxItemTxt, jTextfieldTXT);
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
            // this doesn't have to be in  a particular position, since all objects are deleted
            parentIngredientsScreen.updateIngredientSuppliersJComboBoxes();  // Clear shop GUI in app

            if (itemDeleted) // go through with the deleting process
            {
                parentIngredientsScreen.addOrRemoveSupplierFromList("delete", null, selectedJComboBoxItemTxt);
                return;
            }

            //  Must be the replace process requested
            parentIngredientsScreen.addOrRemoveSupplierFromList("replace", jTextfieldTXT, selectedJComboBoxItemTxt);
        }

        @Override
        protected ArrayList<String> deleteBTNQueries(String mysqlVariableReference1, ArrayList<String> queries)
        {
            //######################################
            // Update ingredients_in_meal
            //######################################
            String query1 = String.format("""
                    UPDATE ingredients_in_sections_of_meal
                    SET pdid = NULL
                    WHERE pdid IN (SELECT pdid FROM ingredientInShops WHERE store_id = %s);""", mysqlVariableReference1);

            //######################################
            // Update  ingredientInShops & Stores
            //######################################
            String query2 = String.format("DELETE FROM ingredientInShops WHERE store_id = %s;", mysqlVariableReference1);
            String query3 = String.format("DELETE FROM stores WHERE store_id = %s;", mysqlVariableReference1);

            //#############################################
            //
            //#############################################
            queries.add(query1);
            queries.add(query2);
            queries.add(query3);

            //#############################################
            //
            //#############################################
            return queries;
        }
    }
}

package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Stores_And_Types;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info.Add_Or_Edit_Ingredients_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Edit_Ingredient_Stores extends Parent_For_Types_And_Stores
{
    protected String collapsibleBTNTXT1 = "Add Suppliers", collapsibleBTNTXT2 = "Edit Suppliers";

    public Edit_Ingredient_Stores(MyJDBC db, Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen, Collection<String> jcomboBoxList)
    {
        this.db = db;
        this.add_or_edit_ingredients_screen = add_or_edit_ingredients_screen;
        this.jcomboBoxList = jcomboBoxList;

        createInterface();
    }

    @Override
    protected void createInterface()
    {
        System.out.printf("\n\nHere1");
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
        AddStores addStores = new AddStores(this, collapsibleBTNTXT1, 250, 50);
        addToContainer(mainCentreScreen, addStores, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit Ingredients Type Form
        //###########################
        EditScreen editStores = new EditStores(this, collapsibleBTNTXT2, 250, 50);
        addToContainer(mainCentreScreen, editStores, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
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
            System.out.printf("\n\nHere2");
            super.mainLabel = "Add Supplier Name";

            super.dataGatheringName = "Supplier Name";
            super.dbColumnNameField = "Store_Name";
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
            String text = "\n\nFailed Upload - Couldn't Add Supplier";
            JOptionPane.showMessageDialog(null, text);
        }

        @Override
        protected void updateOtherScreens()
        {
            add_or_edit_ingredients_screen.updateAllSuppliers();
        }
    }

    public class EditStores extends  EditScreen
    {
        public EditStores(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        @Override
        protected void createForm()
        {
            System.out.printf("\n\nHere4");

            super.lable1 = "Select Ingredient Type Name To Edit";
            super.label2 = "Change Ingredient Type Name";

            super.dataGatheringName = "Supplier Name";
            super.dbColumnNameField = "Store_Name";
            super.dbTableName = "stores";

            super.idColumnName = "StoreID";
            super.fkTable = "ingredients_info";
            super.setToNull = true;

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
    }
}

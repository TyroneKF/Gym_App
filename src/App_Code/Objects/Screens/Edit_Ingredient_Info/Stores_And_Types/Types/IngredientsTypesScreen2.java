package App_Code.Objects.Screens.Edit_Ingredient_Info.Stores_And_Types.Types;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Screens.Edit_Ingredient_Info.IngredientsInfo.Add_Or_Edit_Ingredients_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class IngredientsTypesScreen2 extends ParentClass
{
    protected String collapsibleBTNTXT1 = "Add Ingredients Type", collapsibleBTNTXT2 = "Edit Ingredients Type";

    public IngredientsTypesScreen2(MyJDBC db, Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen, Collection<String> jcomboBoxList)
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
        AddIngredientsType addIngredientsTypeScreen = new AddIngredientsType(this, collapsibleBTNTXT1, 250, 50);
        addToContainer(mainCentreScreen, addIngredientsTypeScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit Ingredients Type Form
        //###########################
        EditIngredientType editIngredientsTypeScreen = new EditIngredientType(this, collapsibleBTNTXT2, 250, 50);
        addToContainer(mainCentreScreen, editIngredientsTypeScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

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


    }

    public class EditIngredientType extends EditScreen
    {
        private JPanel jcomboBoxJPanel;
        String
                lable1, lablel2,
                idColumnName;

        public EditIngredientType(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }
    }
}

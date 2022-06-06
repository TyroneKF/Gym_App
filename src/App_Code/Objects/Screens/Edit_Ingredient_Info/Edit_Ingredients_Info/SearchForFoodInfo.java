package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

//#################################################################################################################
//
//##################################################################################################################

import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import java.awt.*;

public class SearchForFoodInfo extends CollapsibleJPanel
{
    Edit_Ingredients_Screen.CreateForm.IngredientsForm ingredientsForm;
    Find_Ingredients_Info_Screen find_ingredients_info_screen;

    public SearchForFoodInfo(Container parentContainer, Edit_Ingredients_Screen.CreateForm.IngredientsForm ingredientsForm, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, btnText, btnWidth, btnHeight);
        this.ingredientsForm = ingredientsForm;

        createSearchForFoodInfo();
        expandJPanel();
    }

    private void createSearchForFoodInfo()
    {
        //###################################################################
        //
        //###################################################################
        JPanel mainJPanel = getCentreJPanel();
        mainJPanel.setLayout(new BorderLayout());

        //###################################################################
        //
        //###################################################################

        find_ingredients_info_screen = new Find_Ingredients_Info_Screen(getCentreJPanel(),430, 350,  ingredientsForm);
        mainJPanel.add(find_ingredients_info_screen, BorderLayout.CENTER);//
    }
}

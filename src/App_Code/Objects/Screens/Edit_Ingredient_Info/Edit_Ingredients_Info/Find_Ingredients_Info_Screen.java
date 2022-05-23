package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.API.Nutritionix.NutritionIx_API;

import java.awt.*;

public class Find_Ingredients_Info_Screen extends CollapsibleJPanel
{
    NutritionIx_API nutritionIx_api;

    public Find_Ingredients_Info_Screen(Container parentContainer, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, btnText, btnWidth, btnHeight);
        expandJPanel();

        nutritionIx_api = new NutritionIx_API();
    }
}

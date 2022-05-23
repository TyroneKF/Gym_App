package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.API.Nutritionix.NutritionIx_API;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class Find_Ingredients_Info_Screen extends CollapsibleJPanel
{
    NutritionIx_API nutritionIx_api;

    public Find_Ingredients_Info_Screen(Container parentContainer, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, btnText, btnWidth, btnHeight);
        expandJPanel();

        nutritionIx_api = new NutritionIx_API();
    }

    public void findFoodInfo(String food)
    {
        LinkedHashMap<String, Object> foodInfo = nutritionIx_api.getFoodNutritionalInfo(food);

        if(foodInfo == null)
        {
            JOptionPane.showMessageDialog( null,String.format("\n\nError \n\nUnable to get nutritional info for the requested food '%s'!", food));
            return;
        }
        JOptionPane.showMessageDialog( null,String.format("\n\nSuccessfully got the nutritional info for the food '%s'!", food));

    }
}

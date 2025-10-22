package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Shop Form and Ingredients Form
public class Parent_IngredientForm_And_ShopForm extends CollapsibleJPanel
{
    //##################################################
    // Variables
    //##################################################
    protected JPanel northPanel = new JPanel(new GridBagLayout());
    protected Ingredients_Info_Screen ingredients_info_screen;

    protected Meal_Plan_Screen mealPlanScreen;
    protected MyJDBC db;
    protected Integer planID, tempPlanID;
    protected String planName;
    protected GridBagConstraints gbc = new GridBagConstraints();

    protected final int totalNumbersAllowed = 7, decimalScale = 2, decimalPrecision = totalNumbersAllowed - decimalScale, charLimit = 8;

    //##################################################
    // Constructor
    //##################################################
    public Parent_IngredientForm_And_ShopForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, btnText, btnWidth, btnHeight);

        //##################################################
        //
        //##################################################
        this.ingredients_info_screen = ingredients_info_screen;
        this.db = ingredients_info_screen.getDb();
        this.planID = ingredients_info_screen.get_PlanID();
        this.tempPlanID = ingredients_info_screen.get_TempPlanID();
        this.planName = ingredients_info_screen.get_PlanName();
        this.mealPlanScreen = ingredients_info_screen.get_MealPlan_Screen();
    }

    //##################################################
    // Methods
    //##################################################
    protected String convert_To_Big_Decimal(String value, String rowLabel, int rowNumber, JTextField jTextField, boolean checkIfValueEquals0)
    {
        String errorTxt = "";

        String txt = String.format("must be number which has %s numbers in it! Or, a decimal number (%s,%s) with a max of %s numbers before the decimal point and  a of max of  %s numbers after the decimal point!",
                decimalPrecision, decimalPrecision, decimalScale, decimalPrecision, decimalScale);
        try
        {
            BigDecimal zero = new BigDecimal(0);

            //#####################################################
            // Convert Numbers Using Precision & Scale point Values
            //#####################################################
            BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
            int valueScale = bdFromString.scale();
            int valuePrecision = bdFromString.precision();

            if (valueScale > decimalScale) // only round to scale, if needed as otherwise whole numbers get lost etc 5566 = nothing
            {
                bdFromString = bdFromString.setScale(decimalScale, RoundingMode.DOWN); // round the number
            }

            //#####################################################
            // Java Concept Of Precision
            //#####################################################
            if (valueScale == 0 && valuePrecision > decimalPrecision) // the number is too big
            {
                errorTxt += String.format("\nOn Row: %s '%s', %s ",rowNumber, rowLabel, txt);
            }

            //#####################################################
            // MySQL Concept Of Precision
            //#####################################################
            else if (valueScale > 0 && bdFromString.setScale(0, RoundingMode.FLOOR).precision() > decimalPrecision)
            {
                errorTxt += String.format("\nOn Row: %s '%s', %s ", rowNumber,rowLabel, txt);
            }

            //#####################################################
            // Check if the value is bigger than 0
            //#####################################################

            if (bdFromString.compareTo(zero) < 0)// "<")
            {
                errorTxt += String.format("\nOn Row: %s '%s', must have a value which is bigger than 0 and %s", rowNumber,rowLabel, txt);
            }

            //#####################################################
            // Format Data in Cell
            //#####################################################
            // Add decimal point if the number doesn't have it
            String bdStringValue = String.format("%s", bdFromString);
            if(!(bdStringValue.contains(".")))
            {
                bdStringValue+=".00";
            }
            jTextField.setText(bdStringValue);

            //#####################################################
            // Check if value is equal to 0
            //#####################################################
            if(checkIfValueEquals0 && bdFromString.compareTo(zero) == 0)
            {
                errorTxt += String.format("\nOn Row: %s '%s', value must be bigger than 0 and %s", rowNumber,rowLabel, txt);
            }
        }
        catch (Exception e)
        {
            errorTxt += String.format("\nOn Row: %s '%s', %s ", rowNumber,rowLabel, txt);
        }

        return errorTxt;
    }

    protected boolean does_String_Contain_Given_Characters(String stringToCheck, String condition)
    {
        Pattern p1 =
                (condition==null || condition.equals("")) ?
                        Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE):
                        Pattern.compile(String.format("%s",condition), Pattern.CASE_INSENSITIVE);

        Matcher m1 = p1.matcher(stringToCheck.replaceAll("\\s+", ""));
        boolean b1 = m1.find();

        if (b1)
        {
            return true;
        }

        return false;
    }

    protected void add_To_Container(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                                    int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        gbc.ipady = ipady;
        gbc.ipadx = ipadx;

        switch (fill.toLowerCase())
        {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;

            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }

        container.add(addToContainer, gbc);
    }
}

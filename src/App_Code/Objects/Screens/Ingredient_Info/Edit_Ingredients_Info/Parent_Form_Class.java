package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


// Shop Form and Ingredients Form
public class Parent_Form_Class extends CollapsibleJPanel
{
    //##################################################
    //
    //##################################################
    protected JPanel northPanel = new JPanel(new GridBagLayout());
    protected Ingredients_Info_Screen ingredients_info_screen;

    protected Meal_Plan_Screen mealPlanScreen;
    protected MyJDBC db;
    protected Integer planID, tempPlanID;
    protected String planName;
    protected GridBagConstraints gbc = new GridBagConstraints();

    //##################################################
    //
    //##################################################
    protected final int totalNumbersAllowed = 7, decimalScale = 2, decimalPrecision = totalNumbersAllowed - decimalScale, charLimit = 8;

    public Parent_Form_Class(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, btnText, btnWidth, btnHeight);

        //##################################################
        //
        //##################################################
        this.ingredients_info_screen = ingredients_info_screen;
        this.db = ingredients_info_screen.getDb();
        this.planID = ingredients_info_screen.getPlanID();
        this.tempPlanID = ingredients_info_screen.getTempPlanID();
        this.planName = ingredients_info_screen.getPlanName();
    }

    protected String convertToBigDecimal(String value, String errorTxt, String rowLabel, int rowNumber, JTextField jTextField)
    {
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
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowLabel, rowNumber, txt);
            }

            //#####################################################
            // MySQL Concept Of Precision
            //#####################################################
            else if (valueScale > 0 && bdFromString.setScale(0, RoundingMode.FLOOR).precision() > decimalPrecision)
            {
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowLabel, rowNumber, txt);
            }

            //#####################################################
            // Format Data in Cell
            //#####################################################
            jTextField.setText(String.format("%s", bdFromString));

            //#####################################################
            // Check if the value is bigger than 0
            //#####################################################

            if (bdFromString.compareTo(zero) < 0)// "<")
            {
                errorTxt += String.format("\n\n  ' %s 'on Row: %s, must have a value which is bigger than 0 and %s", rowLabel, rowNumber, txt);
            }
        }
        catch (Exception e)
        {
            errorTxt += String.format("\n\n  ' %s 'on Row: %s, %s ", rowLabel, rowNumber, txt);
        }

        return errorTxt;
    }

    protected void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
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

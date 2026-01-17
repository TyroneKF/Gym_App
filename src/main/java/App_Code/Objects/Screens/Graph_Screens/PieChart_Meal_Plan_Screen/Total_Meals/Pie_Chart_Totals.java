package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Graph_Objects.Pie_Chart;
import App_Code.Objects.Tables.JTable_JDBC.Children.View_Data_Tables.Total_Meal_Table.Total_Meal_Macro_Columns;
import App_Code.Objects.Tables.MealManager;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.math.BigDecimal;
import java.text.AttributedString;

public class Pie_Chart_Totals extends Pie_Chart
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    protected MealManager mealManager;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart_Totals
    (
            MealManager mealManager,
            Color[] colors,
            int frameWidth,
            int frameHeight,
            int rotateDelay,
            Font titleFont,
            Font labelFont,
            Font legendFont,
            DefaultPieDataset<Total_Meal_Macro_Columns> datasetInput
    )
    {
        //#################################################################
        // Super Constructor
        //#################################################################
        super("", colors, frameWidth, frameHeight, rotateDelay, titleFont, labelFont, legendFont, datasetInput);
        
        this.mealManager = mealManager;
        
        //#################################################################
        // Override Generic Label Generator
        //#################################################################
        PieSectionLabelGenerator labelGen = new PieSectionLabelGenerator()
        {
            @Override
            public String generateSectionLabel(PieDataset dataset, Comparable macroKey)
            {
                //###############################
                // Macro Info
                //###############################
                String space = getSpace();
                Total_Meal_Macro_Columns macro_name = (Total_Meal_Macro_Columns) macroKey;
                
                BigDecimal macroValue = (BigDecimal) dataset.getValue(macro_name);
                BigDecimal macrosTotal = get_DatasetTotal();
                
                switch (macro_name)
                {
                    case TOTAL_PROTEIN ->
                    {
                        int percent = percent_Calculator(macroValue, macrosTotal);
                        return String.format("%sProtein [ %d%% ] - %s g%s", space, percent, macroValue, space);
                    }
                    case TOTAL_CARBOHYDRATES ->
                    {
                        BigDecimal sugarsMacroValue = (BigDecimal) dataset.getValue(Total_Meal_Macro_Columns.TOTAL_SUGARS_OF_CARBS);
                        BigDecimal total_Carbs = macroValue.add(sugarsMacroValue);
                        int percent = percent_Calculator(total_Carbs, macrosTotal);
                        
                        return String.format("%sCarbohydrates [ %d%% ] - %s g", space, percent, macroValue);
                    }
                    case TOTAL_FATS ->
                    {
                        BigDecimal satFatMacroValue = (BigDecimal) dataset.getValue(Total_Meal_Macro_Columns.TOTAL_SATURATED_FAT);
                        BigDecimal totalFats = macroValue.add(satFatMacroValue);
                        int percent = percent_Calculator(totalFats, macrosTotal);
                        
                        return String.format("%sFats [ %d%% ] - %s g%s", space, percent, macroValue, space);
                    }
                    case TOTAL_SATURATED_FAT ->
                    {
                        return String.format("%sSaturated Fat - %s g%s", space,  macroValue, space);
                    }
                    case TOTAL_SUGARS_OF_CARBS ->
                    {
                        return String.format("%sSugar Of Carbs - %s g%s", space,  macroValue, space);
                    }
                    default -> throw new IllegalStateException(String.format("Unknown Value : %s", macro_name.key() ));
                }
            }
            
            @Override
            public AttributedString generateAttributedSectionLabel(PieDataset pieDataset, Comparable comparable)
            {
                return null;
            }
        };
        
        //#################################################################
        // Configurations
        //#################################################################
        update_PieChart_Title(); // set Title
        plot.setLegendLabelGenerator(labelGen); // Set Label Generator
    }
    
    //####################################################################################
    // Variables
    // #################################################################################################################
    @Override
    protected void dataset_ActionEvents()
    {
        System.out.printf("\n\ndataset_ActionEvents()");
        calculate_Dataset_Total();
        is_PieChart_Empty_MSG();
        reDraw_Legend();
        update_PieChart_Title();
    }
    
    public void update_PieChart_Title()
    {
        String title = String.format("[%s]      %s Macros       [ %s   kcal]",
                mealManager.get_Current_Meal_Time_GUI(),
                mealManager.get_Current_Meal_Name(),
                mealManager.get_Value_On_Total_Meal_Table(Total_Meal_Macro_Columns.TOTAL_CALORIES));
        
        setTitle(title);
    }
    
    protected String getSpace()
    {
        return "\u00A0\u00A0";
    }
}

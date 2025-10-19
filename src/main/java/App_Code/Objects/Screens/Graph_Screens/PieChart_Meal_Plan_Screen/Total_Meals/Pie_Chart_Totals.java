package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Graph_Objects.Pie_Chart;
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
    
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart_Totals(String title, Color[] colors, int frameWidth, int frameHeight, int rotateDelay, Font titleFont,
                            Font labelFont, Font legendFont, DefaultPieDataset<String> datasetInput)
    
    {
        //#################################################################
        // Super Constructor
        //#################################################################
        super(title, colors, frameWidth, frameHeight, rotateDelay, titleFont, labelFont, legendFont, datasetInput);
        
        //#################################################################
        // Override Generic Label Generator
        //#################################################################
        PieSectionLabelGenerator labelGen = new PieSectionLabelGenerator()
        {
            @Override
            public String generateSectionLabel(PieDataset dataset, Comparable macroName)
            {
                //###############################
                // Macro Info
                //###############################
                BigDecimal
                        macroValue = (BigDecimal) dataset.getValue(macroName),
                        macrosTotal = get_DatasetTotal();
                
                //###############################
                // Fats / Carbs / Protein Labels
                //###############################
                if (macroName.equals("Protein"))
                {
                    int percent = percent_Calculator(macroValue, macrosTotal);
                    
                    return String.format("%s [ %d%% ] - %s g ", macroName, percent, macroValue);
                }
                else if (macroName.equals("Carbohydrates"))
                {
                    BigDecimal sugarsMacroValue = (BigDecimal) dataset.getValue("Sugars Of Carbs");
                    BigDecimal total_Carbs = macroValue.add(sugarsMacroValue);
                    int percent = percent_Calculator(total_Carbs, macrosTotal);
                    
                    return String.format("%s [ %d%% ] - %s g ", macroName, percent, macroValue);
                }
                else if (macroName.equals("Fats"))
                {
                    BigDecimal satFatMacroValue = (BigDecimal) dataset.getValue("Saturated Fats");
                    BigDecimal totalFats = macroValue.add(satFatMacroValue);
                    int percent = percent_Calculator(totalFats, macrosTotal);
                    
                    return String.format("%s [ %d%% ] - %s g ", macroName, percent, macroValue);
                }
                
                //###############################
                // Generic Label
                //###############################
                return String.format("%s - %s g", macroName, macroValue);
            }
            
            @Override
            public AttributedString generateAttributedSectionLabel(PieDataset pieDataset, Comparable comparable)
            {
                return null;
            }
        };
        
        //#################################################################
        // Set Label Generator
        //#################################################################
        plot.setLegendLabelGenerator(labelGen);
    }
}

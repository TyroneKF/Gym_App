package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Total_Meals;

import App_Code.Objects.Graph_Objects.Pie_Chart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.math.BigDecimal;
import java.text.AttributedString;

public class PieChart_Totals extends Pie_Chart
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_Totals(String title, Color[] colors, int frameWidth, int frameHeight, int rotateDelay, Font titleFont,
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
            public String generateSectionLabel(PieDataset dataset, Comparable macroKey)
            {
                //###############################
                // Macro Info
                //###############################
                String
                        macroName = (String) macroKey,
                        space = getSpace();
                
                BigDecimal
                        macroValue = (BigDecimal) dataset.getValue(macroName),
                        macrosTotal = get_DatasetTotal();
                
                //###############################
                // Fats / Carbs / Protein Labels
                //###############################
                if (macroName.equals("Protein"))
                {
                    int percent = percent_Calculator(macroValue, macrosTotal);
                    
                    return String.format("%s%s [ %d%% ] - %s g%s", space, macroName, percent, macroValue, space);
                }
                else if (macroName.equals("Carbohydrates"))
                {
                    BigDecimal sugarsMacroValue = (BigDecimal) dataset.getValue("Sugars Of Carbs");
                    BigDecimal total_Carbs = macroValue.add(sugarsMacroValue);
                    int percent = percent_Calculator(total_Carbs, macrosTotal);
                    
                    return String.format("%s%s [ %d%% ] - %s g", space, macroName, percent, macroValue);
                }
                else if (macroName.equals("Fats"))
                {
                    BigDecimal satFatMacroValue = (BigDecimal) dataset.getValue("Saturated Fats");
                    BigDecimal totalFats = macroValue.add(satFatMacroValue);
                    int percent = percent_Calculator(totalFats, macrosTotal);
                    
                    return String.format("%s%s [ %d%% ] - %s g%s", space, macroName, percent, macroValue, space);
                }
                
                //###############################
                // Generic Label
                //###############################
                return String.format("%s%s - %s g%s", space, macroName, macroValue, space);
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
    
    protected String getSpace()
    {
        return "\u00A0\u00A0";
    }
}

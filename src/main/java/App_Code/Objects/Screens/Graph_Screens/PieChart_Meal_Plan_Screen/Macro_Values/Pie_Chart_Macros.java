package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Graph_Objects.Pie_Chart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.math.BigDecimal;
import java.text.AttributedString;

public class Pie_Chart_Macros extends Pie_Chart
{
    // #################################################################################################################
    // Variables
    // #################################################################################################################
    
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart_Macros(String title, Color[] colors, int frameWidth, int frameHeight, int rotateDelay, Font titleFont,
                            Font labelFont, Font legendFont, DefaultPieDataset<MacroKey> datasetInput)

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
            public String generateSectionLabel(PieDataset dataset, Comparable key)
            {
                //#######################################
                // Generic Label
                //#######################################
                MacroKey macroKey = (MacroKey) key;
                BigDecimal macroValue = (BigDecimal) dataset.getValue(key);
                
                ///#######################################
                // Return Label
                //#######################################
                return String.format(" [%s]  %s  (%d%%) -  %s g  ",
                        macroKey.get_MealTime_GUI(), macroKey.get_MealName(),
                        percent_Calculator(macroValue, get_DatasetTotal()), macroValue);
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



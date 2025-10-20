package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Graph_Objects.Pie_Chart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.math.BigDecimal;
import java.text.AttributedString;

public class Pie_Chart_Macros extends Pie_Chart
{
    // #################################################################################################################
    // Variable
    // #################################################################################################################
    private String macroName, measurementSymbol;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public Pie_Chart_Macros(String macroName, String measurementSymbol, Color[] colors, int frameWidth, int frameHeight, int rotateDelay,
                            Font titleFont, Font labelFont, Font legendFont, DefaultPieDataset<MacroKey> datasetInput)

    {
        //#################################################################
        // Super Constructor & Variables
        //#################################################################
        super(macroName, colors, frameWidth, frameHeight, rotateDelay, titleFont, labelFont, legendFont, datasetInput);
    
        this.macroName = macroName;
        this.measurementSymbol = measurementSymbol;
        
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
                return String.format(" [%s]  %s  (%d%%) -  %s %s ",
                        macroKey.get_MealTime_GUI(), macroKey.get_MealName(),
                        percent_Calculator(macroValue, get_DatasetTotal()), macroValue, macroKey.get_MacroSymbol());
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
    
        //#################################################################
        // Update Title On Initialization
        //#################################################################
        updateChartTitle();
    }
    
    // #################################################################################################################
    // Methods
    // #################################################################################################################
    @Override
    protected void dataset_ActionEvents()
    {
        calculate_Dataset_Total();
        is_PieChart_Empty_MSG();
        reDraw_Legend();
        updateChartTitle();
    }
    
    @Override
    protected void set_Horizontal_LegendAlignment()
    {
        LegendTitle legend = plot.getChart().getLegend();
        legend.setPosition(org.jfree.chart.ui.RectangleEdge.BOTTOM);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
    }
    
    public void updateChartTitle()
    {
        setTitle(String.format(" %s Across Meals  [ %s %s ]", macroName, get_DatasetTotal(), measurementSymbol));
    }
}



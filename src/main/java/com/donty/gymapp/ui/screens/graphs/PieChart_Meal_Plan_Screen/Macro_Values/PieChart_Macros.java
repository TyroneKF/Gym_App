package com.donty.gymapp.ui.screens.graphs.PieChart_Meal_Plan_Screen.Macro_Values;

import com.donty.gymapp.ui.charts.Pie_Chart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import java.awt.*;
import java.math.BigDecimal;
import java.text.AttributedString;

public class PieChart_Macros extends Pie_Chart
{
    // #################################################################################################################
    // Variable
    // #################################################################################################################
    private String macroName, measurementSymbol;
    
    // #################################################################################################################
    // Constructor
    // #################################################################################################################
    public PieChart_Macros(
            
            String macroName,
            String measurementSymbol,
            Color[] colors,
            int frameWidth,
            int frameHeight,
            int rotateDelay,
            Font titleFont,
            Font labelFont,
            Font legendFont,
            DefaultPieDataset<PieChart_MacroKey> datasetInput
    )
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
                PieChart_MacroKey macroKey = (PieChart_MacroKey) key;
                BigDecimal macroValue = (BigDecimal) dataset.getValue(key);
                
                //#######################################
                // Return Label
                //#######################################
                return String.format("\u00A0\u00A0\u00A0[%s]\u00A0%s  (%d%%) -  %s %s\u00A0\u00A0",
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
    protected void dataset_Action_Events()
    {
        calculate_Dataset_Total();
        is_PieChart_Empty_MSG();
        reDraw_Legend();
        updateChartTitle();
    }
    
    @Override
    protected void first_RunTime_Events()
    {
        calculate_Dataset_Total();
        is_PieChart_Empty_MSG();
        reDraw_Legend();
    }
    
    public void updateChartTitle()
    {
        setTitle(String.format(" %s Across Meals  [ %s %s ]", macroName, get_DatasetTotal(), measurementSymbol));
    }
}



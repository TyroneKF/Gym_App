package com.donty.gymapp.ui.screens.mealPlan.macroIndicator;

import java.awt.*;

public class ProgressWheelKey
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private final int percent;

    private final Color used_color;
    private final Color remaining_color;

    private final int width_indicator;
    private final int height_indicator;

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public ProgressWheelKey
    (
            int percent,

            Color used_color,
            Color remaining_color,

            int width_indicator,
            int height_indicator
    )
    {
        this.percent = percent;

        this.height_indicator = height_indicator;
        this.width_indicator = width_indicator;

        this.used_color = used_color;
        this.remaining_color = remaining_color;
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    public Color get_Remaining_Color()
    {
        return remaining_color;
    }

    public Color get_Used_Color()
    {
        return used_color;
    }

    public int get_Height()
    {
        return height_indicator;
    }

    public int get_Width()
    {
        return width_indicator;
    }

    public int get_Percent()
    {
        return percent;
    }
}

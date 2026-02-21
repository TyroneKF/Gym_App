package com.donty.gymapp.ui.screens.mealPlan.macroIndicator;

import java.awt.*;

/**
 * @param percent ################################################################################################################## Variables##################################################################################################################
 */
public record ProgressWheelKey
(
                int percent,
                Color used_color,
                Color remaining_color,
                int width_indicator,
                int height_indicator
)
{
    //##################################################################################################################
    // Constructor
    //##################################################################################################################

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

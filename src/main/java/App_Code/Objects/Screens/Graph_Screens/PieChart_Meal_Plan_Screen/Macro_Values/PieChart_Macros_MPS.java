package App_Code.Objects.Screens.Graph_Screens.PieChart_Meal_Plan_Screen.Macro_Values;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;

public class PieChart_Macros_MPS extends Screen_JFrame
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    
    
    
    //##################################################################################################################
    // Constructors
    //##################################################################################################################
    public PieChart_Macros_MPS(MyJDBC db, Boolean addScrollPane, String title, int frameWidth, int frameHeight, int xPos, int yPos)
    {
        super(db, addScrollPane, title, frameWidth, frameHeight, xPos, yPos);
    }
    
    
    
}

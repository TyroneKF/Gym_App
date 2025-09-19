package App_Code.Objects.Screens.Graph;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Screen;
import App_Code.Objects.Screens.Meal_Plan_Screen;

public class Line_Chart_Meal_Plan_Screen extends Screen
{
    private Meal_Plan_Screen meal_plan_screen;

    public Line_Chart_Meal_Plan_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen)
    {
        super(db, "Line Chart: Plan Macros", 1000, 900, 0,0);
        this.meal_plan_screen = meal_plan_screen;
    }




}

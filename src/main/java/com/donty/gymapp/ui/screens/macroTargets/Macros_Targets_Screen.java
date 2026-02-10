package com.donty.gymapp.ui.screens.macroTargets;

import com.donty.gymapp.domain.enums.db_enums.columnNames.tables.MacroTargetsColumns;
import com.donty.gymapp.gui.controls.combobox.base.Field_JComboBox;
import com.donty.gymapp.gui.controls.textfields.Field_JTxtField_BD;
import com.donty.gymapp.gui.controls.textfields.base.Field_JTxtField_Parent;
import com.donty.gymapp.persistence.Shared_Data_Registry;
import com.donty.gymapp.persistence.database.MyJDBC_Sqlite;
import com.donty.gymapp.persistence.database.statements.Fetch_Statement_Full;
import com.donty.gymapp.persistence.database.statements.Upload_Statement_Full;
import com.donty.gymapp.gui.base.Screen_JFrame;
import com.donty.gymapp.ui.screens.mealPlan.Meal_Plan_Screen;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Macros_Targets_Screen extends Screen_JFrame
{
    // ################################################################################################################
    // Variables
    // ################################################################################################################
    private Integer draft_plan_id;

    private boolean is_form_editable = false;

    // #####################################
    // Objects
    // #####################################
    private Shared_Data_Registry shared_data_registry;
    private Meal_Plan_Screen meal_plan_screen;

    private JButton submission_button;

    // #####################################
    // Collections
    // ####################################
    private LinkedHashMap<String, Macro_Targets_Field_Binding<?>> macro_targets_field_bindings;

    private ArrayList<Object> macros_data;

    // ################################################################################################################
    // Constructor
    // ################################################################################################################
    public Macros_Targets_Screen(
            MyJDBC_Sqlite db,
            Meal_Plan_Screen meal_plan_screen,
            Shared_Data_Registry shared_data_registry
    )
    {
        // ############################################
        // Super Constructors & Variables
        // ############################################
        super(db, false, "Macro-Nutrients Screen", 650, 550, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        set_Resizable(true);

        this.shared_data_registry = shared_data_registry;
        this.meal_plan_screen = meal_plan_screen;
        draft_plan_id = shared_data_registry.get_Selected_Plan_ID();

        // ##########################################
        // Get DB Data
        // ##########################################
        if (! db.get_DB_Connection_Status()) { window_Closed_Event(); return; }

        if (! get_Macro_DB_Data()) { window_Closed_Event(); return; }

        // ##########################################
        // Build GUI
        // ##########################################
        build_Macro_Bindings();

        build_GUI();


        //###########################################################
        //
        //###########################################################
        setFrameVisibility(true);
        makeJFrameVisible();
    }

    // ################################################################################################################
    // Methods
    // ################################################################################################################
    @Override
    public void window_Closed_Event()
    {
        meal_plan_screen.remove_Macros_Target_Screen();
        closeJFrame(); // Destroy Window
    }

    // ###########################################################
    // Build GUI Methods
    // ###########################################################
    private boolean get_Macro_DB_Data()
    {
        String query = """
                SELECT *
                FROM draft_macros_per_pound_and_limits
                WHERE plan_id = ?;""";

        String error_msg = "Unable to retrieve this plans Macro Targets";
        Object[] params = new Object[]{ draft_plan_id };

        try
        {
            Fetch_Statement_Full fetch_statement = new Fetch_Statement_Full(query, params, error_msg);
            macros_data = db.get_2D_Query_AL_Object(fetch_statement, false).getFirst();
            return true;
        }
        catch (Exception _)
        {
            JOptionPane.showMessageDialog(null, "Failed Getting Macro Data");
            return false;
        }
    }

    private void build_Macro_Bindings()
    {
        int digit_Char_Limit = 8;

        macro_targets_field_bindings = new LinkedHashMap<>()
        {{
            put(
                    "kg",
                    new Macro_Targets_Field_Binding<>(
                            "Current Weight (KG)",
                            MacroTargetsColumns.CURRENT_WEIGHT_KG,
                            new Field_JTxtField_BD("Current Weight (KG)", digit_Char_Limit, false),
                            2
                    )
            );

            put(
                    "body_fat",
                    new Macro_Targets_Field_Binding<>(
                            "Body Fat Percentage (%)",
                            MacroTargetsColumns.BODY_FAT_PERCENTAGE,
                            new Field_JTxtField_BD("Body Fat Percentage (%)", digit_Char_Limit, false),
                            4
                    )
            );

            put(
                    "protein",
                    new Macro_Targets_Field_Binding<>(
                            "Protein Per Pound Target",
                            MacroTargetsColumns.PROTEIN_PER_POUND,
                            new Field_JTxtField_BD("Protein Per Pound Target", digit_Char_Limit, false),
                            5
                    )
            );
            put(
                    "carbs",
                    new Macro_Targets_Field_Binding<>(
                            "Carbohydrates Per Pound Target",
                            MacroTargetsColumns.CARBOHYDRATES_PER_POUND,
                            new Field_JTxtField_BD("Carbohydrates Per Pound Target", digit_Char_Limit, false),
                            6
                    )
            );
            put(
                    "fibre",
                    new Macro_Targets_Field_Binding<>(
                            "Fibre Target (G)",
                            MacroTargetsColumns.FIBRE,
                            new Field_JTxtField_BD("Fibre Target (G)", digit_Char_Limit),
                            7
                    )
            );

            put(
                    "fats",
                    new Macro_Targets_Field_Binding<>(
                            "Fats Per Pound Target",
                            MacroTargetsColumns.FATS_PER_POUND,
                            new Field_JTxtField_BD("Fats Per Pound Target", digit_Char_Limit, false),
                            8
                    )
            );

            put(
                    "sat_fat",
                    new Macro_Targets_Field_Binding<>(
                            "Saturated Fat Limit",
                            MacroTargetsColumns.SATURATED_FAT_LIMIT,
                            new Field_JTxtField_BD("Saturated Fat Limit", digit_Char_Limit),
                            9
                    )
            );

            put(
                    "salt",
                    new Macro_Targets_Field_Binding<>(
                            "Salt Limit (G)",
                            MacroTargetsColumns.SALT_LIMIT,
                            new Field_JTxtField_BD("Salt Limit (G)", digit_Char_Limit),
                            10
                    )
            );

            put(
                    "water",
                    new Macro_Targets_Field_Binding<>(
                            "Water Target (Ml)",
                            MacroTargetsColumns.WATER_TARGET,
                            new Field_JTxtField_BD("Water Target (Ml)", digit_Char_Limit),
                            11
                    )
            );

            put(
                    "a_cal",
                    new Macro_Targets_Field_Binding<>(
                            "Additional Calories",
                            MacroTargetsColumns.SALT_LIMIT,
                            new Field_JTxtField_BD("Liquid Target (Ml)", digit_Char_Limit),
                            12
                    )
            );
        }};
    }

    private void build_GUI()
    {
        JPanel mainJPanel = getScrollPaneJPanel();
        mainJPanel.setLayout(new BorderLayout());

        //###########################################################
        // North Section Of GUI
        //###########################################################
        JLabel titleLabel = new JLabel("Set Macro-Nutrient Targets");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.green);
        titlePanel.add(titleLabel);

        mainJPanel.add(titlePanel, BorderLayout.NORTH);

        //###########################################################
        // Centre Section Of GUI : FORM
        //###########################################################
        JPanel inputArea = new JPanel(new GridLayout(macro_targets_field_bindings.size(), 2));

        // for each label it is created into a JLabel
        for (Macro_Targets_Field_Binding<?> target_Binding : macro_targets_field_bindings.values())
        {
            //################################
            // Get Values
            //################################
            String label_txt = target_Binding.get_Gui_Label();

            String cell_data = ((BigDecimal) macros_data.get(target_Binding.get_Field_Query_Pos())).toPlainString();

            Field_JTxtField_Parent<?> txt_field = (Field_JTxtField_Parent<?>) target_Binding.get_Gui_Component();

            System.out.printf("\nData : %s", cell_data);

            //################################
            // JLabel
            //################################
            JLabel label = new JLabel(String.format("    %s:", label_txt));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setFont(new Font("Verdana", Font.BOLD, 14));
            inputArea.add(label);

            //################################
            // JTextField
            //################################
            txt_field.setText(cell_data);   // Set Cell Value

            txt_field.setEditable(false);   // Make Cell not editable
            inputArea.add(txt_field);       // Add to GUI
        }

        mainJPanel.add(inputArea, BorderLayout.CENTER);

        //###########################################################
        // South Section Of GUI : Submission BTN
        //###########################################################
        submission_button = new JButton("Edit Form?");
        submission_button.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submission_button.setPreferredSize(new Dimension(50, 50)); // width, height

        submission_button.addActionListener(ae -> {
            submission_BTN_Action();
        });

        mainJPanel.add(submission_button, BorderLayout.SOUTH);
    }

    // ###########################################################
    // Form Validation Methods
    // ###########################################################
    public void submission_BTN_Action()
    {
        if (! get_Editable_Form())  // if the user hasn't requested to edit the form before
        {
            //########################################
            // Confirm Input
            //#######################################
            int reply = JOptionPane.showConfirmDialog(
                    meal_plan_screen.getFrame(),
                    "Would you To Edit Your Macros?",
                    "Edit Macro Targets",
                    JOptionPane.YES_NO_OPTION
            );

            if (reply != JOptionPane.YES_OPTION) { return; }

            //########################################
            // Make Form Editable
            //#######################################
            set_Edit_Form(true);
            submission_button.setText("Submit Form");

            macro_targets_field_bindings
                    .values()
                    .forEach(e -> ((Field_JTxtField_Parent<?>) e.get_Gui_Component()).setEditable(true));

            return;   // Exit
        }

        //#######################################
        // Validate / Update Form
        //#######################################
        // Moving Cursor To JTextField(1) makes form look editable

        macro_targets_field_bindings
                .values().iterator()
                .next()
                .get_Gui_Component()
                .requestFocusInWindow();


        if (! validate_Form()) { return; } // Error

        if (! areYouSure("Update Macro Values", "\n\nAre you sure you want to update your current 'Macro Values'?"))
        {
            return;
        }

        if (! update_Form()) { return; } // Error

        meal_plan_screen.update_Targets_And_Macros_Left_Table();
        meal_plan_screen.set_Has_Macros_Targets_Changed(true);

        window_Closed_Event(); // Trigger Events to call on exit
    }

    public boolean validate_Form()
    {
        LinkedHashMap<String, ArrayList<String>> error_Map = new LinkedHashMap<>();

        //###############################
        // Get Error MSGs from Components
        //###############################
        for (Macro_Targets_Field_Binding<?> field_Binding : macro_targets_field_bindings.values())
        {
            switch (field_Binding.get_Gui_Component())
            {
                case Field_JComboBox<?> jc -> jc.validation_Check(error_Map);
                case Field_JTxtField_Parent<?> jt -> jt.validation_Check(error_Map);
                default -> throw new IllegalStateException("Unexpected value: " + field_Binding.get_Gui_Component());
            }
        }

        //###############################
        // IF no errors returns True
        //###############################
        if (error_Map.isEmpty()) { return true; }

        //###############################
        // Display Errors / Output
        //###############################
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(null, build_Error_MSg(error_Map), "Ingredients Form Error Messages", JOptionPane.INFORMATION_MESSAGE);

        return false;
    }

    private String build_Error_MSg(LinkedHashMap<String, ArrayList<String>> error_Map)
    {
        //###############################
        // Build Error MSGs
        //###############################
        /*
         * HTML:
         * &nbsp; = space
         * <br> = line break
         * <b></b> = bold
         */

        StringBuilder error_MSG_String = new StringBuilder("<html>");

        for (Map.Entry<String, ArrayList<String>> error_Entry : error_Map.entrySet())
        {
            ArrayList<String> error_MSGs = error_Entry.getValue();

            // Singular Error MSG
            if (error_MSGs.size() == 1)
            {
                error_MSG_String.append(String.format("<br><br><b>%s&nbsp;:&nbsp;</b> %s", error_Entry.getKey(), error_MSGs.getFirst()));
                continue;
            }

            // Multiple Error Messages
            error_MSG_String.append(String.format("<br><br><b>%s:</b>", error_Entry.getKey()));

            for (String error : error_MSGs)
            {
                error_MSG_String.append(String.format("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>.</b>&nbsp; %s", error));
            }
        }

        error_MSG_String.append("<br><br></html>");

        //###############################
        // Return Output
        //###############################
        return error_MSG_String.toString();
    }

    public boolean update_Form()
    {
        // ##############################################
        // Create SQL Query
        // ##############################################
        String error_msg = "Error, changing Macro Targets!";
        String update_Query;
        Object[] params;
        try
        {
            Field_JTxtField_BD kg_textfield = (Field_JTxtField_BD) macro_targets_field_bindings.get("kg").get_Gui_Component();

            BigDecimal weight_kg = kg_textfield.get_Text_Casted_To_Type();
            BigDecimal weight_in_pounds = weight_kg.multiply(new BigDecimal("2.2")).setScale(2, RoundingMode.HALF_UP);

            update_Query = """
                UPDATE draft_macros_per_pound_and_limits
                SET
                    current_weight_kg = ?,
                    current_weight_in_pounds = ?,
            
                    body_fat_percentage = ?,
                    protein_per_pound = ?,
                    carbohydrates_per_pound = ?,
                    fibre = ?,
                    fats_per_pound = ?,
                    saturated_fat_limit = ?,
                    salt_limit = ?,
                    water_target = ?,
                    additional_calories = ?
                
                WHERE plan_id = ? ;""";

            params = new Object[]{

                    weight_kg,
                    weight_in_pounds,

                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("body_fat").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("protein").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("carbs").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("fibre").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("fats").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("sat_fat").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("salt").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("water").get_Gui_Component()).get_Text_Casted_To_Type(),
                    ((Field_JTxtField_BD) macro_targets_field_bindings.get("a_cal").get_Gui_Component()).get_Text_Casted_To_Type(),

                    shared_data_registry.get_Selected_Plan_ID()

            };
        }
        catch (Exception e)
        {
            System.err.printf("\n\n%s \n%s", get_Class_And_Method_Name(), e);
            return false;
        }

        // ##############################################
        // Execute Query
        // ##############################################
        Upload_Statement_Full sql_statement = new Upload_Statement_Full(update_Query, params, error_msg, true);

        if (! (db.upload_Data(sql_statement))) { return false; }

        // ##############################################
        // Return Result
        // ##############################################
        // Output MSG & Update Screens
        JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Macro Targets Successfully Updated In DB");
        meal_plan_screen.update_Targets_And_Macros_Left_Table();

        return true;
    }

    // #################################################################################################################
    // Accessor / Mutator Methods
    // #################################################################################################################
    public void set_Edit_Form(boolean editable)
    {
        is_form_editable = editable;
    }

    public boolean get_Editable_Form()
    {
        return is_form_editable;
    }
}

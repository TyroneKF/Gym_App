package App_Code.Objects.Screens;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Gui_Objects.Screen;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Macros_Targets_Screen extends Screen
{
    // ################################################################################################################
    // Variables
    // ################################################################################################################
    private Integer temp_PlanID, planID;
    private String planName;
    private Meal_Plan_Screen meal_plan_screen;

    private boolean formEditable = false;
    private JButton submitButton;

    private ArrayList<JTextField> listOfTextFields = new ArrayList<>();
    private static final String[] labels = { "Selected Plan Name:", "Current Weight (KG):", "Body Fat Percentage (%):",
            "Protein Per Pound Target", "Carbohydrates Per Pound Target:", "Fibre Target (G):", "Fats Per Pound Target:",
            "Saturated Fat Limit:", "Salt Limit (G):", "Water Target (Ml):", "Liquid Target (Ml)", "Additional Calories:" };

    private ArrayList<String> macrosData;
    private String tableName = "macros_per_pound_and_limits";

    // ################################################################################################################
    // Constructor
    // ################################################################################################################
    public Macros_Targets_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen, int planID, int temp_PlanID, String planName)
    {
        // #############################################################################################################
        // Super Constructors & Variables
        // #############################################################################################################
        super(db, false, "Macro-Nutrients Screen", 650, 550, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        setResizable(true);

        // ##########################################
        // Variables
        // ##########################################
        if (! db.get_DB_Connection_Status()) { windowClosedEvent(); return; }

        // ##########################################
        // Variables
        // ##########################################
        this.meal_plan_screen = meal_plan_screen;
        this.planID = planID;
        this.temp_PlanID = temp_PlanID;
        this.planName = planName;

        JPanel mainJPanel = getScrollPaneJPanel();

        //##############################################################################################################
        // Get DB Target Info for this Plan
        //##############################################################################################################
        String query = String.format("""
                SELECT                                
                plan_id, current_weight_kg, body_fat_percentage, protein_per_pound, carbohydrates_per_pound, fibre, 
                fats_per_pound, saturated_fat_limit, salt_limit, water_target, liquid_target, additional_calories                                
                FROM %s                                
                WHERE plan_id = %s;""", tableName, temp_PlanID);

        ArrayList<ArrayList<String>> data = db.getMultiColumnQuery(query);

        if (data == null)
        {
            JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "\n\nUnable to retrieve current plan Macros Data!");
            return;
        }

        macrosData = data.get(0);

        //##############################################################################################################
        // GUI Set-Up
        //##############################################################################################################
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
        JPanel inputArea = new JPanel(new GridLayout(labels.length, 2));

        // for each label it is created into a JLabel
        for (int i = 0; i < labels.length; i++)
        {
            //################################
            // JLabel
            //################################
            String labelTXT = labels[i];

            JLabel label = new JLabel("    " + labelTXT);
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setFont(new Font("Verdana", Font.BOLD, 14));
            inputArea.add(label);

            //################################
            // JTextField
            //################################
            JTextField textField = new JTextField("");

            //Setting TextField limits
            if (labelTXT.equals("Selected Plan Name:"))
            {
                textField.setDocument(new JTextFieldLimit(100));
                textField.setText(String.format(" %s", planName));
                textField.setEditable(false);
            }
            else
            {
                textField.setDocument(new JTextFieldLimit(9));
                textField.setText(String.format(" %s", macrosData.get(i)));
            }

            textField.setEditable(false);
            listOfTextFields.add(textField);
            inputArea.add(textField);
        }

        mainJPanel.add(inputArea, BorderLayout.CENTER);

        //###########################################################
        // South Section Of GUI : Submission BTN
        //###########################################################
        submitButton = new JButton("Edit Form?");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

        submitButton.addActionListener(ae -> {
            submissionBTN_Action();
        });

        mainJPanel.add(submitButton, BorderLayout.SOUTH);

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
    protected void windowClosedEvent()
    {
        meal_plan_screen.remove_macrosTargets_Screen();
        closeJFrame(); // Destroy Window
    }

    // ###########################################################
    // Form Methods
    // ##########################################################
    public void submissionBTN_Action()
    {
        if (! getEditableForm())  // if the user hasn't requested to edit the form before
        {
            //########################################
            // Confirm Input
            //#######################################
            int reply = JOptionPane.showConfirmDialog(meal_plan_screen.getFrame(), String.format("Would you To Edit Your Macros?"),
                    "Edit Macro Targets", JOptionPane.YES_NO_OPTION);

            if (reply != JOptionPane.YES_OPTION) { return; }

            //########################################
            // Make Form Editable
            //#######################################
            setEditForm(true);
            submitButton.setText("Submit Form");

            for (int pos = 0; pos < listOfTextFields.size(); pos++)
            {
                JTextField jTextField = listOfTextFields.get(pos);

                if ((labels[pos].equals("Selected Plan Name:"))) { continue; }

                jTextField.setEditable(true);
            }

            //#######################################
            // Exit
            //#######################################
            return;
        }

        //#######################################
        // Validate / Update Form
        //#######################################
        listOfTextFields.get(1).requestFocusInWindow(); // Moving Cursor To Jtextfield(1) makes form look editable

        if (! validateForm()) { return; } // Error

        if (! areYouSure("Update Macro Values", "\n\nAre you sure you want to update your current 'Macro Values'?")) { return; }

        if (! updateForm()) { return; } // Error

        meal_plan_screen.update_Targets_And_MacrosLeftTables();
        meal_plan_screen.macrosTargetsChanged(true);

        windowClosedEvent(); // Trigger Events to call on exit
    }

    public boolean validateForm()
    {
        // ##############################################
        // Processing User Input
        // ##############################################
        Boolean hasDataChanged = false;
        String errorTxt = "";
        BigDecimal zero = new BigDecimal(0);


        for (int row = 0; row < listOfTextFields.size(); row++)
        {
            if (row == 0) { continue; } // IF row = PlanName skip processing

            String value = listOfTextFields.get(row).getText().trim();  // Gather User Input In TextField
            String labelName = labels[row];

            if (value.equals("")) // If the users input was empty
            {
                errorTxt += String.format("\n\n' %s ' on Row: %s,  must have a value which is not ' NULL '!", labelName, row + 1);
                continue;
            }

            // Decimal Converting
            try
            {
                BigDecimal bd_User_Input = new BigDecimal(value);
                BigDecimal dbRowData_BD_Form = new BigDecimal(macrosData.get(row));

                if (bd_User_Input.compareTo(zero) < 0 || bd_User_Input.compareTo(zero) == 0) // decimal less than 0
                {
                    if (bd_User_Input.compareTo(zero) == 0 && labelName.equals("Additional Calories:") || labelName.equals("Liquid Target (Ml)"))
                    {
                        continue;
                    }
                    errorTxt += String.format("\n\n' %s ' on Row: %s,  must have a value which is bigger than 0 !", labels[row], row + 1);
                }

                if (bd_User_Input.compareTo(dbRowData_BD_Form) != 0) { hasDataChanged = true; } // if they're not equal
            }
            catch (Exception e)
            {
                errorTxt += String.format("\n\n' %s 'on Row: %s, must have a value which is a ' Decimal(8,2) ' !'", labels[row], row + 1);
            }
        }

        // ##############################################
        // Error: Same DATA
        // ##############################################
        if (! hasDataChanged)
        {
            String txt = "\nMacroTarget haven't changed!\nDo you want exit this screen instead?";
            if (areYouSure("Exit Screen",txt)) { windowClosedEvent(); }
            return false;
        }

        // ##############################################
        // No Error: Exit
        // ##############################################
        if (errorTxt.length() == 0) { System.out.printf("\n\nNo Error"); return true; }

        // ##############################################
        // Error Found:  MSG OUTPUT
        // ##############################################
        String txt = String.format("""
                \n\nAll the input rows must have a value which is a ' Decimal(8,2) ' !" +
                                
                                
                Please fix the following rows being; \n%s""", errorTxt);

        JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), txt);
        return false;
    }

    public boolean updateForm()
    {
        // ##############################################
        // SQL Update MSG
        // ##############################################
        String updateTargets_Query = String.format("""
                        UPDATE macros_per_pound_and_limits
                        SET 
                        date_time_of_creation = now(), current_weight_kg = %s , body_fat_percentage = %s, protein_per_pound = %s, 
                        carbohydrates_per_pound = %s, fibre =%s, fats_per_pound = %s, saturated_fat_limit = %s,  salt_limit = %s, 
                        water_target = %s, liquid_target = %s, additional_calories = %s 
                        WHERE plan_id = %s;""",

                listOfTextFields.get(1).getText().trim(), listOfTextFields.get(2).getText().trim(), listOfTextFields.get(3).getText().trim(),
                listOfTextFields.get(4).getText().trim(), listOfTextFields.get(5).getText().trim(), listOfTextFields.get(6).getText().trim(),
                listOfTextFields.get(7).getText().trim(), listOfTextFields.get(8).getText().trim(), listOfTextFields.get(9).getText().trim(),
                listOfTextFields.get(10).getText().trim(), listOfTextFields.get(11).getText().trim(), temp_PlanID);

        System.out.printf("\n\nQuery: \n\n%s", updateTargets_Query);

        // ##############################################
        // Execute Query
        // ##############################################
        if (! (db.uploadData_Batch_Altogether(new String[]{ updateTargets_Query })))
        {
            JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Un-able to Update Macro Targets In DB");
            return false;
        }

        JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Macro Targets Successfully Updated In DB");
        meal_plan_screen.update_Targets_And_MacrosLeftTables();
        return true;
    }

    public void setEditForm(boolean editable)
    {
        formEditable = editable;
    }

    public boolean getEditableForm()
    {
        return formEditable;
    }
}

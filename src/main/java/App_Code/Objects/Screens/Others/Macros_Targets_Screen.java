package App_Code.Objects.Screens.Others;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.Text_Fields.JTextFieldLimit;
import App_Code.Objects.Gui_Objects.Screens.Screen_JFrame;
import App_Code.Objects.Screens.Meal_Plan_Screen;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Macros_Targets_Screen extends Screen_JFrame
{
    // ################################################################################################################
    // Variables
    // ################################################################################################################
    private Integer temp_PlanID;
    private Meal_Plan_Screen meal_plan_screen;
    
    private boolean formEditable = false;
    private JButton submitButton;
    
    private ArrayList<JTextField> listOfTextFields = new ArrayList<>();
    private static final String[] labels = { "Selected Plan Name", "Creation Date", "Current Weight (KG)", "Body Fat Percentage (%)",
            "Protein Per Pound Target", "Carbohydrates Per Pound Target", "Fibre Target (G)", "Fats Per Pound Target",
            "Saturated Fat Limit", "Salt Limit (G)", "Water Target (Ml)", "Liquid Target (Ml)", "Additional Calories" };
    
    private ArrayList<String> not_Editable_Columns = new ArrayList<>(Arrays.asList("Selected Plan Name", "Creation Date"));
    
    private ArrayList<Object> macrosData;
    
    private String creation_Date;
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // ################################################################################################################
    // Constructor
    // ################################################################################################################
    public Macros_Targets_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen, int temp_PlanID, String planName)
    {
        // #############################################################################################################
        // Super Constructors & Variables
        // #############################################################################################################
        super(db, false, "Macro-Nutrients Screen", 650, 550, 0, 0);
        getScrollPaneJPanel().setBackground(Color.WHITE);
        set_Resizable(true);
        
        // ##########################################
        // Variables
        // ##########################################
        if (! db.get_DB_Connection_Status()) { window_Closed_Event(); return; }
        
        // ##########################################
        // Variables
        // ##########################################
        this.meal_plan_screen = meal_plan_screen;
        this.temp_PlanID = temp_PlanID;
        
        JPanel mainJPanel = getScrollPaneJPanel();
        
        //##############################################################################################################
        // Get DB Target Info for this Plan
        //##############################################################################################################
        String
                query_Target = """
                SELECT * FROM macros_per_pound_and_limits
                WHERE plan_id = ?
                AND date_time_of_creation = (Select Max(date_time_of_creation) FROM macros_per_pound_and_limits WHERE plan_id = ?);""",
                
                errorMSG_Target = "Unable to retrieve this plans Macro Targets";
        
        Object[] params_Target = new Object[]{ temp_PlanID, temp_PlanID };
        
        ArrayList<ArrayList<Object>> data = db.get_2D_Query_AL_Object(query_Target,params_Target, errorMSG_Target);
        
        if (data == null)
        {
            JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), errorMSG_Target);
            return;
        }
        
        macrosData = data.getFirst();
        
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
            
            JLabel label = new JLabel(String.format("    %s:", labelTXT));
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setFont(new Font("Verdana", Font.BOLD, 14));
            inputArea.add(label);
            
            //################################
            // JTextField
            //################################
            JTextField textField = new JTextField("");
            Object cellData = macrosData.get(i);
            
            //Setting TextField limits
            if (labelTXT.equals("Selected Plan Name"))
            {
                textField.setDocument(new JTextFieldLimit(100));
                textField.setText(String.format(" %s", planName));
            }
            else
            {
                if (labelTXT.equals("Creation Date"))
                {
                    textField.setDocument(new JTextFieldLimit(30));
                    creation_Date = cellData.toString();
                    
                    textField.setText(creation_Date.replaceAll("-", "/").replaceAll("T", "   "));
                }
                else
                {
                    textField.setDocument(new JTextFieldLimit(9));
                    textField.setText(String.format(" %s", cellData));
                }
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
    public void window_Closed_Event()
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
                
                if (not_Editable_Columns.contains((labels[pos]))) { continue; }
                
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
        listOfTextFields.get(2).requestFocusInWindow(); // Moving Cursor To Jtextfield(1) makes form look editable
        
        if (! validateForm()) { return; } // Error
        
        if (! areYouSure("Update Macro Values", "\n\nAre you sure you want to update your current 'Macro Values'?"))
        {
            return;
        }
        
        if (! updateForm()) { return; } // Error
        
        meal_plan_screen.update_Targets_And_MacrosLeftTables();
        meal_plan_screen.macrosTargetsChanged(true);
        
        window_Closed_Event(); // Trigger Events to call on exit
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
            String value = listOfTextFields.get(row).getText().trim();  // Gather User Input In TextField
            String labelName = labels[row];
            
            if (not_Editable_Columns.contains(labelName)) { continue; } // IF row = PlanName skip processing
            
            if (value.isEmpty()) // If the users input was empty
            {
                errorTxt += String.format("\n\n' %s ' on Row: %s,  must have a value which is not ' NULL '!", labelName, row + 1);
                continue;
            }
            
            // Decimal Converting
            try
            {
                BigDecimal bd_User_Input = new BigDecimal(value);
                BigDecimal dbRowData_BD_Form = (BigDecimal) macrosData.get(row);
                
                if (bd_User_Input.compareTo(zero) < 0 || bd_User_Input.compareTo(zero) == 0) // decimal less than 0
                {
                    if (bd_User_Input.compareTo(zero) == 0 && labelName.equals("Additional Calories") || labelName.equals("Liquid Target (Ml)"))
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
            if (areYouSure("Exit Screen", txt)) { window_Closed_Event(); }
            return false;
        }
        
        // ##############################################
        // No Error: Exit
        // ##############################################
        if (errorTxt.isEmpty()) { System.out.printf("\n\nNo Error"); return true; }
        
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
        // Variables
        // ##############################################
        BigDecimal weightKG = new BigDecimal(listOfTextFields.get(2).getText().trim());
        BigDecimal weightInPounds = weightKG.multiply(new BigDecimal("2.2")).setScale(2, RoundingMode.HALF_UP);
        
        // ##############################################
        // Create SQL Query
        // ##############################################
        Object[] options = { "Update Existing Macro", "Create New Macro Target", "Cancel" };
        
        String update_Query = "";
        Object[] params;
        
        int choice = JOptionPane.showOptionDialog(
                null, // parent component (null = center of screen)
                "How would you like to change your Macro Targets?",
                "Confirm Action",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, // icon
                options, // custom button texts
                options[0] // default option
        );
        
        if (choice == 0)// "Update Existing Macro"
        {
            update_Query = """
                    UPDATE macros_per_pound_and_limits
                    SET
                    date_time_of_creation = now(),
                    current_weight_kg = ?, current_weight_in_pounds = ?, body_fat_percentage = ?,
                    protein_per_pound = ?, carbohydrates_per_pound = ?, fibre =?, fats_per_pound = ?,
                    saturated_fat_limit = ?, salt_limit = ?, water_target = ?, liquid_target = ?,
                    additional_calories = ?
                    WHERE plan_id = ? AND date_time_of_creation = ?;""";
            
            params = new Object[]{
                    weightKG, weightInPounds, new BigDecimal(listOfTextFields.get(3).getText().trim()),
                    new BigDecimal(listOfTextFields.get(4).getText().trim()), new BigDecimal(listOfTextFields.get(5).getText().trim()),
                    new BigDecimal(listOfTextFields.get(6).getText().trim()), new BigDecimal(listOfTextFields.get(7).getText().trim()),
                    new BigDecimal(listOfTextFields.get(8).getText().trim()), new BigDecimal(listOfTextFields.get(9).getText().trim()),
                    new BigDecimal(listOfTextFields.get(10).getText().trim()), new BigDecimal(listOfTextFields.get(11).getText().trim()),
                    new BigDecimal(listOfTextFields.get(12).getText().trim()), temp_PlanID,
                    Timestamp.valueOf(LocalDateTime.parse(creation_Date, formatter)) // Convert to LocalDate & SQLTimeStamp
            };
        }
        else if (choice == 1)// "Replace Existing Macro"
        {
            update_Query = """
                    INSERT INTO macros_per_pound_and_limits
                    (
                    	plan_id , date_time_of_creation,
                    	current_weight_kg, current_weight_in_pounds,
                    	body_fat_percentage, protein_per_pound, carbohydrates_per_pound, fibre,
                    	fats_per_pound, saturated_fat_limit, salt_limit, water_target, liquid_target,
                    	additional_calories
                    )
                    VALUES
                    (?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);""";
            
            params = new Object[]{
                    temp_PlanID, weightKG, weightInPounds, new BigDecimal(listOfTextFields.get(3).getText().trim()),
                    new BigDecimal(listOfTextFields.get(4).getText().trim()), new BigDecimal(listOfTextFields.get(5).getText().trim()),
                    new BigDecimal(listOfTextFields.get(6).getText().trim()), new BigDecimal(listOfTextFields.get(7).getText().trim()),
                    new BigDecimal(listOfTextFields.get(8).getText().trim()), new BigDecimal(listOfTextFields.get(9).getText().trim()),
                    new BigDecimal(listOfTextFields.get(10).getText().trim()), new BigDecimal(listOfTextFields.get(11).getText().trim()),
                    new BigDecimal(listOfTextFields.get(12).getText().trim())
            };
        }
        else
        {
            return false;
        }
        
        // ##############################################
        // Execute Query
        // ##############################################
        if (! (db.upload_Data(update_Query, params, "Error, changing Macro Targets!"))) { return false; }
        
        // ##############################################
        // Output MSG & Update Screens
        // ##############################################
        JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Macro Targets Successfully Updated In DB");
        meal_plan_screen.update_Targets_And_MacrosLeftTables();
        
        // ##############################################
        // Return Result
        // ##############################################
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

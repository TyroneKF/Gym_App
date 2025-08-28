package App_Code.Objects.Screens.Others;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Macros_Targets_Screen extends JFrame
{
    private MyJDBC db;
    private Integer temp_PlanID, planID;
    private String planName;
    private Meal_Plan_Screen meal_plan_screen;

    private boolean formEditable = false;

    private ArrayList<JTextField> listOfTextFields = new ArrayList<>();
    private static final String[] labels = {"Selected Plan Name:", "Current Weight (KG):", "Body Fat Percentage (%):",
            "Protein Per Pound Target", "Carbohydrates Per Pound Target:", "Fibre Target (G):", "Fats Per Pound Target:",
            "Saturated Fat Limit:", "Salt Limit (G):", "Water Target (Ml):", "Liquid Target (Ml)", "Additional Calories:"};

    public Macros_Targets_Screen(MyJDBC db, Meal_Plan_Screen meal_plan_screen, int planID, int temp_PlanID, String planName)
    {
        this.db = db;
        this.meal_plan_screen = meal_plan_screen;

        this.planID = planID;
        this.temp_PlanID = temp_PlanID;

        this.planName = planName;
        try
        {
            if (db.get_DB_Connection_Status())
            {
                //###########################################################
                // Get Current Targets For This Plan DB Info
                //###########################################################
                String query = String.format("""
                       Select plan_id, current_weight_kg, body_fat_percentage, protein_per_pound, carbohydrates_per_pound, fibre, 
                       fats_per_pound, saturated_fat_limit, salt_limit, water_target, liquid_target, additional_calories
                       FROM macros_Per_Pound_And_Limits 
                       WHERE plan_id = %s;""", temp_PlanID);

                ArrayList<ArrayList<String>> data = db.getMultiColumnQuery(query);
                if(data == null)
                {
                    JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "\n\nUnable to retrieve current plan Macros Data!");
                    return;
                }

                ArrayList<String> macrosData = data.get(0);
                //###########################################################
                // Frame Set-Up
                //###########################################################

                setTitle("Macro-Nutrients Screen");
                makeJframeVisible();

                setLocationRelativeTo(null);
                setVisible(true);

                //Delete all temp data on close
                addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override //HELLO Causes Error
                    public void windowClosing(WindowEvent windowEvent)
                    {
                        closeWindowEvent();
                    }
                });

                JPanel mainJPanel = new JPanel(new BorderLayout());

                //###########################################################
                // North Frame
                //###########################################################

                JLabel titleLabel = new JLabel("Set Macro-Nutrient Targets");
                titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
                titleLabel.setHorizontalAlignment(JLabel.CENTER);

                JPanel titlePanel = new JPanel();
                titlePanel.setBackground(Color.green);
                titlePanel.add(titleLabel);

                mainJPanel.add(titlePanel, BorderLayout.NORTH);

                //###########################################################
                // Centre Frame
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
                // South Frame
                //###########################################################

                // Creating submit button
                JButton submitButton = new JButton("Edit Form?");
                submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
                submitButton.setPreferredSize(new Dimension(50, 50)); // width, height


                // creating commands for submit button to execute on
                submitButton.addActionListener(ae -> {

                    if (!getEditableForm())
                    {
                        int reply = JOptionPane.showConfirmDialog(meal_plan_screen.getFrame(), String.format("Would you To Edit Your Macros?"),
                                "Edit Macro Targets", JOptionPane.YES_NO_OPTION); //HELLO Edit

                        if (reply==JOptionPane.YES_OPTION)
                        {
                            setEditForm(true);
                            submitButton.setText("Submit Form");

                            //########################################
                            //Making textfields inside form editable
                            //#######################################
                            int pos = 0;
                            for (JTextField jTextField : listOfTextFields)
                            {
                                if(!(labels[pos].equals("Selected Plan Name:")))
                                {
                                    jTextField.setEditable(true);
                                }
                                pos++;
                            }
                            //#######################################

                            // Moving Cursor To Jtextfield(1) makes form look editable
                            listOfTextFields.get(1).requestFocusInWindow();
                        }
                        return;
                    }
                    else if (validateForm())
                    {
                        if (updateForm())
                        {
                            closeWindowEvent();
                            meal_plan_screen.updateTargetsAndMacrosLeft();
                            meal_plan_screen.macrosTargetsChanged(true);
                            closeeWindow();
                        }
                    }
                });

                mainJPanel.add(submitButton, BorderLayout.SOUTH);
                //###########################################################

                add(mainJPanel);
                pack();
            }
        }
        catch (Exception e)
        {

        }
    }

    public void closeeWindow()
    {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void closeWindowEvent()
    {
        meal_plan_screen.remove_macrosTargets_Screen();
    }

    public void makeJframeVisible()
    {
        setExtendedState(JFrame.NORMAL);
        setPreferredSize(new Dimension(650, 550));
        setLocation(1000, 0);
        setResizable(false);
    }

    public boolean validateForm()
    {
        if (temp_PlanID==null && planID==null && planName==null)
        {
            JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Please Select A Plan First!");
            return false;
        }

        String errorTxt = "";
        BigDecimal zero = new BigDecimal(0);

        for (int row = 0; row < listOfTextFields.size(); row++)
        {
            if (row==0)
            {
                continue;
            }

            String value = listOfTextFields.get(row).getText().trim();

            if (value.equals(""))
            {
                errorTxt += String.format("\n\n' %s ' on Row: %s,  must have a value which is not ' NULL '!", labels[row], row + 1);
                continue;
            }
            try
            {
                BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                if (bdFromString.compareTo(zero) < 0 || bdFromString.compareTo(zero)==0) // decimal less than 0
                {
                    if (bdFromString.compareTo(zero)==0 && labels[row].equals("Additional Calories:"))
                    {
                        continue;
                    }
                    errorTxt += String.format("\n\n' %s ' on Row: %s,  must have a value which is bigger than 0 !", labels[row], row + 1);
                }
            }
            catch (Exception e)
            {
                errorTxt += String.format("\n\n' %s 'on Row: %s, must have a value which is a ' Decimal(8,2) ' !'", labels[row], row + 1);
            }
        }

        if (errorTxt.length()==0)
        {
            System.out.printf("\n\nNo Error");
            return true;
        }

        JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), String.format("\n\nAll the input rows must have a value which is a ' Decimal(8,2) ' !" +
                "\n\nPlease fix the following rows being; \n%s", errorTxt));

        return false;
    }

    public boolean updateForm()
    {
        String updateTargets_Query = String.format("""
                        UPDATE macros_Per_Pound_And_Limits
                                      
                        SET date_time_of_creation = now(), current_weight_kg = %s , body_fat_percentage = %s, protein_per_pound = %s, 
                        carbohydrates_per_pound = %s, fibre =%s, fats_per_pound = %s, saturated_fat_limit = %s,  salt_limit = %s, 
                        water_target = %s, liquid_target = %s, additional_calories = %s WHERE plan_id =  %s;""",

                listOfTextFields.get(1).getText().trim(), listOfTextFields.get(2).getText().trim(), listOfTextFields.get(3).getText().trim(),
                listOfTextFields.get(4).getText().trim(), listOfTextFields.get(5).getText().trim(), listOfTextFields.get(6).getText().trim(),
                listOfTextFields.get(7).getText().trim(),listOfTextFields.get(8).getText().trim(), listOfTextFields.get(9).getText().trim(),
                listOfTextFields.get(10).getText().trim(),listOfTextFields.get(11).getText().trim(), temp_PlanID);

        System.out.printf("\n\nQuery: \n\n%s", updateTargets_Query);

        if (!(db.uploadData_Batch_Altogether(new String[]{updateTargets_Query})))
        {
            JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Un-able to Update Macro Targets In DB");
            return false;
        }

        JOptionPane.showMessageDialog(meal_plan_screen.getFrame(), "Macro Targets Successfully Updated In DB");
        meal_plan_screen.updateTargetsAndMacrosLeft();
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

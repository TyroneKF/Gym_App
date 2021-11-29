package App_Code.Objects.Screens.Add_Ingredients_Screen;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Screens.Meal_Plan_Screen.MealPlanScreen2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Add_Ingredients_Screen extends JFrame {
    private GridBagConstraints gbc = new GridBagConstraints();

    private MyJDBC db;
    private Integer temp_PlanID, planID;
    private String planName;
    private MealPlanScreen2 gui;

    private boolean formEditable = false;

    private static final String[] labels = {"Ingredient Measurement In:", "Ingredient Name:", "Ingredient_Type:", "Based_On_Quantity:", "Protein:",
            "Carbohydrates:", "Sugars Of Carbs:", "Fibre:", "Fat:", "Saturated Fat:", "Salt:", "Water_Content:", "Calories:"};

    private int ingredientNameLabelIndex = 0;

    // Form Objects
    private JComboBox ingredientsMeasure_JComboBox = new JComboBox(), ingredientsType_JComboBox = new JComboBox();


    private ArrayList<Component> formObjects = new ArrayList<>();


    int jFramewidth = 650, jFrameheight = 650;

    public Add_Ingredients_Screen(MyJDBC db, MealPlanScreen2 gui, int planID, int temp_PlanID, String planName) {
        this.db = db;
        this.gui = gui;

        this.planID = planID;
        this.temp_PlanID = temp_PlanID;

        this.planName = planName;
        try {
            if (db.isDatabaseConnected()) {

                //################################################################
                // Frame Set-Up
                //################################################################

                setTitle("Add/Edit Ingredients Screen");
                makeJframeVisible();

                setLocationRelativeTo(null);
                setLayout(new BorderLayout());
                setVisible(true);

                //Delete all temp data on close
                addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override //HELLO Causes Error
                    public void windowClosing(WindowEvent windowEvent) {
                        closeWindowEvent();
                    }
                });

                CollapsibleJPanel ingredinetsInfo_CJP = new CollapsibleJPanel(this, "Add Ingredinets Info", 250, 50);
                JPanel mainJPanel = ingredinetsInfo_CJP.getCentreJPanel();
                mainJPanel.setLayout(new BorderLayout());

                //###################################################################
                // North Frame
                //###################################################################

                // Creating North JPanel Area with 2 rows
                JPanel northPanel = new JPanel(new GridBagLayout());
                mainJPanel.add(northPanel, BorderLayout.NORTH);

                //#####################################################
                // Creating area for North JPanel (title area)
                //#####################################################
                JLabel titleLabel = new JLabel("Add Ingredient");
                titleLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
                titleLabel.setHorizontalAlignment(JLabel.CENTER);

                JPanel titlePanel = new JPanel();
                titlePanel.setBackground(Color.green);
                titlePanel.add(titleLabel);

                // Add title JPanel to North Panel Area
                addToContainer(northPanel, titlePanel, 0, 2, 1, 1, 0.25, 0.25, "both", 0, 0);


                //#####################################################
                // Creating area for North JPanel (Refresh Icon)
                //#####################################################

                JPanel iconArea = new JPanel(new GridBagLayout());
                addToContainer(northPanel, iconArea, 0, 1, 1, 1, 0.25, 0.25, "both", 0, 0);

                IconPanel iconPanel = new IconPanel(1, 10, "East");
                JPanel iconPanelInsert = iconPanel.getIconJpanel();

                addToContainer(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

                //##########################
                // Refresh Icon
                //##########################
                int width = 30;
                int height = 30;

                IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/++refresh.png", "", width, height, width, height,
                        "centre", "right"); // btn text is useless here , refactor

                JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
                refresh_Icon_Btn.makeBTntransparent();

                refresh_Btn.addActionListener(ae -> {

                    for (int i = 0; i < formObjects.size(); i++) {
                        Component comp = formObjects.get(i);

                        if (comp instanceof JComboBox) {
                            ((JComboBox<?>) comp).setSelectedIndex(-1);
                        } else if (comp instanceof JTextField) {
                            ((JTextField) comp).setText("");
                        }
                    }
                });

                iconPanelInsert.add(refresh_Icon_Btn);

                //#################################################################
                // Centre Frame
                //#################################################################
                JPanel inputArea = new JPanel(new GridLayout(labels.length, 2));

                // for each label it is created into a JLabel
                for (int i = 0; i < labels.length; i++) {
                    boolean jcomboxBeingCreated = false;

                    Object formObject = null;

                    JTextField textField = new JTextField("");
                    JComboBox comboBox = null;

                    //#########################################
                    // JLabel
                    //#########################################

                    String labelTXT = labels[i];

                    JLabel label = new JLabel("    " + labelTXT);
                    label.setHorizontalAlignment(JLabel.LEFT);
                    label.setFont(new Font("Verdana", Font.BOLD, 14));
                    inputArea.add(label);

                    //#########################################
                    // JComboField
                    //#########################################

                    if (labelTXT.equals("Ingredient Measurement In:")) {
                        String ingredientMeassurements[] = {"Litres", "Grams"};
                        ingredientsMeasure_JComboBox = new JComboBox(ingredientMeassurements);
                        inputArea.add(ingredientsMeasure_JComboBox);

                        jcomboxBeingCreated = true;
                        comboBox = ingredientsMeasure_JComboBox;
                    } else if (labelTXT.equals("Ingredient_Type:")) {
                        String ingredientsType[] = {"Breads", "Cereals", "Cereal Bars", "Cheese", "Fish", "Frozen Fruit", "Frozen Vegetables", "Fruit",
                                "Eggs", "Grains & Legumes", "Juice", "Milk", "Lean Meat", "Noodles", "Nuts", "Nuts & Seeds", "Meat", "Other Grains", "Pasta",
                                "Potatoes", "Poultry", "Rice", "Smoothie", "Vegetables", "Yoghurt"};

                        ingredientsType_JComboBox = new JComboBox(ingredientsType);
                        inputArea.add(ingredientsType_JComboBox);

                        jcomboxBeingCreated = true;
                        comboBox = ingredientsType_JComboBox;
                    }

                    // if a JComboBox is being created Centre JComboBox Items & Set Selected Item to 0
                    if (jcomboxBeingCreated && comboBox != null) {
                        // Centre JComboBox Item
                        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
                        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
                        comboBox.setRenderer(listRenderer);

                        // Set Selected Item To Nothing
                        comboBox.setSelectedIndex(-1);

                        formObjects.add(comboBox);

                        continue;
                    }

                    //#########################################
                    // JTextfields
                    //#########################################
                    if (labelTXT.equals("Ingredient Name:")) //Setting TextField limits
                    {
                        textField.setDocument(new JTextFieldLimit(255));
                        ingredientNameLabelIndex = i;
                    } else {
                        textField.setDocument(new JTextFieldLimit(9));
                    }

                    formObjects.add(textField);
                    inputArea.add(textField);
                }
                mainJPanel.add(inputArea, BorderLayout.CENTER);

                //###########################################################
                // South Frame
                //###########################################################

                // Creating submit button
                JButton submitButton = new JButton("Submit Form");
                submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
                submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

                // creating commands for submit button to execute on
                submitButton.addActionListener(ae -> {

                    if (validateForm()) {
                        if (updateForm()) {
                            closeWindowEvent();
                            gui.updateInfo();
                            gui.macrosTargetsChanged(true);
                            closeeWindow();
                        }
                    }
                });

                mainJPanel.add(submitButton, BorderLayout.SOUTH);
                //###########################################################

                add(ingredinetsInfo_CJP);
                pack();
            }
        } catch (Exception e) {

        }
    }

    public boolean validateForm()// HELLO Modify
    {
        if (temp_PlanID == null && planID == null && planName == null) {
            JOptionPane.showMessageDialog(gui.getFrame(), "Please Select A Plan First!");
            return false;
        }

        String errorTxt = "";
        BigDecimal zero = new BigDecimal(0);

        //##############################
        // Validation JTextFields
        //##############################
        for (int row = 0; row < formObjects.size(); row++) {
            String value = "";
            Component comp = formObjects.get(row);

            if (comp instanceof JComboBox) {
                JComboBox comboBox = (JComboBox) comp;

                if (comboBox.getSelectedIndex() == -1) // if no item has been selected by JComboBox
                {
                    errorTxt += String.format("\n\n  ' %s ' on Row: %s, an option inside the dropdown menu must be selected", labels[row], row + 1);
                }
                continue;
            } else if (comp instanceof JTextField) {
                JTextField jTextField = (JTextField) comp;
                value = jTextField.getText().trim();


                //#########################################
                // Check if JTextfield input is empty
                //#########################################
                if (value.equals("")) {
                    errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is not ' NULL '!", labels[row], row + 1);
                    continue;
                }

                /*#######################################
                If JTextField is Ingredient Name Skip
                Decimal eval Below
                #########################################*/

                if (row == ingredientNameLabelIndex) {
                    continue;
                }

                //#########################################
                // Check if JTextfield input is a Decimal
                //#########################################
                try {
                    BigDecimal bdFromString = new BigDecimal(String.format("%s", value));
                    if (bdFromString.compareTo(zero) < 0 || bdFromString.compareTo(zero) == 0) // decimal less than 0
                    {
                        if (bdFromString.compareTo(zero) == 0 && labels[row].equals("Additional Calories:")) {
                            continue;
                        }
                        errorTxt += String.format("\n\n  ' %s ' on Row: %s,  must have a value which is bigger than 0 !", labels[row], row + 1);
                    }
                } catch (Exception e) {
                    errorTxt += String.format("\n\n  ' %s 'on Row: %s, must have a value which is a ' Decimal(8,2) ' !'", labels[row], row + 1);
                }
            }
        }

        //####################################################
        //Check if IngredientName Already exists in DB
        //####################################################

        JTextField ingredientName_JTxtF = (JTextField) formObjects.get(ingredientNameLabelIndex);
        String ingredientName_Txt = ingredientName_JTxtF.getText().trim();

        if (!(ingredientName_Txt.equals(""))) {
            String query = String.format("SELECT Ingredient_Name FROM ingredients_info WHERE Ingredient_Name = '%s';", ingredientName_Txt);

            if (db.getSingleColumnQuery(query) != null) {
                errorTxt += String.format("\n\n  Ingredient named %s already exists within the database!", ingredientName_Txt);
            }
        }

        //####################################################
        //Check if any error were found & Process it
        //####################################################

        if (errorTxt.length() == 0) {
            System.out.printf("\n\nNo Error");
            return true;
        }

        JOptionPane.showMessageDialog(gui.getFrame(), String.format("\n\nPlease fix the following rows being; \n%s", errorTxt));

        return false;
    }

    public boolean updateForm() // HELLO needs further update methods created for gui
    {
        //####################################
        // Gathering Form Txt Data
        //####################################
        ArrayList<String> formResults = new ArrayList<>();
        for (Component comp : formObjects) {
            if (comp instanceof JTextField) {
                formResults.add(((JTextField) comp).getText());
            } else if (comp instanceof JComboBox) {
                formResults.add(((JComboBox) comp).getSelectedItem().toString());
            }
        }

        //####################################
        // Creating Upload Query
        //####################################
        int i =0;
        String updateTargets_Query = String.format("""
                        INSERT INTO ingredients_info
                        
                        (Meassurement, Ingredient_Name, Ingredient_Type, Based_On_Quantity, Protein, Carbohydrates, 
                        Sugars_Of_Carbs, Fibre, Fat, Saturated_Fat, Salt, Water_Content, Calories)
                        
                        Values ('%s', '%s', '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) """,
                formResults.get(i), formResults.get(i+=1), formResults.get(i+=1), formResults.get(i+=1), formResults.get(i+=1),
                formResults.get(i+=1), formResults.get(i+=1), formResults.get(i+=1), formResults.get(i+=1),
                formResults.get(i+=1), formResults.get(i+=1), formResults.get(i+=1), formResults.get(i+=1));

        System.out.printf("\n\nQuery: \n\n%s", updateTargets_Query);

        //####################################
        // Uploading Query
        //####################################
        if (!(db.uploadData_Batch(new String[]{updateTargets_Query})))
        {
            JOptionPane.showMessageDialog(gui.getFrame(), "Un-able to Create Ingredient In DB!");
            return false;
        }

        JOptionPane.showMessageDialog(gui.getFrame(), "Successfully Created Ingredient In DB!!!");

        //HELLO Update feature for ingredients in Meal needed

        return true;
    }

    public void closeeWindow() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void closeWindowEvent() {
        gui.remove_addIngredients_Screen();
    }

    public void makeJframeVisible() {
        setExtendedState(JFrame.NORMAL);
        setPreferredSize(new Dimension(jFramewidth, jFrameheight));
        setLocation(1000, 0);
        setResizable(false);
    }

    private void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                                int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        gbc.ipady = ipady;
        gbc.ipadx = ipadx;

        switch (fill.toLowerCase()) {
            case "horizontal":
                gbc.fill = GridBagConstraints.HORIZONTAL;
                break;
            case "vertical":
                gbc.fill = GridBagConstraints.VERTICAL;
                break;

            case "both":
                gbc.fill = GridBagConstraints.BOTH;
                break;
        }

        container.add(addToContainer, gbc);
    }
}

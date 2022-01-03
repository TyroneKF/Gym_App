package App_Code.Objects.Screens.Edit_Ingredient_Info.Stores_And_Types.Types;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Screens.Edit_Ingredient_Info.IngredientsInfo.Add_Or_Edit_Ingredients_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParentClass extends JPanel
{
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected int yPos = 0;
    protected int charlimit = 55;


    protected MyJDBC db;
    protected JComboBox jComboBox;
    protected Collection<String> jcomboBoxList;
    protected Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen;
    protected String collapsibleBTNTXT1 = "", collapsibleBTNTXT2= "";

    public   ParentClass()
    {}

    public ParentClass(MyJDBC db, Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen, Collection<String> jcomboBoxList)
    {
        this.db = db;
        this. add_or_edit_ingredients_screen =  add_or_edit_ingredients_screen;
        this.jcomboBoxList = jcomboBoxList;

       createInterface();
    }

    protected void createInterface()
    {
        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################

        setLayout(new BorderLayout());

        //###################################################################################
        //   Create Main Centre Screen for Interface
        //##################################################################################
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        add(mainCentreScreen, BorderLayout.CENTER);


        //###########################
        //Add Ingredients Type Form
        //###########################
        AddScreen addIngredientsTypeScreen = new AddScreen(this, collapsibleBTNTXT1, 250, 50, "");
        addToContainer(mainCentreScreen, addIngredientsTypeScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit Ingredients Type Form
        //###########################
        EditScreen editIngredientsTypeScreen = new EditScreen(this, collapsibleBTNTXT2, 250, 50, "");
        addToContainer(mainCentreScreen, editIngredientsTypeScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Space Divider
        //###########################
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(630, 50));
        jPanel.setBackground(Color.PINK);
        addToContainer(mainCentreScreen, jPanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        revalidate();
    }

    public class AddScreen extends CollapsibleJPanel
    {
        protected int
                ypos2 = 0;

        protected JPanel jtextfieldJPanel, mainJPanel, mainJPanel2;
        protected JTextField jTextField;
        protected JButton submitButton;
        protected String
                jtextfieldTXT,
                mainLabel,

                dataGatheringName,

                dbColumnNameField,
                dbTableName;

        public AddScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight, String mainLabel)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
            expandJPanel();

            this.mainLabel = mainLabel;
            createAddScreenObjects();
        }

        private void createIconBar()
        {
            //#####################################################
            // Creating area for North JPanel (Refresh Icon)
            //#####################################################

            JPanel iconArea = new JPanel(new GridBagLayout());

            IconPanel iconPanel = new IconPanel(1, 10, "East");
            JPanel iconPanelInsert = iconPanel.getIconJpanel();

            addToContainer(iconArea, iconPanel.getIconAreaPanel(), 0, 0, 1, 1, 0.25, 0.25, "horizontal", 10, 0);

            //##########################
            // Refresh Icon
            //##########################
            int width = 35;
            int height = 35;

            IconButton refresh_Icon_Btn = new IconButton("src/images/refresh/++refresh.png", "", width, height, width, height,
                    "centre", "right"); // btn text is useless here , refactor

            JButton refresh_Btn = refresh_Icon_Btn.returnJButton();
            refresh_Icon_Btn.makeBTntransparent();

            refresh_Btn.addActionListener(ae -> {

                refreshBtnAction();
            });

            iconPanelInsert.add(refresh_Icon_Btn);

            addToContainer(mainJPanel2, iconArea, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            additionalIconSetup(iconPanelInsert);
        }

        protected void additionalIconSetup(JPanel iconPanelInsert)
        {
        }

        protected void refreshBtnAction()
        {
            jTextField.setText("");
        }

        protected  void successUploadMessage()
        {
            String text = "";
            JOptionPane.showMessageDialog(null, text);
        }

        protected void failureMessage()
        {
            String text = "";
            JOptionPane.showMessageDialog(null, text);
        }

        protected void submissionBtnAction()
        {
            if (validateForm())
            {
                if (uploadForm(true))
                {
                    successUploadMessage();

                    resetActions();
                    updateOtherScreens();
                }
                else
                {
                   failureMessage();
                }
            }
        }

        private void createAddScreenObjects()
        {
            mainJPanel = getCentreJPanel();
            mainJPanel.setLayout(new GridBagLayout());

            mainJPanel2 = new JPanel(new GridBagLayout());
            mainJPanel2.setBackground(Color.black);
            addToContainer(mainJPanel, mainJPanel2, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //#######################
            // Create Icon Bar
            //#######################
            createIconBar();

            //#################################################################
            //  Centre & Create Form
            //#################################################################
            jtextfieldJPanel = new JPanel(new GridLayout());
            jtextfieldJPanel.setPreferredSize(new Dimension(630, 50));
            jtextfieldJPanel.setBackground(Color.red);

            //#######################
            // JTextfield
            //#######################

            jTextField = new JTextField("");
            jTextField.setFont(new Font("Verdana", Font.PLAIN, 15));
            jTextField.setHorizontalAlignment(JTextField.CENTER);
            jTextField.setDocument(new JTextFieldLimit(charlimit));
            jtextfieldJPanel.add(jTextField);

            //###################################################################
            // South Screen for Interface
            //####################################################################

            // Creating submit button
            submitButton = new JButton("Submit");
            submitButton.setFont(new Font("Arial", Font.BOLD, 15)); // setting font
            submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

            // creating commands for submit button to execute on
            submitButton.addActionListener(ae -> {
                submissionBtnAction();
            });

            //###################################################################
            // Drawing interface
            //####################################################################
            creatingAdditionalAddScreenObjects(); // for overwrite purposes
            addScreenObjects(); // adding all objects to the screen
        }

        protected void creatingAdditionalAddScreenObjects()
        {

        }

        protected JPanel createLabelPanel(String labelTXT, JLabel jLabel)
        {
            //###########################
            // Create JPanel
            //###########################
            JPanel jpanel = new JPanel(new GridBagLayout());
            jpanel.setPreferredSize(new Dimension(630, 50));
            jpanel.setBackground(Color.GREEN);

            //###########################
            // Creating Label
            //###########################
            jLabel = new JLabel(labelTXT);
            jLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
            jLabel.setHorizontalAlignment(JLabel.CENTER);

            // Add title JPanel to North Panel Area
            addToContainer(jpanel, jLabel, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

            //###########################
            // Return Label
            //###########################
            return jpanel;
        }

        protected void addScreenObjects()
        {
            addToContainer(mainJPanel2, createLabelPanel(mainLabel, new JLabel()), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jtextfieldJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            addToContainer(mainJPanel2, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            mainJPanel.revalidate();
            mainJPanel2.revalidate();
        }

        protected void loadJComboBox()
        {
            jComboBox.removeAllItems();
            for (String ingredientType : jcomboBoxList)
            {                
                jComboBox.addItem(ingredientType);
            }
            jComboBox.setSelectedIndex(-1);
            
            removeJCombBoxItems();
        }
        
        protected void removeJCombBoxItems()
        {
            
        }

        protected boolean doesStringContainCharacters(String input)
        {
            Pattern p1 = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
            Matcher m1 = p1.matcher(input.replaceAll("\\s+", ""));
            boolean b1 = m1.find();

            if (b1)
            {
                return true;
            }

            return false;
        }

        protected String removeSpaceAndHiddenChars(String stringToBeEdited)
        {
            return stringToBeEdited.trim().replaceAll("\\p{C}", ""); // remove all whitespace & hidden characters like \n
        }

        protected boolean validateForm()
        {
            jtextfieldTXT = jTextField.getText();

            if (jtextfieldTXT.equals(""))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nAn %s Cannot Be Null!!", dataGatheringName));
                return false;
            }

            jtextfieldTXT = removeSpaceAndHiddenChars(jtextfieldTXT);

            if (doesStringContainCharacters(jtextfieldTXT))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nAn %s Cannot Contain Any Symbols Or, Numbers!!", dataGatheringName));
                return false;
            }

            if (!additionalValidateForm())
            {
                return false;
            }

            return true;
        }

        protected boolean additionalValidateForm()
        {
            return true;
        }

        protected boolean uploadForm(boolean checkDB)
        {
            if (checkDB)
            {
                String query = String.format("SELECT  %s  FROM %ss WHERE %s = '%s';",dbColumnNameField, dbTableName, jtextfieldTXT);

                System.out.printf("\n\n%s", query);

                if (db.getSingleColumnQuery(query) != null)
                {
                    JOptionPane.showMessageDialog(null, String.format("\n\ns '' %s '' Already Exists!", dataGatheringName, jtextfieldTXT));
                    return false;
                }
            }

            String uploadString = String.format("""
                    INSERT INTO %s (%s) VALUES
                    ('%s');
                    """,dbTableName,dbColumnNameField, jtextfieldTXT);

            if (db.uploadData_Batch_Altogether(new String[]{uploadString}))
            {
                return true;
            }

            return false;
        }

        protected void addOrDeleteIngredientFromMap(String process, String ingredientType)
        {
            if (process.equals("add"))// if key exists add the ingredientName in
            {
                jcomboBoxList.add(ingredientType);
            }
            else if (process.equals("delete"))
            {
                jcomboBoxList.remove(ingredientType);
            }

            add_or_edit_ingredients_screen.setAll_IngredientsTypeNamesList(jcomboBoxList);
        }

        protected void resetActions()
        {
            refreshBtnAction();
            addOrDeleteIngredientFromMap("add", jtextfieldTXT);
            loadJComboBox();
        }


        protected void updateOtherScreens()
        {

        }
    }

    public class EditScreen extends AddScreen
    {
        protected JPanel jcomboBoxJPanel;
        protected  String
                lable1, lablel2,
                idColumnName;

        public EditScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight, String mainLabel2)
        {
            super(parentContainer, btnText, btnWidth, btnHeight, mainLabel2);
        }

        @Override
        protected void additionalIconSetup(JPanel iconPanelInsert)
        {
            //###########################################
            // DELETE Icon
            //###########################################
            int width = 35;
            int height = 35;

            IconButton delete_Icon_Btn = new IconButton("src/images/x/x.png", "", width, height, width, height,
                    "centre", "right"); // btn text is useless here , refactor

            JButton delete_Btn = delete_Icon_Btn.returnJButton();
            delete_Icon_Btn.makeBTntransparent();

            delete_Btn.addActionListener(ae -> {

                deleteIngredientBTNAction();
            });

            iconPanelInsert.add(delete_Icon_Btn);
        }

        private void deleteIngredientBTNAction()
        {

        }

        @Override
        protected void creatingAdditionalAddScreenObjects()
        {
            //########################################################################################################
            //  IngredientTypeJComboBox
            //########################################################################################################
            jcomboBoxJPanel = new JPanel(new GridLayout(1, 1));

            jComboBox = new JComboBox();
            loadJComboBox();

            jComboBox.setSelectedIndex(-1);
            jComboBox.setFont(new Font("Arial", Font.PLAIN, 15)); // setting font
            ((JLabel) jComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

            jcomboBoxJPanel.add(jComboBox);
            jcomboBoxJPanel.setPreferredSize(new Dimension(650, 50));
        }

        @Override
        protected boolean additionalValidateForm()
        {
            if (jComboBox.getSelectedIndex() == -1)
            {
                JOptionPane.showMessageDialog(null,String.format("\n\nSelect An %s To Edit!", dataGatheringName));
                return false;
            }

            return true;
        }

        @Override
        protected boolean uploadForm(boolean checkDB)
        {
            if (checkDB)
            {
                String query = String.format("SELECT  Ingredient_Type_Name  FROM ingredientTypes WHERE Ingredient_Type_Name = '%s';", jtextfieldTXT);

                System.out.printf("\n\n%s", query);

                if (db.getSingleColumnQuery(query) != null)
                {
                    JOptionPane.showMessageDialog(null, String.format("\n\nIngredient Type Name '' %s '' Already Exists!", jtextfieldTXT));
                    return false;
                }
            }

            String selectedItem = (String) jComboBox.getSelectedItem();
            String mysqlVariableReference1 = "@CurrentTypeID";
            String createMysqlVariable1 = String.format("SET %s = (SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = '%s');", mysqlVariableReference1, selectedItem);

            String uploadString = String.format("""                    
                    UPDATE ingredientTypes
                    SET Ingredient_Type_Name = '%s'
                    WHERE Ingredient_Type_ID = %s;""", jtextfieldTXT, mysqlVariableReference1);

            System.out.printf("\n\n%s \n\n%s", createMysqlVariable1, uploadString);

            if (db.uploadData_Batch_Independently(new String[]{createMysqlVariable1, uploadString}))
            {
                return true;
            }

            return false;
        }

        @Override
        protected void resetActions()
        {
            addOrDeleteIngredientFromMap("add", jtextfieldTXT);
            addOrDeleteIngredientFromMap("delete", (String) jComboBox.getSelectedItem());
            refreshBtnAction();
            loadJComboBox();
        }

        @Override
        protected void addScreenObjects()
        {
            addToContainer(mainJPanel2, createLabelPanel(lable1, new JLabel("dddd")), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jcomboBoxJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            addToContainer(mainJPanel2, new JPanel(), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            addToContainer(mainJPanel2, createLabelPanel(lablel2, new JLabel("ssa")), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jtextfieldJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            mainJPanel.revalidate();
            mainJPanel2.revalidate();
        }

        @Override
        protected void refreshBtnAction()
        {
            try
            {
                jTextField.setText("");
                jComboBox.setSelectedIndex(-1);
            }
            catch (Exception e)
            {
                System.out.printf("\n\n%s", e);
            }
        }
    }

    protected void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
                                  int gridheight, double weightx, double weighty, String fill, int ipady, int ipadx)
    {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        gbc.ipady = ipady;
        gbc.ipadx = ipadx;

        switch (fill.toLowerCase())
        {
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

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

public class IngredientsTypesScreen extends JPanel
{
    /*
    private GridBagConstraints gbc = new GridBagConstraints();
    private int yPos = 0;
    private int charlimit = 55;
    private Collection<String> all_IngredientsTypeNamesList;

    private MyJDBC db;
    private JComboBox ingredientTypes_JC;

    private Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen;

    public IngredientsTypesScreen(MyJDBC db, Add_Or_Edit_Ingredients_Screen add_or_edit_ingredients_screen)
    {
        this.db = db;
        this. add_or_edit_ingredients_screen =  add_or_edit_ingredients_screen;
        this.all_IngredientsTypeNamesList = add_or_edit_ingredients_screen.getAll_IngredientsTypeNamesList();

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
        AddIngredientsTypeScreen addIngredientsTypeScreen = new AddIngredientsTypeScreen(this, "Add Ingredients Type", 250, 50);
        addToContainer(mainCentreScreen, addIngredientsTypeScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit Ingredients Type Form
        //###########################
        EditIngredientsTypeScreen editIngredientsTypeScreen = new EditIngredientsTypeScreen(this, "Edit Ingredients Type", 250, 50);
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

    public class AddIngredientsTypeScreen extends CollapsibleJPanel
    {
        protected int
                ypos2 = 0;

        protected JPanel mainTitlePanel, jtextfieldJPanel, mainJPanel, mainJPanel2;
        protected JTextField jTextField;
        protected JButton submitButton;
        protected String newIngredientTypeName;

        public AddIngredientsTypeScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
            expandJPanel();
            createAddTypeScreenObjects();
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

        private void createAddTypeScreenObjects()
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
            creatingAdditionalObjects(); // for overwrite purposes
            addScreenObjects(); // adding all objects to the screen
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

        protected void addScreenObjects()
        {
            addToContainer(mainJPanel2, createLabelPanel("Add Ingredient Type Name", new JLabel()), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jtextfieldJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            addToContainer(mainJPanel2, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            mainJPanel.revalidate();
            mainJPanel2.revalidate();
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

        protected void creatingAdditionalObjects()
        {

        }

        protected void loadJComboBox()
        {
            ingredientTypes_JC.removeAllItems();
            for (String ingredientType : all_IngredientsTypeNamesList)
            {
                if(ingredientType.equals("UnAssigned") || ingredientType.equals("None Of The Above"))
                {
                    continue;
                }
                ingredientTypes_JC.addItem(ingredientType);
            }
            ingredientTypes_JC.setSelectedIndex(-1);
        }

        protected void submissionBtnAction()
        {
            if (validateForm())
            {
                if (uploadForm(true))
                {
                    JOptionPane.showMessageDialog(null, "\n\nSuccessfully Added New Ingredient Type");
                    resetActions();
                    updateOtherScreenIngredientTypes();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "\n\nFailed Upload - Couldn't Add New Ingredient Type");
                }
            }
        }

        protected void resetActions()
        {
            refreshBtnAction();
            addOrDeleteIngredientFromMap("add", newIngredientTypeName);
            loadJComboBox();
        }

        protected boolean validateForm()
        {
            newIngredientTypeName = jTextField.getText();

            if (newIngredientTypeName.equals(""))
            {
                JOptionPane.showMessageDialog(null, "\n\nAn Ingredient Type Name Cannot Be Null!!");
                return false;
            }

            newIngredientTypeName = removeSpaceAndHiddenChars(newIngredientTypeName);

            if (doesStringContainCharacters(newIngredientTypeName))
            {
                JOptionPane.showMessageDialog(null, "\n\nAn Ingredient Type Name Cannot Contain Any Symbols Or, Numbers!!");
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
                String query = String.format("SELECT  Ingredient_Type_Name  FROM ingredientTypes WHERE Ingredient_Type_Name = '%s';", newIngredientTypeName);

                System.out.printf("\n\n%s", query);

                if (db.getSingleColumnQuery(query) != null)
                {
                    JOptionPane.showMessageDialog(null, String.format("\n\nIngredient Type Name '' %s '' Already Exists!", newIngredientTypeName));
                    return false;
                }
            }

            String uploadString = String.format("""
                    INSERT INTO ingredientTypes (Ingredient_Type_Name) VALUES
                    ('%s');
                    """, newIngredientTypeName);

            if (db.uploadData_Batch_Altogether(new String[]{uploadString}))
            {
                return true;
            }

            return false;
        }

        //HELLO EDIT
        protected void addOrDeleteIngredientFromMap(String process, String ingredientType)
        {
            if (process.equals("add"))// if key exists add the ingredientName in
            {
                all_IngredientsTypeNamesList.add(ingredientType);
            }
            else if (process.equals("delete"))
            {
                all_IngredientsTypeNamesList.remove(ingredientType);
            }
        }
    }

    public class EditIngredientsTypeScreen extends AddIngredientsTypeScreen
    {
        private JPanel jcomboBoxJPanel;

        public EditIngredientsTypeScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
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
            String selectedItem = (String) ingredientTypes_JC.getSelectedItem();

            if(selectedItem == null)
            {
                JOptionPane.showMessageDialog(null, "Select An Ingredient Type To Delete It!!!");
                return;
            }

            String mysqlVariableReference1 = "@CurrentTypeID";
            String createMysqlVariable1 = String.format("SET %s = (SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = '%s');", mysqlVariableReference1, selectedItem);


            String query = String.format("""                  
                    UPDATE ingredients_info
                    SET Ingredient_Type_ID = (SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = 'UnAssigned')
                    WHERE Ingredient_Type_ID = %s;""", mysqlVariableReference1, selectedItem );


            String query2 = String.format("DELETE FROM ingredientTypes WHERE Ingredient_Type_ID = @CurrentTypeID;");

            System.out.printf("\n\n%s \n\n%s \n\n%s",createMysqlVariable1, query, query2);

            if(! db.uploadData_Batch_Independently(new String[]{createMysqlVariable1, query, query2}))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete Selected Item ''%s'' !!"));
                return;
            }

            addOrDeleteIngredientFromMap("delete", (String) ingredientTypes_JC.getSelectedItem());
            refreshBtnAction();
            loadJComboBox();
            updateOtherScreenIngredientTypes();

            JOptionPane.showMessageDialog(null, String.format("\n\nSelected Item ''%s'' Has Successfully Been Deleted!!!", selectedItem));
        }

        @Override
        protected void creatingAdditionalObjects()
        {
            //########################################################################################################
            //  IngredientTypeJComboBox
            //########################################################################################################
            jcomboBoxJPanel = new JPanel(new GridLayout(1, 1));

            ingredientTypes_JC = new JComboBox();
            loadJComboBox();

            ingredientTypes_JC.setSelectedIndex(-1);
            ingredientTypes_JC.setFont(new Font("Arial", Font.PLAIN, 15)); // setting font
            ((JLabel) ingredientTypes_JC.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

            jcomboBoxJPanel.add(ingredientTypes_JC);
            jcomboBoxJPanel.setPreferredSize(new Dimension(650, 50));
        }

        @Override
        protected boolean additionalValidateForm()
        {
            if (ingredientTypes_JC.getSelectedIndex() == -1)
            {
                JOptionPane.showMessageDialog(null,"\n\nSelect An Ingredient Type To Edit!");
                return false;
            }

            return true;
        }

        @Override
        protected boolean uploadForm(boolean checkDB)
        {
            if (checkDB)
            {
                String query = String.format("SELECT  Ingredient_Type_Name  FROM ingredientTypes WHERE Ingredient_Type_Name = '%s';", newIngredientTypeName);

                System.out.printf("\n\n%s", query);

                if (db.getSingleColumnQuery(query) != null)
                {
                    JOptionPane.showMessageDialog(null, String.format("\n\nIngredient Type Name '' %s '' Already Exists!", newIngredientTypeName));
                    return false;
                }
            }

            String selectedItem = (String) ingredientTypes_JC.getSelectedItem();
            String mysqlVariableReference1 = "@CurrentTypeID";
            String createMysqlVariable1 = String.format("SET %s = (SELECT Ingredient_Type_ID FROM ingredientTypes WHERE Ingredient_Type_Name = '%s');", mysqlVariableReference1, selectedItem);

            String uploadString = String.format("""                    
                    UPDATE ingredientTypes
                    SET Ingredient_Type_Name = '%s'
                    WHERE Ingredient_Type_ID = %s;""", newIngredientTypeName, mysqlVariableReference1);

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
            addOrDeleteIngredientFromMap("add", newIngredientTypeName);
            addOrDeleteIngredientFromMap("delete", (String) ingredientTypes_JC.getSelectedItem());
            refreshBtnAction();
            loadJComboBox();
        }

        @Override
        protected void addScreenObjects()
        {
            addToContainer(mainJPanel2, createLabelPanel("Select Ingredient Type Name To Edit", new JLabel()), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jcomboBoxJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            addToContainer(mainJPanel2, new JPanel(), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            addToContainer(mainJPanel2, createLabelPanel("Change Ingredient Type Name", new JLabel()), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
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
                ingredientTypes_JC.setSelectedIndex(-1);
            }
            catch (Exception e)
            {
                System.out.printf("\n\n%s", e);
            }
        }
    }

    private void updateOtherScreenIngredientTypes()
    {
        add_or_edit_ingredients_screen.updateAllIngredientTypesJComboBoxes();
    }

    private void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
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

     */
}

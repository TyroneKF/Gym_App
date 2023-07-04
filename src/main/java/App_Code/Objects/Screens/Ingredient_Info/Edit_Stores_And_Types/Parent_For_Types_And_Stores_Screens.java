package App_Code.Objects.Screens.Ingredient_Info.Edit_Stores_And_Types;


import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parent_For_Types_And_Stores_Screens extends JPanel
{

    protected GridBagConstraints gbc = new GridBagConstraints();
    protected int yPos = 0;
    protected int charlimit = 55;


    protected MyJDBC db;
    protected JComboBox jComboBoxObject;
    protected Collection<String> jcomboBoxList;
    protected Ingredients_Info_Screen parentIngredientsScreen;
    protected String collapsibleBTNTXT1 = "", collapsibleBTNTXT2 = "", sqlFilePath, process;

    protected EditScreen editScreen;
    protected AddScreen addScreen;

    public Parent_For_Types_And_Stores_Screens()
    {
    }

    public Parent_For_Types_And_Stores_Screens(MyJDBC db, Ingredients_Info_Screen _Parent__ingredients_Info_screen, Collection<String> jcomboBoxList)
    {
        this.db = db;
        this.parentIngredientsScreen = _Parent__ingredients_Info_screen;
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
        //Add  Form
        //###########################
        addScreen = new AddScreen(this, collapsibleBTNTXT1, 250, 50);
        addToContainer(mainCentreScreen, addScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //###########################
        //Edit  Form
        //###########################
        editScreen = new EditScreen(this, collapsibleBTNTXT2, 250, 50);
        addToContainer(mainCentreScreen, editScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

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
        protected int ypos2 = 0;

        protected JPanel jtextfieldJPanel, mainJPanel, mainJPanel2;
        protected JTextField jTextField;
        protected JButton submitButton;

        protected String jTextfieldTXT, mainLabel, dataGatheringName, dbColumnNameField, dbTableName;

        public AddScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
            expandJPanel();
            createForm();
        }

        protected void createForm()
        {
            //###################################################################
            // Drawing interface
            //####################################################################
            createAddScreenObjects();
            creatingAdditionalAddScreenObjects(); // for overwrite purposes
            addScreenObjects(); // adding all objects to the screen
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

            IconButton refresh_Icon_Btn = new IconButton("src/main/java/images/refresh/++refresh.png", "", width, height, width, height,
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

        protected void successUploadMessage()
        {
            String text = "";
            JOptionPane.showMessageDialog(null, text);
        }

        protected void failureMessage()
        {
            String text = "";
            JOptionPane.showMessageDialog(null, text);
        }

        protected void createAddScreenObjects()
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
            addToContainer(mainJPanel2, createLabelPanel(mainLabel, new JLabel(mainLabel)), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jtextfieldJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            addToContainer(mainJPanel2, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            mainJPanel.revalidate();
            mainJPanel2.revalidate();
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

        protected void submissionBtnAction()
        {
            if (validateForm())
            {
                if (uploadForm())
                {
                    updateOtherScreens();
                    backupDataInSQLFile();
                    resetActions();
                    successUploadMessage();
                }
                else
                {
                    failureMessage();
                }
            }
        }

        protected boolean validateForm()
        {
            jTextfieldTXT = jTextField.getText();

            if (!additionalValidateForm())
            {
                return false;
            }

            if (jTextfieldTXT.equals(""))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nAn %s Cannot Be Null!!", dataGatheringName));
                return false;
            }

            jTextfieldTXT = removeSpaceAndHiddenChars(jTextfieldTXT);

            if (doesStringContainCharacters(jTextfieldTXT))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nAn %s Cannot Contain Any Symbols Or, Numbers!!", dataGatheringName));
                return false;
            }

            return true;
        }

        protected boolean additionalValidateForm()
        {
            return true;
        }

        protected boolean uploadForm()
        {
            String query = String.format("SELECT %s  FROM %s WHERE %s = '%s';", dbColumnNameField, dbTableName, dbColumnNameField, jTextfieldTXT);

            if (db.getSingleColumnQuery(query) != null)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", dataGatheringName, jTextfieldTXT));
                return false;
            }


            String uploadString = String.format("""
                    INSERT INTO %s (%s) VALUES
                    ('%s');
                    """, dbTableName, dbColumnNameField, jTextfieldTXT);

            if (db.uploadData_Batch_Altogether(new String[]{uploadString}))
            {
                return true;
            }

            return false;
        }

        protected void updateOtherScreens()
        {
            System.out.printf("\n\nDefault updateOtherScreens()");
        }

        protected void resetActions()
        {
            refreshBtnAction();
            editScreen.loadJComboBox();
        }

        protected boolean backupDataInSQLFile()
        {
            String txtToAdd = String.format("\n('%s');", jTextfieldTXT);

            if (!(db.writeTxtToSQLFile(sqlFilePath, txtToAdd)))
            {
                JOptionPane.showMessageDialog(null, String.format("Error, backing up new %s to SQL file!", process));
                return false;
            }
            return true;
        }
    }

    public class EditScreen extends AddScreen
    {
        protected JPanel jcomboBoxJPanel;
        protected String
                lable1, label2,
                idColumnName,
                selectedJComboBoxItemTxt = "",
                fkTable;
        protected boolean itemDeleted = false;
        protected String[] removeJComboBoxItems = new String[]{};

        public EditScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
        }

        protected void loadJComboBox()
        {
            jComboBoxObject.removeAllItems();
            for (String object : jcomboBoxList)
            {
                jComboBoxObject.addItem(object);
            }
            jComboBoxObject.setSelectedIndex(-1);
        }

        @Override
        protected void createForm()
        {
            //###################################################################
            // Drawing interface
            //####################################################################
            createAddScreenObjects();
            creatingAdditionalAddScreenObjects(); // for overwrite purposes
            addScreenObjects(); // adding all objects to the screen
        }

        protected void addScreenObjects()
        {
            addToContainer(mainJPanel2, createLabelPanel(lable1, new JLabel()), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jcomboBoxJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            addToContainer(mainJPanel2, new JPanel(), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

            addToContainer(mainJPanel2, createLabelPanel(label2, new JLabel()), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, jtextfieldJPanel, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);
            addToContainer(mainJPanel2, submitButton, 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

            mainJPanel.revalidate();
            mainJPanel2.revalidate();
        }

        @Override
        protected void additionalIconSetup(JPanel iconPanelInsert)
        {
            //###########################################
            // DELETE Icon
            //###########################################
            int width = 35;
            int height = 35;

            IconButton delete_Icon_Btn = new IconButton("src/main/java/images/x/x.png", "", width, height, width, height,
                    "centre", "right"); // btn text is useless here , refactor

            JButton delete_Btn = delete_Icon_Btn.returnJButton();
            delete_Icon_Btn.makeBTntransparent();

            delete_Btn.addActionListener(ae -> {

                deleteBTNActionListener();
            });

            iconPanelInsert.add(delete_Icon_Btn);
        }

        @Override
        protected void creatingAdditionalAddScreenObjects()
        {
            //########################################################################################################
            //  JComboBox
            //########################################################################################################
            jcomboBoxJPanel = new JPanel(new GridLayout(1, 1));

            jComboBoxObject = new JComboBox();
            loadJComboBox();

            jComboBoxObject.setSelectedIndex(-1);
            jComboBoxObject.setFont(new Font("Arial", Font.PLAIN, 15)); // setting font
            ((JLabel) jComboBoxObject.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

            jComboBoxObject.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent ie)
                {
                    if (ie.getStateChange() == ItemEvent.SELECTED)
                    {
                        selectedJComboBoxItemTxt = (String) jComboBoxObject.getSelectedItem();
                    }
                }
            });

            jcomboBoxJPanel.add(jComboBoxObject);
            jcomboBoxJPanel.setPreferredSize(new Dimension(650, 50));
        }

        @Override
        protected boolean additionalValidateForm()
        {
            if (jComboBoxObject.getSelectedIndex() == -1)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nSelect An %s To Edit!", dataGatheringName));
                return false;
            }
            return true;
        }

        @Override
        protected boolean uploadForm()
        {
            String query = String.format("SELECT %s  FROM %s WHERE %s = '%s';", dbColumnNameField, dbTableName, dbColumnNameField, jTextfieldTXT);

            if (db.getSingleColumnQuery(query) != null)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\n%s '' %s '' Already Exists!", dataGatheringName, jTextfieldTXT));
                return false;
            }

            String mysqlVariableReference1 = "@CurrentID";
            String createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = '%s');",

                    mysqlVariableReference1, idColumnName, dbTableName, dbColumnNameField, selectedJComboBoxItemTxt);

            String uploadString = String.format("""                    
                            UPDATE %s 
                            SET %s = '%s'
                            WHERE %s = %s;""",

                    dbTableName, dbColumnNameField, jTextfieldTXT, idColumnName, mysqlVariableReference1);

            if (db.uploadData_Batch_Independently(new String[]{createMysqlVariable1, uploadString}))
            {
                return true;
            }

            return false;
        }

        @Override
        protected void resetActions()
        {
            refreshBtnAction();
            loadJComboBox();
            itemDeleted = false;
        }

        @Override
        protected void refreshBtnAction()
        {
            try
            {
                jTextField.setText("");
                jComboBoxObject.setSelectedIndex(-1);
                selectedJComboBoxItemTxt = "";
            }
            catch (Exception e)
            {
                System.out.printf("\n\n%s", e);
            }
        }

        private void deleteBTNActionListener()
        {
            if (selectedJComboBoxItemTxt.equals(""))
            {
                JOptionPane.showMessageDialog(null, String.format("Select An ' %s 'To Delete It !!!", dataGatheringName));
                return;
            }

            if (deleteBTNAction())
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nSelected Item ''%s'' Has Successfully Been Deleted!!!", selectedJComboBoxItemTxt));

                updateOtherScreens();
                resetActions();
            }
            else
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete Selected Item ''%s'' !!", selectedJComboBoxItemTxt));
            }
        }

        protected  ArrayList<String> deleteBTNQueries(String mysqlVariableReference1, ArrayList<String> queries)
        {
            return queries;
        }

        protected boolean deleteBTNAction()
        {

            //##########################################################################################################
            // Delete From SQL Database
            //##########################################################################################################
            System.out.printf("\n#################################################################################");

            String mysqlVariableReference1 = "@CurrentID";
            String createMysqlVariable1 = String.format("SET %s = (SELECT %s FROM %s WHERE %s = '%s');",
                    mysqlVariableReference1, idColumnName, dbTableName, dbColumnNameField, selectedJComboBoxItemTxt);

            ArrayList<String> queries = deleteBTNQueries(mysqlVariableReference1, new ArrayList<>(Arrays.asList(createMysqlVariable1)));

            //##########################################################################################################
            //
            //##########################################################################################################
            if (!db.uploadData_Batch_Independently(queries))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nFailed To Delete ' %s ' FROM %s !!", selectedJComboBoxItemTxt, dataGatheringName));
                return false;
            }

            itemDeleted = true;

            //##########################################################################################################
            // Delete From BackUp SQL File
            //##########################################################################################################

            ArrayList<String> txtToDeleteList = new ArrayList<>(Arrays.asList(String.format("('%s'),", selectedJComboBoxItemTxt), String.format("('%s');;", selectedJComboBoxItemTxt)));

            if (!(db.deleteTxtInFile(sqlFilePath, txtToDeleteList)))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError, deleteBTNAction() deleting ingredient type '%s' from backup files!", selectedJComboBoxItemTxt));
            }

            //##########################################################################################################
            //
            //##########################################################################################################
            return true;
        }

        @Override
        protected boolean backupDataInSQLFile()
        {
            System.out.printf("\n\nSql File Path: %s \nSelectedJComboBox: %s \nJTextfield: %s", sqlFilePath,selectedJComboBoxItemTxt,jTextfieldTXT);

            if( ! (db.replaceTxtInSQLFile(sqlFilePath,selectedJComboBoxItemTxt,jTextfieldTXT)))
            {
                JOptionPane.showMessageDialog(null, String.format("Error, changing back-up of %s in SQL file!", process));
                return false;
            }
            return  true;
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

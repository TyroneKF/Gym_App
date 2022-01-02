package App_Code.Objects.Screens.Edit_Ingredients_Info;


import App_Code.Objects.Gui_Objects.CollapsibleJPanel;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.IconPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class IngredientsTypesScreen extends JPanel
{
    private GridBagConstraints gbc = new GridBagConstraints();
    private int yPos = 0;
    private int charlimit = 55;
    protected String[] all_IngredientsTypeNamesList;

    public IngredientsTypesScreen(String[] all_IngredientsTypeNamesList)
    {
        this.all_IngredientsTypeNamesList =  all_IngredientsTypeNamesList;

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
        EditIngredientsTypeScreen editIngredientsTypeScreen = new EditIngredientsTypeScreen(this, "Edit Ingredients Type", 250, 50, all_IngredientsTypeNamesList);
        addToContainer(mainCentreScreen, editIngredientsTypeScreen, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);


        //###########################
        //Space Divider
        //###########################
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(630, 50));
        jPanel.setBackground(Color.PINK);
        addToContainer(mainCentreScreen, jPanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);
    }

    public class AddIngredientsTypeScreen extends CollapsibleJPanel
    {
        protected int ypos = 0;
        protected int
                ypos2 = 0,
                labelPos = ypos2 += 1,
                jtextfieldPos = ypos2 += 1,
                submitBTNPos = ypos2 += 1;

        protected JPanel mainTitlePanel, jtextfieldJPanel, mainJPanel, mainJPanel2;
        protected JTextField jTextField;
        protected JButton submitButton;

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
            addToContainer(mainTitlePanel, iconArea, 0, ypos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

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


            });

            iconPanelInsert.add(refresh_Icon_Btn);
        }

        private void createAddTypeScreenObjects()
        {
            mainJPanel = getCentreJPanel();
            //mainJPanel.setPreferredSize(new Dimension(jFramewidth,  50));
            mainJPanel.setBackground(Color.PINK);
            mainJPanel.setLayout(new GridBagLayout());

            mainJPanel2 = new JPanel(new GridBagLayout());
            mainJPanel2.setBackground(Color.black);
            addToContainer(mainJPanel, mainJPanel2, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //###################################################################
            //Space Divider
            //###################################################################
            addToContainer(mainJPanel2, new JPanel(), 0, ypos2 += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

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

        protected void setSubmitBtnText(String text)
        {
            submitButton.setText(text);
        }

        protected void creatingAdditionalObjects()
        {

        }

        protected void submissionBtnAction()
        {

        }
    }

    public class EditIngredientsTypeScreen extends AddIngredientsTypeScreen
    {
        private JComboBox ingredientTypes_JComboBox = new JComboBox();
        private JPanel jcomboBoxJPanel;
        private String[] all_IngredientsTypeNamesList;

        public EditIngredientsTypeScreen(Container parentContainer, String btnText, int btnWidth, int btnHeight, String[] all_IngredientsTypeNamesList)
        {
            super(parentContainer, btnText, btnWidth, btnHeight);
            this.all_IngredientsTypeNamesList = all_IngredientsTypeNamesList;
        }

        @Override
        protected void creatingAdditionalObjects()
        {
            //########################################################################################################
            //  IngredientTypeJComboBox
            //########################################################################################################
            jcomboBoxJPanel = new JPanel(new GridLayout(1, 1));

            ingredientTypes_JComboBox = new JComboBox(all_IngredientsTypeNamesList);
            ingredientTypes_JComboBox.setFont(new Font("Arial", Font.PLAIN, 15)); // setting font
            ((JLabel) ingredientTypes_JComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text

            //################################################
            //  Actionlistener
            //#################################################

            ingredientTypes_JComboBox.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent ie)
                {
                    if (ie.getStateChange() == ItemEvent.SELECTED)
                    {

                    }
                }
            });

            jcomboBoxJPanel.add(ingredientTypes_JComboBox);
            jcomboBoxJPanel.setPreferredSize(new Dimension(650, 50));
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
        protected void submissionBtnAction()
        {

        }
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
}

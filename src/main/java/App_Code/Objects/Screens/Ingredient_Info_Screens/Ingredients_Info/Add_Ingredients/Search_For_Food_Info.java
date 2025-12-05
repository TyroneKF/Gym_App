package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients;

//#################################################################################################################
//
//##################################################################################################################

import App_Code.Objects.Gui_Objects.CollapsibleJPanel;

import javax.swing.*;
import java.awt.*;

import App_Code.Objects.API.Nutritionix.NutritionIx_API;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.Text_Fields.Parent.JTextFieldLimit;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Search_For_Food_Info extends CollapsibleJPanel
{
    private Ingredients_Form addIngredientsForm;

    private GridBagConstraints gbc = new GridBagConstraints();

    private Container parentContainer;
    private JPanel centrePanel, searchBarResults, scrollPaneJPanel, searchBarJPanel;

    private NutritionIx_API nutritionIx_api;
    private ArrayList<JPanel> resultsJPanelDisplay = new ArrayList();

    private JTextField textField;
    private JComboBox jComboBox;
    private ArrayList<JPanel> searchResultsJPanels = new ArrayList<>();

    private int
            yPos = 1,
            frameWidth = 690, frameHeight = 400,

    titleJPanelHeight = 45, titleFontSize = 16,

    searchBarTxtInputSize = 15,

    searchBarHeight = titleJPanelHeight,
            searchBarButtonWidth = 45,

    searchBarWidth = frameWidth - 160,

    searchBarIconWidth = searchBarButtonWidth - 10,
            searchBarButtonHeight = 45,
            searchBarIconHeight = searchBarButtonHeight - 10,
            searchBarJPanelXPos = 0, searchBarJPanelYPos = 0;

    public Search_For_Food_Info(Container parentContainer, Ingredients_Form ingredients_Form, String btnText)
    {
        super(parentContainer, btnText, 250, 50);

        this.parentContainer = parentContainer;
        this.addIngredientsForm = ingredients_Form;
        this.frameWidth = 430;
        this.frameHeight = 350;

        nutritionIx_api = new NutritionIx_API();
        expand_JPanel();

        //###################################################################
        //
        //###################################################################
        JPanel mainJPanel = get_Centre_JPanel();
        mainJPanel.setLayout(new BorderLayout());

        centrePanel = new JPanel(new GridBagLayout());
        mainJPanel.add(centrePanel, BorderLayout.CENTER);//

        createGUI();
    }


    public void createGUI()
    {
        centrePanel.setLayout(new GridBagLayout());
        centrePanel.setPreferredSize(new Dimension(frameWidth, frameHeight));

        //##############################################################################################################
        // Create ScrollPane & add to Interface
        //##############################################################################################################
        ScrollPaneCreator scrollPane = new ScrollPaneCreator();
        addToContainer(centrePanel, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new BorderLayout());

        //##############################################################################################################
        //  Creating JPanel Sections
        //##############################################################################################################


        //#####################################################################
        //  North JPanel
        //#####################################################################
        JPanel mainNorthPanel = new JPanel(new BorderLayout());
        //mainNorthPanel.setBackground(Color.RED);
        scrollPaneJPanel.add(mainNorthPanel, BorderLayout.NORTH);

        //####################################
        // Label
        //####################################
        JLabel titleLabel = new JLabel("Search For Ingredient Info");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, titleFontSize));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(frameWidth - 50, titleJPanelHeight));
        titlePanel.setBackground(Color.green);
        titlePanel.add(titleLabel);

        // Add title JPanel to North Panel Area
        mainNorthPanel.add(titlePanel, BorderLayout.CENTER);

        //#########################################################################################################
        //  Centre JPanel
        //##########################################################################################################
        JPanel mainCenterPanel = new JPanel(new BorderLayout());
        scrollPaneJPanel.add(mainCenterPanel, BorderLayout.CENTER);

        //#################################################################
        // Search Bar JPanel
        //#################################################################
        // Adding the main section of search Bar JPanel GUI to display
        searchBarJPanel = new JPanel(new GridBagLayout());
        searchBarJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
        mainCenterPanel.add(searchBarJPanel, BorderLayout.NORTH);

        //#################################################################
        // Search Bar JComboBox
        //#################################################################
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setBorder(BorderFactory.createLineBorder(Color.red, 3));

        addToContainer(searchBarJPanel, jp, searchBarJPanelXPos, 0, 1, 1, 1, 1, "horizontal", 0, 0);

        String jComboBoxOptions[] = {"Single Ingredient", "Product"};
        jComboBox = new JComboBox(jComboBoxOptions);
        jComboBox.setFont(new Font("Verdana", Font.PLAIN, 15));
        jp.add(jComboBox);

        ((JLabel) jComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
        jComboBox.setSelectedIndex(-1);

        //########################################################################
        // (West) TextField JPanel
        //########################################################################
        // Creating JPanel for text input area for search bar
        JPanel searchBarWestJPanel = new JPanel(new GridLayout(1, 1));
        searchBarWestJPanel.setPreferredSize(new Dimension(searchBarWidth, searchBarHeight));
        searchBarWestJPanel.setBackground(Color.BLUE);
        addToContainer(searchBarJPanel, searchBarWestJPanel, searchBarJPanelXPos, searchBarJPanelYPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //####################
        // Text Field
        //####################

        // Creating Text input object for search bar
        textField = new JTextField("");

        textField.setFont(new Font("Verdana", Font.PLAIN, searchBarTxtInputSize)); // Changing font size
        textField.setDocument(new JTextFieldLimit(255));  // Setting character limit
        textField.setHorizontalAlignment(JTextField.CENTER); // Centering JTextField Text

        // Adding to GUI
        searchBarWestJPanel.add(textField); // adding textField to JPanel*/

        //#########################################
        // Search Icon Setup
        //#########################################
        iconSetup();

        //#################################################################
        // SearchBar Results
        //#################################################################
        searchBarResults = new JPanel(new GridBagLayout());
        mainCenterPanel.add(searchBarResults, BorderLayout.CENTER);
        //searchBarResults.setBackground(Color.RED);
        
        //##############################################################################################################
        //  Resizing GUI
        //##############################################################################################################
        resizeGUI(); // Resize GUI
    }

    private void iconSetup()
    {
        //#########################################
        // (East)  Search Icon
        //#########################################

        IconButton searchIcon = new IconButton("/images/search/search2.png", searchBarIconWidth, searchBarIconHeight, searchBarButtonWidth, searchBarButtonHeight,
                "centre", "right");
//        searchIcon.makeBTntransparent();

        JButton searchIconBTN = searchIcon.returnJButton();
        searchIconBTN.addActionListener(ae -> {

            searchButtonAction();
        });

        addToContainer(searchBarJPanel, searchIcon, searchBarJPanelXPos += 1, searchBarJPanelYPos, 1, 1, 0.25, 0.25, "vertical", 0, 0);
    }

    public LinkedHashMap<String, Object> get_API_V2NaturalNutrients(String food)
    {
        return nutritionIx_api.get_POST_V2NaturalNutrients(food);
    }

    public ArrayList<LinkedHashMap<String, Object>> get_API_V2Instant(String product)
    {
        return nutritionIx_api.get_POST_V2SearchInstant(product);
    }

    private void displayResults(LinkedHashMap<String, Object> foodInfo)
    {
        try
        {
            JPanel displayJPanel = new JPanel(new GridBagLayout());
            displayJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
//            displayJPanel.setBackground(Color.BLACK);

            searchResultsJPanels.add(displayJPanel);

            addToContainer(searchBarResults, displayJPanel, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            int gridX = -1, height = 150;

            //##########################################################################
            // Ingredient URL Image
            //##########################################################################
            int ingredientImageWidth = 425;

            //#############################
            // Image JPanel
            //#############################
            JPanel picJPanel2 = new JPanel(new GridLayout(1, 1));
            picJPanel2.setBackground(Color.GREEN);
            picJPanel2.setPreferredSize(new Dimension(ingredientImageWidth, height));

            // Adding URL Image to Display
            addToContainer(displayJPanel, picJPanel2, gridX += 1, yPos, 1, 1, 0.25, 0.25, "", 0, 0);

            //#############################
            // Creating URL Image
            //#############################
            String urlLink = (String) foodInfo.get("highres");
            URL url = new URL(urlLink);
            BufferedImage image = ImageIO.read(url);
            ImageIcon icon2 = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(ingredientImageWidth, height, Image.SCALE_DEFAULT));

            JLabel lbl2 = new JLabel();
            lbl2.setIcon(icon2);
            picJPanel2.add(lbl2);


            //########################################################################
            // Space Divider
            //########################################################################
            JPanel spaceDividerInsideImageDisplay = new JPanel();
//            spaceDividerInsideImageDisplay.setBackground(Color.CYAN);
            spaceDividerInsideImageDisplay.setPreferredSize(new Dimension(50, height));


            addToContainer(displayJPanel, spaceDividerInsideImageDisplay, gridX += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //##########################################################################
            // Add Image Icon
            //##########################################################################
            int addIconJPanelWidth = 100, addIconJPanelHeight = 100, addIconHeight = 71, addIconWidth = 71;

            //####################################
            // Add Icon JPanel
            //####################################
            JPanel iconJPanel = new JPanel(new GridBagLayout());
//            iconJPanel.setBackground(Color.GREEN);
            iconJPanel.setPreferredSize(new Dimension(addIconJPanelWidth, addIconJPanelHeight));

            addToContainer(displayJPanel, iconJPanel, gridX += 1, yPos, 1, 1, 0.25, 0.25, "", 0, 0);

            //####################################
            // Add Icon BTN
            //####################################
            IconButton addIcon = new IconButton("/images/add/++++add.png", addIconWidth, addIconHeight, addIconWidth, addIconHeight, "centre", "right");

            addIcon.makeBTntransparent();

            JButton addButton = addIcon.returnJButton();
            addButton.addActionListener(ae -> {

                addButtonAction(foodInfo);
            });

            addToContainer(iconJPanel, addIcon, 0, 0, 1, 1, 0.25, 0.25, "", 0, 0);

            //########################################################################
            // Space Divider
            //########################################################################
            JPanel spaceDividerInsideImageDisplay2 = new JPanel();
//            spaceDividerInsideImageDisplay2.setBackground(Color.CYAN);
            spaceDividerInsideImageDisplay2.setPreferredSize(new Dimension(50, height));

            addToContainer(displayJPanel, spaceDividerInsideImageDisplay2, gridX += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //#########################################################################################################
            // Space Divider
            //#########################################################################################################
            JPanel spaceDivider = new JPanel();
            spaceDivider.setBackground(Color.PINK);
            searchResultsJPanels.add(spaceDivider);
            addToContainer(searchBarResults, spaceDivider, 0, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 50, 0);

        }
        catch (Exception e)
        {
            System.out.printf("\n\ndisplayResults() Error \n", e);
        }

        //##############################################################################################################
        //  Resizing GUI
        //#############################################################################################################
        resizeGUI();
    }

    private void displayResults2(ArrayList<LinkedHashMap<String, Object>> products)
    {
        int height = 150;
        for (LinkedHashMap<String, Object> foodInfo : products)
        {
            try
            {
                int gridX = -1;

                JPanel displayJPanel = new JPanel(new GridBagLayout());
                displayJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));

                searchResultsJPanels.add(displayJPanel);

                addToContainer(searchBarResults, displayJPanel, gridX += 1, yPos += 1, 1, 1, 0.25, 0.25, "horizontal", 50, 0);

                //##########################################################################
                // Product Title & Picture
                //##########################################################################
                int ingredientImageWidth = 425;
                JPanel labelAndPicJPanel = new JPanel(new BorderLayout());
//                picJPanel2.setBackground(Color.GREEN);
                labelAndPicJPanel.setPreferredSize(new Dimension(ingredientImageWidth, height));

                // Adding URL Image to Display
                addToContainer(displayJPanel, labelAndPicJPanel, gridX += 1, yPos, 1, 1, 0.25, 0.25, "", 0, 0);

                //#############################
                // Creating JLabel
                //#############################
                JPanel labelJPanel = new JPanel(new GridLayout(1, 1));
                labelJPanel.setBackground(Color.GREEN);
                labelAndPicJPanel.add(labelJPanel, BorderLayout.NORTH);

                JLabel titleLabel = new JLabel(String.format("%s", (String) foodInfo.get("food_name")));
                titleLabel.setFont(new Font("Verdana", Font.BOLD, 17));
                titleLabel.setHorizontalAlignment(JLabel.CENTER);

                labelJPanel.add(titleLabel);

                //#############################
                // Creating URL Image
                //#############################
                String urlLink = (String) foodInfo.get("thumb");
                URL url = new URL(urlLink);
                BufferedImage image = ImageIO.read(url);
                ImageIcon icon2 = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(ingredientImageWidth, height, Image.SCALE_DEFAULT));

                JLabel lbl2 = new JLabel();
                lbl2.setIcon(icon2);
                labelAndPicJPanel.add(lbl2, BorderLayout.CENTER);

                //########################################################################
                // Space Divider
                //########################################################################
                JPanel spaceDividerInsideImageDisplay = new JPanel();
//            spaceDividerInsideImageDisplay.setBackground(Color.CYAN);
                spaceDividerInsideImageDisplay.setPreferredSize(new Dimension(50, height));


                addToContainer(displayJPanel, spaceDividerInsideImageDisplay, gridX += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

                //##########################################################################
                // Add Image Icon
                //##########################################################################
                int addIconJPanelWidth = 100, addIconJPanelHeight = 100, addIconHeight = 71, addIconWidth = 71;

                //####################################
                // Add Icon JPanel
                //####################################
                JPanel iconJPanel = new JPanel(new GridBagLayout());
//            iconJPanel.setBackground(Color.GREEN);
                iconJPanel.setPreferredSize(new Dimension(addIconJPanelWidth, addIconJPanelHeight));

                addToContainer(displayJPanel, iconJPanel, gridX += 1, yPos, 1, 1, 0.25, 0.25, "", 0, 0);

                //####################################
                // Add Icon BTN
                //####################################
                IconButton addIcon = new IconButton("/images/add/++++add.png", addIconWidth, addIconHeight, addIconWidth, addIconHeight, "centre", "right");

                addIcon.makeBTntransparent();

                JButton addButton = addIcon.returnJButton();
                addButton.addActionListener(ae -> {

                    String nix_item_id = (String) foodInfo.get("nix_item_id");

                    if (nix_item_id != null)
                    {
                        LinkedHashMap<String, Object> foodInfo2 = nutritionIx_api.get_GET_V2SearchItem(nix_item_id);

                        if (foodInfo2 != null)
                        {
                            addButtonAction(foodInfo2);
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Error, Unable to gather product info!");
                });

                addToContainer(iconJPanel, addIcon, 0, 0, 1, 1, 0.25, 0.25, "", 0, 0);

                //########################################################################
                // Space Divider
                //########################################################################
                JPanel spaceDividerInsideImageDisplay2 = new JPanel();
//            spaceDividerInsideImageDisplay2.setBackground(Color.CYAN);
                spaceDividerInsideImageDisplay2.setPreferredSize(new Dimension(50, height));

                addToContainer(displayJPanel, spaceDividerInsideImageDisplay2, gridX += 1, yPos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            }
            catch (Exception e)
            {
                System.out.printf("\n\ndisplayResults() Error \n", e);
            }

            //##############################################################################################################
            //  Resizing GUI
            //#############################################################################################################
            resizeGUI();
        }
    }

    private void searchButtonAction()
    {
        String food = textField.getText().trim();
        int selectedIndex = jComboBox.getSelectedIndex();

        //##########################################################
        //
        //##########################################################
        if (selectedIndex == -1)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nPlease select an option inside the dropdown box for the food / ingredient ' %s '.", food));
            return;
        }

        if (food.equals(""))
        {
            JOptionPane.showMessageDialog(null, "\n\nError \n\nPlease input a food / ingredient name inside the text-box !");
            return;
        }


        //##########################################################
        // Reset Results Display
        //##########################################################
        resetSearchDisplay();

        //##########################################################
        // If Ingredient Option do a check if not get info
        //##########################################################

        if (selectedIndex == 0)
        {
            // Check if string is one ingredient
            if (food.split("\\s+").length > 1)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nInputted search string for an ingredient must be only one word:  \" %s \" !", food));
                return;
            }

            //  Get Nutritional Info From API
            LinkedHashMap<String, Object> foodInfo = get_API_V2NaturalNutrients(String.format("100g of %s", food));

            //##################################
            // Error Message
            //##################################
            if (foodInfo == null)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nUnable to get nutritional info for the requested food \" %s \" !", food));
                return;
            }

            displayResults(foodInfo);

        }
        else if (selectedIndex == 1)
        {
            ArrayList<LinkedHashMap<String, Object>> foodInfo = get_API_V2Instant(food);

            if (foodInfo == null)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nUnable to get nutritional info for the requested food \" %s \" !", food));
                return;
            }

            displayResults2(foodInfo);
        }

      /*  //########################################################
        // Successful Message
        //########################################################

        JOptionPane.showMessageDialog(null, String.format("\n\nSuccessfully got the nutritional info for the food '%s'!", food));*/

    }

    private void addButtonAction(LinkedHashMap<String, Object> foodInfo)
    {
        System.out.printf("\n\n########################''");
        foodInfo.entrySet().forEach(entry -> {
            System.out.printf("\n%s : %s", entry.getKey(), entry.getValue());
        });

        resetFullDisplay();

        addIngredientsForm.update_Form_With_Nutrition_IX_Search(foodInfo);
    }

    public void resetFullDisplay()
    {
        resetSearchDisplay();


        jComboBox.setSelectedIndex(-1);
        textField.setText("");
    }

    private void resetSearchDisplay()
    {
        //################################
        // Removing Previous Results From
        // JPanel
        //################################
        for (JPanel jp : searchResultsJPanels)
        {
            jp.setSize(new Dimension(0, 0));
            searchBarResults.remove(jp);
            searchBarResults.revalidate();
        }

        yPos = 0;

        resizeGUI();
    }

    private void resizeGUI()
    {
        searchBarResults.revalidate();
        centrePanel.revalidate();
        parentContainer.revalidate();
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

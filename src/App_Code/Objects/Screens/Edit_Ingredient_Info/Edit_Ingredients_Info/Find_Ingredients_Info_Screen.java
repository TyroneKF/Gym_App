package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.API.Nutritionix.NutritionIx_API;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Find_Ingredients_Info_Screen extends JPanel
{
    private GridBagConstraints gbc = new GridBagConstraints();
    private JPanel screenSectioned, centerJPanel;

    private NutritionIx_API nutritionIx_api;
    private Edit_Ingredients_Screen.CreateForm.IngredientsForm ingredientsForm;
    private ArrayList<JPanel> resultsJPanelDisplay = new ArrayList();

    private JTextField textField;
    private JComboBox jComboBox;
    private String chosenOption = "";
    private ArrayList<JPanel> searchResultsJPanels = new ArrayList<>();

    private int
            frameWidth = 710 - 40, frameHeight = 850,

    titleJPanelHeight = 45, titleFontSize = 16,

    searchBarTxtInputSize = 15,

    searchBarHeight = titleJPanelHeight,
            searchBarButtonWidth = 45,

    searchBarWidth = frameWidth - 105,

    searchBarIconWidth = searchBarButtonWidth - 10,
            searchBarButtonHeight = 45,
            searchBarIconHeight = searchBarButtonHeight - 10;


    private JPanel searchBarResults, scrollPaneJPanel;
    private Container parentContainer;

    private int yPos = 1;

    public static void main(String[] args)
    {
        //#############################################################################################################
        //   1. Create the frame.
        //#############################################################################################################

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(710, 850);
        frame.setLocation(00, 0);

        //########################################################
        //  Contentpane
        //########################################################

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1, 1));
        contentPane.setVisible(true);

        //########################################################
        // Creating TabbedPane
        //########################################################
        JTabbedPane tp = new JTabbedPane();
        contentPane.add(tp);

        tp.add("Add Ingredients", new Find_Ingredients_Info_Screen(contentPane, null));


    }

    public Find_Ingredients_Info_Screen(Container parentContainer, Edit_Ingredients_Screen.CreateForm.IngredientsForm ingredientsForm)
    {
        this.parentContainer = parentContainer;
        this.ingredientsForm = ingredientsForm;

        nutritionIx_api = new NutritionIx_API();

        createGUI();
    }

    public void createGUI()
    {
        super.setLayout(new GridBagLayout());

        //##############################################################################################################
        // Create ScrollPane & add to Interface
        //##############################################################################################################
        ScrollPaneCreator scrollPane = new ScrollPaneCreator();
        addToContainer(this, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new BorderLayout());

        //##############################################################################################################
        //  Creating JPanel Sections
        //##############################################################################################################


        //#####################################################################
        //  North JPanel
        //#####################################################################
        JPanel mainNorthPanel = new JPanel(new BorderLayout());
        mainNorthPanel.setBackground(Color.RED);
        scrollPaneJPanel.add(mainNorthPanel, BorderLayout.NORTH);

        //####################################
        // Label
        //####################################
        JLabel titleLabel = new JLabel("Search For Ingredient Info");
        titleLabel.setFont(new Font("Verdana", Font.PLAIN, titleFontSize));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(frameWidth, titleJPanelHeight));
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
        JPanel searchBarJPanel = new JPanel(new GridBagLayout());
        searchBarJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
        mainCenterPanel.add(searchBarJPanel, BorderLayout.NORTH);

        int searchBarJPanelXPos = 0, searchBarJPanelYPos = 0;

        //#################################################################
        // Search Bar JComboBox
        //#################################################################
        JPanel jp = new JPanel(new GridLayout(1, 1));
        jp.setBorder(BorderFactory.createLineBorder(Color.red, 3));
//        jp.setPreferredSize(new Dimension(frameWidth-50, 50));

        addToContainer(searchBarJPanel, jp, searchBarJPanelXPos, 0, 1, 1, 1, 1, "both", 0, 0);

        String jComboBoxOptions[] = {"Single Ingredient", "Product"};
        jComboBox = new JComboBox(jComboBoxOptions);
        jComboBox.setFont(new Font("Verdana", Font.PLAIN, 15));
        jp.add(jComboBox);

        ((JLabel) jComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // centre text
        jComboBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ie)
            {
                int selectedItemIndex = jComboBox.getSelectedIndex();

                if (selectedItemIndex != -1)
                {
                    chosenOption = jComboBox.getItemAt(selectedItemIndex).toString();
                }
            }
        });

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
        textField.setText("oats");// HELLO Remove

        // Adding to GUI
        searchBarWestJPanel.add(textField); // adding textField to JPanel*/

        //#########################################
        // (East)  Search Icon
        //#########################################

        IconButton searchIcon = new IconButton("src/images/search/search2.png", "", searchBarIconWidth, searchBarIconHeight, searchBarButtonWidth, searchBarButtonHeight,
                "centre", "right");
//        searchIcon.makeBTntransparent();

        JButton searchIconBTN = searchIcon.returnJButton();
        searchIconBTN.addActionListener(ae -> {

            searchButtonAction();
        });

        addToContainer(searchBarJPanel, searchIcon, searchBarJPanelXPos += 1, searchBarJPanelYPos, 1, 1, 0.25, 0.25, "vertical", 0, 0);

        //#################################################################
        // SearchBar Results
        //#################################################################
        searchBarResults = new JPanel(new GridBagLayout());
        mainCenterPanel.add(searchBarResults, BorderLayout.CENTER);
        searchBarResults.setBackground(Color.RED);


        //##############################################################################################################
        //  Resizing GUI
        //##############################################################################################################

        resizeGUI(); // Resize GUI
    }

    public LinkedHashMap<String, Object> get_API_V2NaturalNutrients(String food)
    {
        return nutritionIx_api.get_POST_V2NaturalNutrients(food);
    }

    public LinkedHashMap<String, Object> get_API_V2Instant(String product)
    {
        return null;
    }

    private boolean doesStringContainCharacters(String input)
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
            IconButton addIcon = new IconButton("src/images/add/++++add.png", "", addIconWidth, addIconHeight, addIconWidth, addIconHeight, "centre", "right");

            addIcon.makeBTntransparent();

            JButton addButton = addIcon.returnJButton();
            addButton.addActionListener(ae -> {

            });

//            iconJPanel.add(addIcon);

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

    private void displayResults2(LinkedHashMap<String, Object> foodInfo)
    {
        try
        {
            JPanel displayJPanel = new JPanel(new GridBagLayout());
            displayJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
//            displayJPanel.setBackground(Color.BLACK);
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
            String urlLink = "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80";
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
            IconButton addIcon = new IconButton("src/images/add/++++add.png", "", addIconWidth, addIconHeight, addIconWidth, addIconHeight, "centre", "right");

            addIcon.makeBTntransparent();

            JButton addButton = addIcon.returnJButton();
            addButton.addActionListener(ae -> {

            });

//            iconJPanel.add(addIcon);

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

    private void searchButtonAction()
    {
        String food = textField.getText().trim();
        int selectedIndex = jComboBox.getSelectedIndex();

        //##########################################################
        // Choose an option ingredient or product
        //##########################################################
        if (selectedIndex == -1)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nPlease select an option inside the dropdown box for the food / ingredient ' %s '.", food));
            return;
        }

        //##########################################################
        // Check if JTextField is empty or, contains any characters
        //##########################################################
        if (food.equals("") || doesStringContainCharacters(food))
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nInputted search string for an ingredient / food cannot be empty !  Or, the inputted  ingredient / food being \" %s \" contains characters !", food));
            return;
        }

        //##########################################################
        // Reset Results Display
        //##########################################################
        resetSearchDisplay();

        //##########################################################
        // If Ingredient Option do a check if not get info
        //##########################################################
        LinkedHashMap<String, Object> foodInfo = null;

        if (selectedIndex == 0)
        {
            // Check if string is one ingredient
            if (food.split("\\s+").length > 1)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nInputted search string for an ingredient must be only one word:  \" %s \" !", food));
                return;
            }

            //  Get Nutritional Info From API
            foodInfo = get_API_V2NaturalNutrients(String.format("100g of %s", food));
        }
        else if (selectedIndex == 1)
        {
            foodInfo = get_API_V2Instant(food);
        }

        //##################################
        // Error Message
        //##################################
        if (foodInfo == null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nUnable to get nutritional info for the requested food \" %s \" !", food));
            return;
        }

        //##########################################################
        // Display Results
        //##########################################################
        if (selectedIndex == 0)
        {
            displayResults(foodInfo);
        }
        else
        {
            displayResults2(foodInfo);
        }

      /*  //########################################################
        // Successful Message
        //########################################################

        JOptionPane.showMessageDialog(null, String.format("\n\nSuccessfully got the nutritional info for the food '%s'!", food));*/

    }

    private void resizeGUI()
    {
        searchBarResults.revalidate();
        super.revalidate();
        parentContainer.revalidate();
    }

    private void resetFullDisplay()
    {
        jComboBox.setSelectedIndex(-1);
        textField.setText("");
        chosenOption = "";

        resetSearchDisplay();
    }

    private void resetSearchDisplay()
    {
        //################################
        // Removing Previous Results From
        // JPanel
        //################################
        for(JPanel jp: searchResultsJPanels)
        {
            searchBarResults.remove(jp);
        }

        yPos = 0;

        resizeGUI();
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

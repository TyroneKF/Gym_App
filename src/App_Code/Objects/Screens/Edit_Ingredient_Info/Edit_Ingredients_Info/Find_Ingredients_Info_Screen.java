package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.API.Nutritionix.NutritionIx_API;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;
import App_Code.Objects.Gui_Objects.ScrollPaneCreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    private int ypos = 1;


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
        mainCenterPanel.add(searchBarJPanel, BorderLayout.NORTH);

        //###########################################
        // Search Bar West Panel
        //##########################################
        // Creating JPanel for text input area for search bar
        JPanel searchBarWestJPanel = new JPanel(new GridLayout(1, 1));
        searchBarWestJPanel.setPreferredSize(new Dimension(searchBarWidth, searchBarHeight));
        searchBarWestJPanel.setBackground(Color.BLUE);

        //####################
        // Text Field
        //####################

        // Creating Text input object for search bar
        textField = new JTextField("");

        textField.setFont(new Font("Verdana", Font.PLAIN, searchBarTxtInputSize)); // Changing font size
        textField.setDocument(new JTextFieldLimit(255));  // Setting charecter limit
        textField.setHorizontalAlignment(JTextField.CENTER); // Centering JTextField Text
        textField.setText("oats");// HELLO Remove

        // Adding to GUI
        searchBarWestJPanel.add(textField); // adding textField to JPanel*/
        addToContainer(searchBarJPanel, searchBarWestJPanel, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //#########################################
        // Search Bar East Panel
        //#########################################

        IconButton searchIcon = new IconButton("src/images/search/search2.png", "", searchBarIconWidth, searchBarIconHeight, searchBarButtonWidth, searchBarButtonHeight,
                "centre", "right");

        JButton searchIconBTN = searchIcon.returnJButton();
        searchIconBTN.addActionListener(ae -> {

            String food = textField.getText().trim();

            // Check if JTextField is empty or, contains any characters
            if (food.equals("") || doesStringContainCharacters(food))
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nInputted search string for an ingredient cannot be empty !  Or, the inputted  ingredient being \" %s \" contains characters !", food));
                return;
            }

            // Check if string is one ingredient
            if (food.split("\\s+").length > 1)
            {
                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nInputted search string for an ingredient must be only one word:  \" %s \" !", food));
                return;
            }


            // Get Nutritional Info From API
            //HELLO  UNCOMMENT
//            LinkedHashMap<String, Object> foodInfo = findFoodInfo(String.format("100g of %s", food));
//
//            //##################################
//            // Error Message
//            //##################################
//            if (foodInfo == null)
//            {
//                JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nUnable to get nutritional info for the requested food \" %s \" !", food));
//                return;
//            }

            //##################################
            // Display Results
            //##################################
//            displayResults(foodInfo);
            displayResults2();

            //##################################
            // Successful Message
            //##################################

            JOptionPane.showMessageDialog(null, String.format("\n\nSuccessfully got the nutritional info for the food '%s'!", food));

        });

        addToContainer(searchBarJPanel, searchIcon, 1, 0, 1, 1, 0.25, 0.25, "vertical", 0, 0);

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

    private void displayResults2()
    {
        try
        {
            JPanel displayJPanel = new JPanel(new GridBagLayout());
            displayJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
            displayJPanel.setBackground(Color.BLACK);
            addToContainer(searchBarResults, displayJPanel, 0, ypos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            int gridX= -1;

             //###############################################
            // URL Image
            //################################################
            int imageWidth = 300, imageHeight = 200;

            JPanel picJPanel2 = new JPanel(new GridLayout(1, 1));
            picJPanel2.setBackground(Color.GREEN);
            picJPanel2.setPreferredSize(new Dimension(imageWidth, imageHeight));

            //#############################
            String urlLink = "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80";
            URL url = new URL(urlLink);
            BufferedImage image = ImageIO.read(url);
            ImageIcon icon2 = new ImageIcon(new ImageIcon(image).getImage()
                    .getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT));

            JLabel lbl2 = new JLabel();
            lbl2.setIcon(icon2);
            picJPanel2.add(lbl2);

            addToContainer(displayJPanel, picJPanel2, gridX+=1, ypos, 1, 1, 0.25, 0.25, "vertical", 0, 0);

            //###############################################
            // Space Divider
            //################################################
            JPanel spaceDividerInsideImageDisplay = new JPanel();
            spaceDividerInsideImageDisplay.setBackground(Color.CYAN);
            spaceDividerInsideImageDisplay.setPreferredSize(new Dimension(100,200));


            addToContainer(displayJPanel, spaceDividerInsideImageDisplay, gridX+=1, ypos, 1, 1, 0.25, 0.25, "vertical", 0, 0);

            //###############################################
            // Add Image Icon
            //################################################
            int imageWidth2 = 200, imageHeight2 = 200;
            JPanel picJPanel = new JPanel(new GridLayout(1, 1));
            picJPanel.setBackground(Color.BLUE);
            picJPanel.setPreferredSize(new Dimension(imageWidth2, imageHeight2));


            ImageIcon icon = new ImageIcon(new ImageIcon("src/images/nature-image-for-website.jpg").getImage()
                    .getScaledInstance(imageWidth2, imageHeight2, Image.SCALE_DEFAULT));

            JLabel lbl = new JLabel();
            lbl.setIcon(icon);
            picJPanel.add(lbl);


            addToContainer(displayJPanel, picJPanel, gridX+=1, ypos, 1, 1, 0.25, 0.25, "vertical", 0, 0);

            //#############################################################################
            // Space Divider
            //#############################################################################
            JPanel spaceDivider = new JPanel();
            spaceDivider.setBackground(Color.PINK);
            addToContainer(searchBarResults, spaceDivider, 0, ypos += 1, 1, 1, 0.25, 0.25, "horizontal", 50, 0);

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

    private void displayResults(LinkedHashMap<String, Object> foodInfo)
    {
        try
        {
            JPanel displayJPanel = new JPanel(new GridBagLayout());
            displayJPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
            displayJPanel.setBackground(Color.BLACK);
            addToContainer(searchBarResults, displayJPanel, 0, ypos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //#####################################
            //
            //#####################################
            JPanel picJPanel = new JPanel(new GridLayout(1, 1));
            picJPanel.setBackground(Color.BLUE);
            picJPanel.setPreferredSize(new Dimension(200, 200));


            ImageIcon icon = new ImageIcon(new ImageIcon("src/images/nature-image-for-website.jpg").getImage()
                    .getScaledInstance(200, 200, Image.SCALE_DEFAULT));

            JLabel lbl = new JLabel();
            lbl.setIcon(icon);
            picJPanel.add(lbl);


            addToContainer(displayJPanel, picJPanel, 0, ypos += 1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //#####################################
            //
            //#####################################
            JPanel picJPanel2 = new JPanel(new GridLayout(1, 1));
            picJPanel2.setBackground(Color.GREEN);
            picJPanel2.setPreferredSize(new Dimension(300, 200));


            ImageIcon icon2 = new ImageIcon(new ImageIcon("src/images/nature-image-for-website.jpg").getImage()
                    .getScaledInstance(500, 200, Image.SCALE_DEFAULT));

            JLabel lbl2 = new JLabel();
            lbl2.setIcon(icon2);
            picJPanel2.add(lbl2);


            addToContainer(displayJPanel, picJPanel2, 1, ypos, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

            //#####################################
            // Space Divider
            //#####################################
            JPanel spaceDivider = new JPanel();
            spaceDivider.setBackground(Color.PINK);
            addToContainer(searchBarResults, spaceDivider, 0, ypos += 1, 1, 1, 0.25, 0.25, "horizontal", 50, 0);

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

    private void resizeGUI()
    {
        searchBarResults.revalidate();
        super.revalidate();
        parentContainer.revalidate();
    }

    private void clearSearchResultsDisplay()
    {

    }

    public LinkedHashMap<String, Object> findFoodInfo(String food)
    {
        return nutritionIx_api.getFoodNutritionalInfo(food);
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


}

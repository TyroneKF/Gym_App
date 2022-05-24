package App_Code.Objects.Screens.Edit_Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.API.Nutritionix.NutritionIx_API;
import App_Code.Objects.Gui_Objects.IconButton;
import App_Code.Objects.Gui_Objects.JTextFieldLimit;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class Find_Ingredients_Info_Screen extends JPanel
{
    private GridBagConstraints gbc = new GridBagConstraints();
    private JPanel screenSectioned, centerJPanel;

    private NutritionIx_API nutritionIx_api;
    private Edit_Ingredients_Screen.CreateForm.IngredientsForm ingredientsForm;

    private int
            frameWidth = 710, frameHeight = 850,

    titleJPanelHeight = 45, titleFontSize = 16,

    searchBarTxtInputSize = 13,

    searchBarHeight = titleJPanelHeight,
            searchBarButtonWidth = 70,

    searchBarWidth = frameWidth - (searchBarButtonWidth + 20),

    searchBarIconWidth = searchBarButtonWidth - 10,
            searchBarButtonHeight = searchBarHeight,
            searchBarIconHeight = searchBarButtonHeight;


    Container contentPane;


    public static void main(String[] args)
    {
        new Find_Ingredients_Info_Screen(null, null);
    }

    public Find_Ingredients_Info_Screen(Container parentContainer, Edit_Ingredients_Screen.CreateForm.IngredientsForm ingredientsForm)
    {
        this.ingredientsForm = ingredientsForm;
        nutritionIx_api = new NutritionIx_API();

        createGUI();
    }

    public void createGUI()
    {
        //#############################################################################################################
        //   1. Create the frame.
        //#############################################################################################################

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(00, 0);

        //########################################################
        //  Contentpane
        //########################################################

        contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1, 1));
        contentPane.setVisible(true);

        //########################################################
        // Creating TabbedPane
        //########################################################
        JTabbedPane tp = new JTabbedPane();
        contentPane.add(tp);

        //##############################################################################################################
        //  Creating JPanel Sections
        //##############################################################################################################

        screenSectioned = new JPanel(new BorderLayout());
        tp.add("Add Ingredients", screenSectioned);


        //##############################################################################################################
        //  Creating JPanel Sections
        //##############################################################################################################
        //   screenSectioned = new JPanel(new BorderLayout());

        //#####################################################################
        //  North JPanel
        //#####################################################################
        JPanel mainNorthPanel = new JPanel(new BorderLayout());
        mainNorthPanel.setBackground(Color.RED);
        screenSectioned.add(mainNorthPanel, BorderLayout.NORTH);

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
        screenSectioned.add(mainCenterPanel, BorderLayout.CENTER);

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
        searchBarWestJPanel.setPreferredSize(new Dimension(searchBarWidth , 45));
        searchBarWestJPanel.setBackground(Color.BLUE);

        //####################
        // Text Field
        //####################

        // Creating Text input object for search bar
        JTextField textField = new JTextField("");

        textField.setFont(new Font("Verdana", Font.PLAIN, searchBarTxtInputSize)); // Changing font size
        textField.setDocument(new JTextFieldLimit(255));  // Setting charecter limit
        textField.setHorizontalAlignment(JTextField.CENTER); // Centering JTextField Text

        // Adding to GUI
        searchBarWestJPanel.add(textField); // adding textField to JPanel*/
        addToContainer(searchBarJPanel, searchBarWestJPanel, 0, 0, 1, 1, 0.25, 0.25, "horizontal", 0, 0);

        //#########################################
        // Search Bar East Panel
        //#########################################

        IconButton searchIcon = new IconButton("src/images/search/search2.png", "", 35, 35, 45, 45,
                "centre", "right");

        addToContainer(searchBarJPanel, searchIcon, 1, 0, 1, 1, 0.25, 0.25, "both", 0, 0);


        //#################################################################
        // SearchBar Results
        //#################################################################
        JPanel searchBarResults = new JPanel(new BorderLayout());
        mainCenterPanel.add(searchBarResults, BorderLayout.CENTER);

        searchBarResults.setBackground(Color.RED);




        //##############################################################################################################
        //  Resizing GUI
        //##############################################################################################################

        resizeGUI(); // Resize GUI
    }

    public void resizeGUI()
    {
        contentPane.revalidate();
        screenSectioned.revalidate();
    }

    public void findFoodInfo(String food)
    {
        LinkedHashMap<String, Object> foodInfo = nutritionIx_api.getFoodNutritionalInfo(food);

        if (foodInfo == null)
        {
            JOptionPane.showMessageDialog(null, String.format("\n\nError \n\nUnable to get nutritional info for the requested food '%s'!", food));
            return;
        }

        //##################################
        // Change T
        //##################################

        JOptionPane.showMessageDialog(null, String.format("\n\nSuccessfully got the nutritional info for the food '%s'!", food));


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

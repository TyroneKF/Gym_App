package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Gui_Objects.*;
import App_Code.Objects.Screens.Ingredient_Info.SearchForFoodInfo;
import App_Code.Objects.Screens.Others.Meal_Plan_Screen;
import org.javatuples.Triplet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Add_Ingredients extends JPanel
{
    protected int yPos = 0;
    protected JPanel scrollPaneJPanel;
    protected GridBagConstraints gbc = new GridBagConstraints();

    protected boolean formEditable = false;

    protected IngredientsForm ingredientsForm;
    private ShopForm shopForm;
    protected SearchForFoodInfo searchForIngredientInfo;


    //##############################################################
    // NEW SHIT
    //##############################################################
    protected Ingredients_Info_Screen ingredients_info_screen;
    protected Meal_Plan_Screen mealPlanScreen;
    protected MyJDBC db;
    protected Integer planID, tempPlanID;
    protected String planName;


    //#################################################################################################################
    // Constructor
    //##################################################################################################################

    Add_Ingredients(Ingredients_Info_Screen ingredients_info_screen, MyJDBC db)
    {
        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.db = db;
        this.ingredients_info_screen = ingredients_info_screen;

        //###################################################################################
        //   Parent Stuff
        //###################################################################################
        this.planID = ingredients_info_screen.getPlanID();
        this.tempPlanID = ingredients_info_screen.getTempPlanID();
        this.planName = ingredients_info_screen.getPlanName();

        this.mealPlanScreen = ingredients_info_screen.getMealPlanScreen();

        //###################################################################################
        //   Create Screen for Interface
        //###################################################################################

        setLayout(new BorderLayout());

        //###################################################################################
        //   Create Main Centre Screen for Interface
        //##################################################################################
        JPanel mainCentreScreen = new JPanel(new GridBagLayout());
        add(mainCentreScreen, BorderLayout.CENTER);

        //##########################################################
        // Create ScrollPane & add to Interface
        //#########################################################
        ScrollPaneCreator scrollPane = new ScrollPaneCreator();
        scrollPaneJPanel = scrollPane.getJPanel();
        scrollPaneJPanel.setLayout(new GridBagLayout());
        addToContainer(mainCentreScreen, scrollPane, 0, 0, 1, 1, 0.25, 0.25, "both", 0, 0);

        //#################################
        // Needs to be created near this
        //##################################
//            ingredientsForm = new IngredientsForm(this, "Add Ingredients Info", 250, 50);// HELLO REMOVE
        ingredientsForm = new IngredientsForm(scrollPaneJPanel, ingredients_info_screen, "Add Ingredients Info", 250, 50);

        shopForm = new ShopForm(scrollPaneJPanel, ingredients_info_screen, this, "Add Ingredient Suppliers", 250, 50);

        searchForIngredientInfo = new SearchForFoodInfo(scrollPaneJPanel, ingredientsForm, "Search For Food Info", 250, 50);

        createForms(ingredientsForm, shopForm, searchForIngredientInfo);
    }

    //####################################################
    // Methods
    //###################################################

    protected void updateIngredientForm_Type_JComboBox()
    {
        ingredientsForm.loadIngredientsTypeJComboBox();
    }

    protected void createForms(IngredientsForm ingredientsForm, ShopForm shopForm, SearchForFoodInfo searchForFoodInfo)
    {
        //##################################################################################
        // Creating Parts of screen & adding it to interface
        //##################################################################################

        //###########################
        //Search For Ingredients form
        //###########################

        addToContainer(scrollPaneJPanel, searchForFoodInfo, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###########################
        //Ingredients form
        //###########################

        addToContainer(scrollPaneJPanel, ingredientsForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###########################
        // Add shop
        //###########################
        addToContainer(scrollPaneJPanel, shopForm, 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 0, 0);

        //###########################
        //Space Divider
        //###########################
        addToContainer(scrollPaneJPanel, new JPanel(), 0, yPos += 1, 1, 1, 0.25, 0.25, "both", 10, 0);

        //###################################################################################
        // South Screen for Interface
        //###################################################################################

        // Creating submit button
        JButton submitButton = new JButton("Submit Form");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14)); // setting font
        submitButton.setPreferredSize(new Dimension(50, 50)); // width, height

        // creating commands for submit button to execute on
        submitButton.addActionListener(ae -> {
            submissionBtnAction();
        });

        add(submitButton, BorderLayout.SOUTH);

        //###################################################################################
        // Resizing GUI
        //###################################################################################
        resize_GUI();
    }

    protected void clearSearchForIngredientInfoForm()
    {
        searchForIngredientInfo.resetFullDisplay();
    }

    protected void clearIngredientsForm()
    {
        ingredientsForm.clearIngredientsForm();
    }

    protected void clearShopForm()
    {
        shopForm.clearShopForm();
    }

    protected void submissionBtnAction()
    {
        if (!areYouSure("add this new Ingredient - this will cause the mealPlan to save its data to the DB"))
        {
            return;
        }

        boolean errorFound = false;

        // ingredientsForm
        if (!(ingredientsForm.validate_IngredientsForm()))
        {
            errorFound = true;
        }

        // ShopForm
        if (!(shopForm.validateForm()))
        {
            errorFound = true;
        }

        if (!errorFound)
        {
            if (!areYouSure("upload these values as they may have been changed / adapted to fit our data type format"))
            {
                return;
            }

            if (updateBothForms(ingredientsForm.get_IngredientsForm_UpdateString(null), shopForm.get_ShopForm_UpdateString()))
            {
                ingredients_info_screen.setUpdateIngredientInfo(true);
                JOptionPane.showMessageDialog(mealPlanScreen, "The ingredient updates won't appear on the mealPlan screen until this window is closed!");

                //#####################################
                // Reset Ingredient Names/Types
                //####################################
                String ingredientName = ((JTextField) ingredientsForm.getIngredientsFormObjects().get(1)).getText();
                String ingredientType = (String) ((JComboBox) ingredientsForm.getIngredientsFormObjects().get(2)).getSelectedItem();

                addOrDeleteIngredientFromMap("add", ingredientType, ingredientName);

                //#####################################
                // Reset Form & Update GUI
                //####################################
                refreshInterface();
                resize_GUI();
            }
        }
    }

    //HELLO EDIT
    protected boolean addOrDeleteIngredientFromMap(String process, String ingredientType, String ingredientName)
    {
        // Storing
        Collection<String> ingredientTypeList = ingredients_info_screen.getMapIngredientTypesToNames().get(ingredientType);
        if (process.equals("add"))// if key exists add the ingredientName in
        {
            if (ingredientTypeList != null)
            {
                // Add ingredientName to collection
                ingredientTypeList.add(ingredientName);
            }
            else // create the list for the new type and add ingredient in
            {
                ingredientTypeList = new TreeSet<>(Collator.getInstance());
                ingredientTypeList.add(ingredientName);
                ingredients_info_screen.getMapIngredientTypesToNames().put(ingredientType, ingredientTypeList);
            }
        }
        else if (process.equals("delete"))
        {
            ingredientTypeList.remove(ingredientName);

            // Remove List as there is no items in it
            if (ingredientTypeList.size() == 0)
            {
                ingredients_info_screen.getMapIngredientTypesToNames().remove(ingredientType);
            }
        }

        ingredients_info_screen.update_EditIngredientsInfo_IngredientsTypes();
        return true;
    }

    protected Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(mealPlanScreen, String.format("Are you sure you want to: %s?", process, process),
                "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    private void refreshInterface() // only available to reset screen
    {
        clearSearchForIngredientInfoForm();
        clearIngredientsForm();
        clearShopForm();
    }

    protected boolean updateBothForms(String updateIngredients_String, String[] updateIngredientShops_String)
    {
        System.out.printf("\n\n%s", updateIngredients_String, Arrays.toString(updateIngredientShops_String));

        //####################################
        // Uploading Query
        //####################################
        if (!(db.uploadData_Batch_Altogether(new String[]{updateIngredients_String})))
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 2/2 Updates - Unable To Add Ingredient Info In DB!");
            return false;
        }

        if (updateIngredientShops_String != null)
        {
            if (!(db.uploadData_Batch_Independently(updateIngredientShops_String)))
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Failed 1/2 Updates - Unable To Add Shop Supplier For Ingredient In DB!");
                return false;
            }
        }

        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "\n\nUpdated Ingredient Info! \n\nAlso updated 2/2 Shop Info In DB In DB!!!");

        return true;
    }

    protected void resize_GUI()
    {
        ingredients_info_screen.resize_GUI();
        scrollPaneJPanel.revalidate();
        revalidate();
    }

    //#################################################################################################################
    // Other Methods
    //##################################################################################################################

    public void addToContainer(Container container, Component addToContainer, int gridx, int gridy, int gridwidth,
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



    


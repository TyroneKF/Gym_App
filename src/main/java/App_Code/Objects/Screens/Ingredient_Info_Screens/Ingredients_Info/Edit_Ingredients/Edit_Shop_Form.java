package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients;

import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Add_Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Ingredients_Info_Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Edit_Shop_Form extends Add_Shop_Form
{
    //##################################################
    // Variables
    //##################################################
    private Edit_Ingredients_Form edit_IngredientsForm;
    private ArrayList<ArrayList<String>> shopsFormDBData = new ArrayList<>();

    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_Shop_Form(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Edit_Ingredients_Screen edit_ingredients, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, edit_ingredients, btnText, btnWidth, btnHeight);
        this.edit_IngredientsForm = edit_ingredients.get_Ingredients_Form();
    }

    //##################################################################################################################
    // Update Form From DB Info Methods
    //##################################################################################################################
    public void update_ShopForm_With_Info_From_DB()
    {
        // Clear Shop Form For New Requested Info
        clear_Shop_Form();

        //###########################
        //
        //###########################
        String selectedIngredientID = edit_IngredientsForm.get_Selected_IngredientID();

        //###########################
        // Get New Ingredient Shop Info
        //###########################
        ArrayList<ArrayList<String>> temp_ShopsFormDBData = db.getMultiColumnQuery(String.format("""                    
                SELECT i.pdid, s.store_name, i.product_name, i.cost_per_unit, i.volume_per_unit
                FROM  ingredient_in_shops i
                INNER JOIN
                (
                  SELECT store_id, store_name FROM stores
                ) s
                ON s.store_id = i.store_id
                AND  i.ingredient_id = %s ;""", selectedIngredientID));

        if (temp_ShopsFormDBData==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), "Unable to grab selected ingredient shop info! \nMaybe there isn't any suppliers created for this Ingredient!");
            return;
        }

        shopsFormDBData = temp_ShopsFormDBData;

        //###########################
        //Add Rows for shops onto form
        //###########################
        load_ShopForm_Data();
    }

    public void load_ShopForm_Data()
    {
        for (int i = 0; i < shopsFormDBData.size(); i++)
        {
            ArrayList<String> rowData = shopsFormDBData.get(i);

            // PDID is set in constructor & Add Row
            new Edit_ShopForm_Object(inputArea, rowData.get(0), rowData.get(1), rowData.get(2), rowData.get(3), rowData.get(4));
        }
    }

    //##################################################################################################################
    // Methods
    //##################################################################################################################
    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(mealPlanScreen.getFrame(), String.format("Are you sure you want to: %s?", process, process),
                "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    @Override
    protected void extra_Clear_Shops_Form()
    {
        shopsFormDBData.clear();
    }

    //EDITING NOW
    public String[] get_ShopForm_Update_String(String ingredientIDInDB) // Not an override method
    {
        System.out.println("\n\nEDIT get_ShopForm_UpdateString()");
        //#############################################################
        // Checks if there is anything to update before, updating
        //############################################################
        if (shopFormObjects.size()==0)
        {
            System.out.println("\n\nget_ShopForm_UpdateString() No Suppliers in shop");
            return null;
        }

        ///###################################################################
        // Creating Insert Supplier Statement
        //####################################################################

        String
                insertValues = "",
                insertStatement = "INSERT INTO ingredient_in_shops (ingredient_id, product_name, volume_per_unit, cost_per_unit, store_id) VALUES";

        String[] updates = new String[shopFormObjects.size()];

        int pos = -1; // Due to pos+=1, pos will be greater than list size
        Iterator<Add_ShopForm_Object> it = shopFormObjects.iterator();
        boolean hasDataChanged = false;
        while (it.hasNext())
        {
            // Assigning current shopForm object in list to variable
            Add_ShopForm_Object shopForm_Object = it.next();

            // Get PDID of ShopForm Object
            Integer PDID = shopForm_Object.get_PDID();

            if (PDID!=null) // Shop Object is in DB
            {
                // create dummy value for error below
                Edit_ShopForm_Object editShopForm_Object = new Edit_ShopForm_Object();

                // Convert to Edit_ShopForm_Object
                if (shopForm_Object instanceof Edit_ShopForm_Object)
                {
                    editShopForm_Object = (Edit_ShopForm_Object) shopForm_Object;
                }

                // Check if the data changed
                if (!(editShopForm_Object.has_Data_Changed()))
                {
                    continue;
                }
                hasDataChanged = true;

                //Update String
                updates[pos += 1] = String.format("""
                                UPDATE ingredient_in_shops
                                SET  product_name = '%s', volume_per_unit = %s, cost_per_unit = %s, store_id = (SELECT store_id FROM stores WHERE store_name = '%s')
                                WHERE pdid = %s;""",
                        editShopForm_Object.get_ProductName_Txt(),
                        editShopForm_Object.get_Product_Quantity_Per_Pack_Txt(),
                        editShopForm_Object.get_ProductPrice_Txt(),
                        editShopForm_Object.get_ProductShops_TXT(),
                        PDID);
            }
            else
            {
                System.out.println("\n\nget_ShopForm_UpdateString() here here");
                insertValues += String.format("\n(%s, '%s', %s, %s, (SELECT store_id FROM stores WHERE store_name = '%s')),",
                        ingredientIDInDB,
                        shopForm_Object.get_ProductName_Txt(),
                        shopForm_Object.get_Product_Quantity_Per_Pack_Txt(),
                        shopForm_Object.get_ProductPrice_Txt(),
                        shopForm_Object.get_ProductShops_TXT());
            }
        }

        //############################################################
        // Insert Statement
        //############################################################
        if (!(insertValues.equals("")))
        {
            insertValues = insertValues.substring(0, insertValues.length() - 1) + ";;";

            insertStatement += insertValues;
            updates[pos += 1] = (insertStatement);
        }
        else if (!hasDataChanged)
        {
            return null;
        }

        //############################################################
        // Return values
        //############################################################
        return updates;
    }


    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public class Edit_ShopForm_Object extends Add_ShopForm_Object
    {
       String shopName_OG, productName_OG, productPrice_OG, quantityPerPack_OG;

        //#######################################
        // Main  Constructor
        //#######################################
        public Edit_ShopForm_Object(Container parentContainer, String PDID, String shopName, String productName, String productPrice, String quantityPerPack)
        {
            // Setting Variables
            super(parentContainer);
            this.shopName_OG = shopName;
            this.productName_OG = productName;
            this.productPrice_OG = productPrice;
            this.quantityPerPack_OG = quantityPerPack;

            // Set GUI Objects
            set_PDID(Integer.parseInt(PDID));   //Set PDID

            get_ProductShop_JComboBox().setSelectedItem(shopName); // Set ShopName

            get_ProductName_TxtField().setText(productName); // Set Product Name

            get_Product_Price_TxtField().setText(productPrice); // Set Product Price

            get_Product_Quantity_Per_Pack_TxtField().setText(quantityPerPack); // Set Volume Info
        }

        public Edit_ShopForm_Object()
        {
            super(parentContainer);
        }

        //#######################################
        // Methods
        //#######################################
        private boolean has_Data_Changed()
        {
            boolean hasDataChanged = false;

            String shopName_current = get_ProductShops_TXT();
            String productName_current = get_ProductName_Txt();
            String productPrice_current = get_ProductPrice_Txt();
            String quantityPerPack_current = get_Product_Quantity_Per_Pack_Txt();

            if (!(shopName_current.equals(shopName_OG)))
            {
                hasDataChanged = true;
            }
            else if (!(productName_current.equals(productName_OG)))
            {
                hasDataChanged = true;
            }
            else if (!(productPrice_current.equals(productPrice_OG)))
            {
                hasDataChanged = true;
            }
            else if (!(quantityPerPack_current.equals(quantityPerPack_OG)))
            {
                hasDataChanged = true;
            }

            return hasDataChanged;
        }

        @Override
        protected void delete_Row_Action()
        {
            //################################################
            // Confirm if the user wants to delete the shop
            //################################################
            String
                    chosenShop = String.valueOf(get_ProductShops_TXT()),
                    productName = String.valueOf(get_ProductName_Txt());

            if (!(areYouSure(String.format("you want to permanently delete the product: \"%s\" from \"%s\" as a product for this ingredient.", productName, chosenShop))))
            {
                return;
            }

            //################################################
            // Get Ingredient PDID
            //################################################
            Integer PDID = get_PDID();

            //################################################
            // If Object In DB Remove From DB
            //################################################
            if (PDID!=null)
            {
                //################################################
                // Get Ingredient ID
                //################################################
                String selectedIngredientID = edit_IngredientsForm.get_Selected_IngredientID();

                if (selectedIngredientID==null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Unable to get selected ingredients information to delete product:\n\"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
                    return;
                }

                //###################################################
                // Delete Supplier From ingredients_in_meal (PDID)
                //###################################################
                String updateQuery = String.format("""
                        UPDATE ingredients_in_sections_of_meal
                        SET  pdid = NULL
                        WHERE pdid = %s; """, PDID);

                System.out.printf("\n\n%s", updateQuery);

                //###################################################
                // Delete Supplier From ingredientInShops
                //###################################################
                String updateQuery2 = String.format("DELETE FROM ingredient_in_shops  WHERE pdid = %s;", PDID);

                System.out.printf("\n\n%s", updateQuery2);

                //###################################################
                // Update
                //###################################################
                if (!(db.uploadData_Batch_Altogether(new String[]{updateQuery, updateQuery2})))
                {
                    JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Unable to remove product: \"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
                    return;
                }
            }

            //################################################
            // Remove Row Object
            //################################################
            shopFormObjects.remove(this);
            remove_From_Parent_Container();

            //################################################
            // Remove Row Object
            //################################################
            JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Successfully, removed product: \"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
        }
    }
}

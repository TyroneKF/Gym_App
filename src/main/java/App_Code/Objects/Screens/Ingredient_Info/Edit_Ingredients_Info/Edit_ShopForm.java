package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Edit_ShopForm extends Add_ShopForm
{
    private Edit_IngredientsForm edit_IngredientsForm;
    private ArrayList<ArrayList<String>> shopsFormDBData = new ArrayList<>();


    public Edit_ShopForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Edit_IngredientsScreen edit_ingredients, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, edit_ingredients, btnText, btnWidth, btnHeight);
        this.edit_IngredientsForm = edit_ingredients.getIngredientsForm();
    }

    private Boolean areYouSure(String process)
    {
        int reply = JOptionPane.showConfirmDialog(mealPlanScreen, String.format("Are you sure you want to: %s?", process, process),
                "Confirmation", JOptionPane.YES_NO_OPTION); //HELLO Edit

        if (reply==JOptionPane.NO_OPTION || reply==JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        return true;
    }

    @Override
    protected void extraClearShopsForm()
    {
        shopsFormDBData.clear();
    }

    public void updateShopFormWithInfoFromDB()
    {
        // Clear Shop Form For New Requested Info
        clearShopForm();

        //###########################
        //
        //###########################
        String selectedIngredientID = edit_IngredientsForm.getSelectedIngredientID();

        //###########################
        // Get New Ingredient Shop Info
        //###########################
        ArrayList<ArrayList<String>> temp_ShopsFormDBData = db.getMultiColumnQuery(String.format("""                    
                SELECT i.PDID, s.Store_Name, i.Product_Name, i.Cost_Per_Unit, i.Volume_Per_Unit
                FROM  ingredientInShops i
                INNER JOIN
                (
                  SELECT StoreID, Store_Name FROM stores
                ) s
                ON s.StoreID = i.StoreID
                AND  i.IngredientID = %s ;""", selectedIngredientID));

        if (temp_ShopsFormDBData==null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab selected ingredient shop info! \nMaybe there isn't any suppliers created for this Ingredient!");
            return;
        }

        shopsFormDBData = temp_ShopsFormDBData;

        //###########################
        //Add Rows for shops onto form
        //###########################
        loadShopFormData();
    }

    public void loadShopFormData()
    {
        for (int i = 0; i < shopsFormDBData.size(); i++)
        {
            ArrayList<String> rowData = shopsFormDBData.get(i);

            // PDID is set in constructor & Add Row
            new EditShopForm_Object(inputArea, rowData.get(0), rowData.get(1), rowData.get(2), rowData.get(3), rowData.get(4));
        }
    }

    //EDITING NOW
    public String[] get_ShopForm_UpdateString(String ingredientIDInDB) // Not an override method
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
                insertStatement = "INSERT INTO ingredientInShops (IngredientID, Product_Name, Volume_Per_Unit, Cost_Per_Unit, StoreID) VALUES";

        String[] updates = new String[shopFormObjects.size()];

        //##############################################################
        // HELLO DELETE
        //##############################################################
        System.out.printf("\n\nget_ShopForm_UpdateString() List Size: %s \nList items:", shopFormObjects.size());
        for (AddShopForm_Object i : shopFormObjects)
        {
            System.out.printf("\n%s", i.getProductName_TxtField().getText().trim());
        }
        //##############################################################

        int pos = -1; // Due to pos+=1, pos will be greater than list size
        Iterator<AddShopForm_Object> it = shopFormObjects.iterator();
        boolean hasDataChanged = false;
        while (it.hasNext())
        {
            // Assigning current shopForm object in list to variable
            AddShopForm_Object shopForm_Object = it.next();

            // Get PDID of ShopForm Object
            Integer PDID = shopForm_Object.getPDID();

            if (PDID!=null) // Shop Object is in DB
            {
                // create dummy value for error below
                EditShopForm_Object editShopForm_Object = new EditShopForm_Object();

                // Convert to EditShopForm_Object
                if (shopForm_Object instanceof EditShopForm_Object)
                {
                    editShopForm_Object = (EditShopForm_Object) shopForm_Object;
                }

                // Check if the data changed
                if (!(editShopForm_Object.hasDataChanged()))
                {
                    continue;
                }
                hasDataChanged = true;

                //Update String
                updates[pos += 1] = String.format("""
                                UPDATE ingredientInShops
                                SET  Product_Name = '%s', Volume_Per_Unit = %s, Cost_Per_Unit = %s, StoreID = (SELECT StoreID FROM stores WHERE Store_Name = '%s')
                                WHERE PDID = %s;""",
                        editShopForm_Object.getProductName_TxtField().getText(),
                        editShopForm_Object.getProductQuantityPerPack_TxtField().getText(),
                        editShopForm_Object.getProductPrice_TxtField().getText(),
                        editShopForm_Object.getShops_JComboBox().getSelectedItem().toString(),
                        PDID);
            }
            else
            {
                System.out.println("\n\nget_ShopForm_UpdateString() here here");
                insertValues += String.format("\n(%s, '%s', %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s')),",
                        ingredientIDInDB,
                        shopForm_Object.getProductName_TxtField().getText(),
                        shopForm_Object.getProductQuantityPerPack_TxtField().getText(),
                        shopForm_Object.getProductPrice_TxtField().getText(),
                        shopForm_Object.getShops_JComboBox().getSelectedItem().toString());
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

    public class EditShopForm_Object extends AddShopForm_Object
    {
       String shopName_OG, productName_OG, productPrice_OG, quantityPerPack_OG;

        //#######################################
        // Main  Consturctor
        //#######################################
        EditShopForm_Object(Container parentContainer, String PDID, String shopName, String productName, String productPrice, String quantityPerPack)
        {
            // Setting Variables
            super(parentContainer);
            this.shopName_OG = shopName;
            this.productName_OG = productName;
            this.productPrice_OG = productPrice;
            this.quantityPerPack_OG = quantityPerPack;

            // Set GUI Objects
            setPDID(Integer.parseInt(PDID));   //Set PDID

            getShops_JComboBox().setSelectedItem(shopName); // Set ShopName

            getProductName_TxtField().setText(productName); // Set Product Name

            getProductPrice_TxtField().setText(productPrice); // Set Product Price

            getProductQuantityPerPack_TxtField().setText(quantityPerPack); // Set Volume Info
        }

        EditShopForm_Object()
        {

        }

        private boolean hasDataChanged()
        {
            boolean hasDataChanged = false;

            String shopName_current = getShops_JComboBox().getSelectedItem().toString().trim();
            String productName_current = getProductName_TxtField().getText().trim();
            String productPrice_current = getProductPrice_TxtField().getText().trim();
            String quantityPerPack_current = getProductQuantityPerPack_TxtField().getText().trim();

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

        //#######################################
        // Delete Row
        //#######################################
        @Override
        protected void deleteRowAction()
        {
            //################################################
            // Confirm if the user wants to delete the shop
            //################################################
            String
                    chosenShop = String.valueOf(getShops_JComboBox().getSelectedItem()),
                    productName = String.valueOf(getProductName_TxtField().getText());

            if (!(areYouSure(String.format("you want to permanently delete the product: \"%s\" from \"%s\" as a product for this ingredient.", productName, chosenShop))))
            {
                return;
            }

            //################################################
            // Get Ingredient PDID
            //################################################
            Integer PDID = getPDID();

            //################################################
            // If Object In DB Remove From DB
            //################################################
            if (PDID!=null)
            {
                //################################################
                // Get Ingredient ID
                //################################################
                String selectedIngredientID = edit_IngredientsForm.getSelectedIngredientID();

                if (selectedIngredientID==null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, String.format("Unable to get selected ingredients information to delete product:\n\"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
                    return;
                }

                //###################################################
                // Delete Supplier From ingredients_in_meal (PDID)
                //###################################################
                String updateQuery = String.format("""
                        UPDATE ingredients_in_sections_of_meal
                        SET  PDID = NULL
                        WHERE PDID = %s; """, PDID);

                System.out.printf("\n\n%s", updateQuery);

                //###################################################
                // Delete Supplier From ingredientInShops
                //###################################################
                String updateQuery2 = String.format("""
                        DELETE FROM ingredientInShops
                        WHERE PDID = %s; """, PDID);

                System.out.printf("\n\n%s", updateQuery2);

                //###################################################
                // Update
                //###################################################
                if (!(db.uploadData_Batch_Altogether(new String[]{updateQuery, updateQuery2})))
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, String.format("Unable to remove product: \"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
                    return;
                }
            }

            //################################################
            // Remove Row Object
            //################################################
            shopFormObjects.remove(this);
            removeFromParentContainer();

            //################################################
            // Remove Row Object
            //################################################
            JOptionPane.showMessageDialog(mealPlanScreen, String.format("Successfully, removed product: \"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
        }
    }
}

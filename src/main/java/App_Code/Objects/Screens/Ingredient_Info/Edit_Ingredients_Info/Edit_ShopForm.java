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

            System.out.printf("\n\nloadShopFormData() \n%s", rowData);

            // PDID is set in consturctor & Add Row
            EditAddShopForm_Object editShopForm_object = new EditAddShopForm_Object(inputArea, Integer.parseInt(rowData.get(0)));

            // Set ShopName
            editShopForm_object.getShops_JComboBox().setSelectedItem(rowData.get(1));

            // Set Product Name
            editShopForm_object.getProductName_TxtField().setText(rowData.get(2));

            // Set Cost Info
            editShopForm_object.getProductPrice_TxtField().setText(rowData.get(3));// HELLO IDK WHAT I DID HERE  in REFACTORING

            // Set Volume Info
            editShopForm_object.getQuantityPerPack_TxtField().setText(rowData.get(4));// HELLO IDK WHAT I DID HERE  in REFACTORING
        }
    }

    @Override
    protected void extraClearShopsForm()
    {
        shopsFormDBData.clear();
    }

    //EDITING NOW
    public String[] get_ShopForm_UpdateString(String ingredientIDInDB) // Not an override method
    {
        //#############################################################
        // Checks if there is anything to update before, updating
        //############################################################
        if (shopFormObjects.size() == 0)
        {
            return null;
        }

        ///###################################################################
        // Creating Insert Supplier Statement
        //####################################################################

        String
                insertValues = "",
                insertStatement = "INSERT INTO ingredientInShops (IngredientID, Product_Name, Volume_Per_Unit, Cost_Per_Unit, StoreID) VALUES";

        Iterator<AddShopForm_Object> it = shopFormObjects.iterator();

        String[] updates = new String[shopFormObjects.size()];

        System.out.printf("\n\nget_ShopForm_UpdateString() here here %s", shopFormObjects.size());

        int pos = 0;

        while(it.hasNext())
        {
            // Assigning current shopForm object in list to variable
            AddShopForm_Object shopForm_object = it.next();

            // Get PDID of ShopForm Object
            Integer PDID = shopForm_object.getPDID();

            // Shop Object is in DB
            if(PDID != null)
            {
                //Update String
                updates[pos] = String.format("""
                                UPDATE ingredientInShops
                                SET  Product_Name = '%s', Volume_Per_Unit = %s, Cost_Per_Unit = %s, StoreID = (SELECT StoreID FROM stores WHERE Store_Name = '%s')
                                WHERE PDID = %s;""",
                        shopForm_object.getProductName_TxtField().getText(),
                        shopForm_object.getQuantityPerPack_TxtField().getText(),
                        shopForm_object.getProductPrice_TxtField().getText(),
                        shopForm_object.getShops_JComboBox().getSelectedItem().toString(),
                        PDID);
            }
            else
            {
                System.out.println("\n\nget_ShopForm_UpdateString() here here");
                insertValues += String.format("\n(%s, '%s', %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s')),",
                        ingredientIDInDB,
                        shopForm_object.getProductName_TxtField().getText(),
                        shopForm_object.getQuantityPerPack_TxtField().getText(),
                        shopForm_object.getProductPrice_TxtField().getText(),
                        shopForm_object.getShops_JComboBox().getSelectedItem().toString());
            }

            //#############################
            //
            //#############################
            pos++;
        }

        //############################################################
        // Insert Statement
        //############################################################
        if(!(insertValues.equals("")))
        {
            insertValues = insertValues.substring(0, insertValues.length() - 1) + ";;";

            insertStatement += insertValues;
            updates[pos+=1] = (insertStatement);
        }

        System.out.println("\n\nget_ShopForm_UpdateString()");
        for(String i: updates)
        {
            System.out.printf("\n%s", i);
        }

        //############################################################
        // Return values
        //############################################################
        return updates;
    }

    public class EditAddShopForm_Object extends AddShopForm_Object
    {
        //#######################################
        // Main  Consturctor
        //#######################################
        EditAddShopForm_Object(Container parentContainer, Integer PDID)
        {
            super(parentContainer);
            setPDID(PDID);
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

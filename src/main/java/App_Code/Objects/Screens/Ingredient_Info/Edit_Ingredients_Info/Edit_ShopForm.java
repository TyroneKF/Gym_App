package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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

        if (reply == JOptionPane.NO_OPTION || reply == JOptionPane.CLOSED_OPTION)
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

        if (temp_ShopsFormDBData == null)
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

            System.out.printf("\n\nloadShopFormData() \n%s",rowData);

            // PDID is set in consturctor & Add Row
            EditAddShopForm_Object editShopForm_object = edit_AddShopForm_Object(Integer.parseInt(rowData.get(0)));
            shopFormObjects.add(editShopForm_object); // store in ingredients screen object memory

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

    public ArrayList<ArrayList<String>> getShopsFormDBData()
    {
        return shopsFormDBData;
    }

    //EDITING NOW
    public String[] get_ShopForm_UpdateString(String ingredientIDInDB) // Not an override method
    {/*
        //#############################################################
        // Checks if there is anything to update before, updating
        //############################################################
        if (addShopFormObjects.size() == 0)
        {
            return null;
        }

        //################################################################
        // Adding Shops To Category's To Either Insert Or Update into DB
        //################################################################
        ArrayList<AddShopForm_Object> suppliersInDBList = new ArrayList<>();
        ArrayList<AddShopForm_Object> suppliersNeedToBeAddedToDBList = new ArrayList<>();

        for (AddShopForm_Object shopForm_object : addShopFormObjects)
        {
            //  EditAddShopForm_Object obj = (EditAddShopForm_Object) shopForm_object;
            AddShopForm_Object obj = shopForm_object;

            if (obj.getPDID() != null)
            {
                suppliersInDBList.add(obj);
            }
            else
            {
                suppliersNeedToBeAddedToDBList.add(obj);
            }
        }

        //###############################
        // Creating Update List
        //###############################
        int suppliersInDBSize = suppliersInDBList.size();
        int suppliersNotInDBSize = suppliersNeedToBeAddedToDBList.size();

        String[] updates = new String[suppliersInDBSize + suppliersNotInDBSize];

        int listPos = 0;

        ///###################################################################
        // Creating Insert Supplier Statement
        //####################################################################

        if (suppliersInDBSize > 0)
        {
            // Creating String Of Add Values
            for (AddShopForm_Object supplierInDB : suppliersInDBList)
            {
                int objectID = supplierInDB.getObjectID();
                Integer PDID = supplierInDB.getPDID();

                //Update String
                String updateString = String.format("""
                                UPDATE ingredientInShops
                                SET Volume_Per_Unit = %s, Cost_Per_Unit = %s, StoreID = (SELECT StoreID FROM stores WHERE Store_Name = '%s')
                                WHERE PDID = %s;""",
                        supplierInDB.getQuantityPerPack_TxtField().getText(),
                        supplierInDB.getProductPrice_TxtField().getText(),
                        supplierInDB.getShops_JComboBox().getSelectedItem().toString(),
                        PDID);

                // Add to update List
                updates[listPos] = updateString;

                listPos++;
            }






        }

        //###################################################################
        // Creating Insert Supplier Statement
        //####################################################################

        String query4_UpdateString = "";

        if (suppliersNotInDBSize > 0)
        {
            // Variables
            String values = "";
            int pos = 0;

            // Insert String
            query4_UpdateString = "INSERT INTO ingredientInShops (IngredientID, Volume_Per_Unit, Cost_Per_Unit, StoreID) VALUES";

            // Creating String Of Add Values
            for (AddShopForm_Object supplierAddToDB : suppliersNeedToBeAddedToDBList)
            {
                int objectID = supplierAddToDB.getObjectID();

                values += String.format("\n(%s, %s, %s, (SELECT StoreID FROM stores WHERE Store_Name = '%s'))",
                        ingredientIDInDB, quantityPerPack.get(objectID).getText(),
                        prices.get(objectID).getText(), shopJComboBoxes.get(objectID).getSelectedItem().toString());

                if (pos == suppliersNotInDBSize - 1)
                {
                    values += ";";
                    continue;
                }

                values += ",";
                pos++;
            }

            // Adding Both The Query & Values Together
            query4_UpdateString += values;

            //########################
            // Adding Update String
            //########################
            updates[listPos] = query4_UpdateString;
        }

        //############################################################
        // Return values
        //############################################################
        return updates;*/

        return new String[1];
    }

    //EDITING NOW
    private EditAddShopForm_Object edit_AddShopForm_Object(Integer PDID) // Not an override method
    {
        EditAddShopForm_Object obj = new EditAddShopForm_Object(inputArea, PDID, true);
        addToContainer(inputArea, obj, 0, yPos+=1, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
        return obj;
    }

    public class EditAddShopForm_Object extends AddShopForm_Object
    {
        //#######################################
        // Main  Consturctor
        //#######################################
        EditAddShopForm_Object(Container parentContainer, Integer PDID, boolean addRow)
        {
            super(parentContainer, addRow);
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
            if (PDID != null)
            {
                //################################################
                // Get Ingredient ID
                //################################################
                String selectedIngredientID = edit_IngredientsForm.getSelectedIngredientID();

                if (selectedIngredientID == null)
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

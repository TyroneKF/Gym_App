package App_Code.Objects.Screens.Ingredient_Info.Edit_Ingredients_Info;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Edit_ShopForm extends Add_ShopForm
{
    private Edit_IngredientsForm ingredientsForm;

    public Edit_ShopForm(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen, Edit_IngredientsScreen edit_ingredients, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, edit_ingredients, btnText, btnWidth, btnHeight);
        this.ingredientsForm = edit_ingredients.getIngredientsForm();
    }

    public void updateShopFormWithInfoFromDB()
    {
        //###########################
        //
        //###########################
        String selectedIngredientID = ingredientsForm.getSelectedIngredientID();

        //###########################
        // Get New Ingredient Shop Info
        //###########################
        ArrayList<ArrayList<String>> ingredientShops_R = db.getMultiColumnQuery(String.format("""                    
                SELECT i.PDID, s.Store_Name, i.Cost_Per_Unit, i.Volume_Per_Unit
                FROM  ingredientInShops i
                INNER JOIN
                (
                  SELECT StoreID, Store_Name FROM stores
                ) s
                ON s.StoreID = i.StoreID
                AND  i.IngredientID = %s ;""", selectedIngredientID));

        if (ingredientShops_R == null)
        {
            JOptionPane.showMessageDialog(mealPlanScreen, "Unable to grab selected ingredient shop info! \nMaybe there isn't any suppliers created for this Ingredient!");
            return;
        }

        //###########################
        // Clear Supplier Info
        //###########################
        clearShopForm();

        //###########################
        //Add Rows for shops onto form
        //###########################

        for (int i = 0; i < ingredientShops_R.size(); i++)
        {
            ArrayList<String> rowData = ingredientShops_R.get(i);

            // Set PDID & Add Row
            Add_ShopForm.AddShopForm_Object row = addShopForm_object(Integer.parseInt(rowData.get(0)));
            addShopFormObjects.add(row);

            // Set ShopName
            row.getShops_JComboBox().setSelectedItem(rowData.get(1));// HELLO IDK WHAT I DID HERE in REFACTORING

            // Set Cost Info
            row.getIngredientPrice_TxtField().setText(rowData.get(2));// HELLO IDK WHAT I DID HERE  in REFACTORING

            // Set Volume Info
            row.getQuantityPerPack_TxtField().setText(rowData.get(3));// HELLO IDK WHAT I DID HERE  in REFACTORING
        }
    }

    //EDITING NOW
    public String[] get_ShopForm_UpdateString(String ingredientIDIn) // Not an override method
    {
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
                        supplierInDB.getIngredientPrice_TxtField().getText(),
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
                        ingredientIDIn, quantityPerPack.get(objectID).getText(),
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
        return updates;
    }

    //EDITING NOW
    public AddShopForm_Object addShopForm_object(Integer PDID) // Not an override method
    {
        EditAddShopForm_Object obj = new EditAddShopForm_Object(inputArea, PDID, true);
        addToContainer(inputArea, obj, 0, objectID, 1, 1, 0.25, 0.25, "horizontal", 0, 0);
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
        // Inherited Constructor
        //#######################################
        EditAddShopForm_Object(Container parentContainer, boolean addRow) //HELLO DO not remove, it may appear as unused but, its definitely needed
        {
            super(parentContainer, addRow);
            System.out.printf("\n\nID: %s");
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
            String chosenShop = String.valueOf(shops_JComboBox.getSelectedItem());
            if (!(areYouSure(String.format("you want to permanently delete %s as a Supplier for this ingredient", chosenShop))))
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
                String selectedIngredientID = ingredientsForm.getSelectedIngredientID();

                if (selectedIngredientID == null)
                {
                    JOptionPane.showMessageDialog(mealPlanScreen, String.format("Unable to get selected ingredients information to delete the Supplier ' %s ' !", chosenShop));
                    return;
                }

                //###################################################
                // Delete Supplier From ingredients_in_meal (PDID)
                //###################################################
                String updateQuery = String.format("""
                            UPDATE ingredients_in_meal
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
                    JOptionPane.showMessageDialog(mealPlanScreen, String.format("Unable to remove the supplier ' %s ' from this ingredient!", chosenShop));
                    return;
                }
            }

            //################################################
            // Remove Row Object
            //################################################
            removeFromParentContainer();
            addShopFormObjects.remove(this);

            //################################################
            // Remove Row Object
            //################################################
            JOptionPane.showMessageDialog(mealPlanScreen, String.format("Successfully, remove the  supplier ' %s ' from this ingredient!", chosenShop));
        }
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
}

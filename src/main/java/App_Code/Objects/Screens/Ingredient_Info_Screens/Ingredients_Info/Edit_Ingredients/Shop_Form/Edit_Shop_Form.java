package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Add_ShopForm_Object;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Add_Shop_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Ingredients_Form.Edit_Ingredients_Form;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Edit_Ingredients_Screen;
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
    public Edit_Shop_Form(Container parentContainer, Ingredients_Info_Screen ingredients_info_screen,
                          Edit_Ingredients_Screen edit_ingredients, String btnText, int btnWidth, int btnHeight)
    {
        super(parentContainer, ingredients_info_screen, edit_ingredients, btnText, btnWidth, btnHeight);
        this.edit_IngredientsForm = edit_ingredients.get_Ingredients_Form();
        
        collapsibleJPanel.expand_JPanel();
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
        String shopInfoQuery = String.format("""
                SELECT i.pdid, s.store_name, i.product_name, i.cost_per_unit, i.volume_per_unit
                FROM  ingredient_in_shops i
                INNER JOIN
                (
                  SELECT store_id, store_name FROM stores
                ) s
                ON s.store_id = i.store_id
                AND  i.ingredient_id = %s ;""", selectedIngredientID);
        
        String errorMSG = "Error, Unable to get ShopForm Info For Selected Ingredient!";
        
        ArrayList<ArrayList<String>> temp_ShopsFormDBData = db.get_Multi_Column_Query(shopInfoQuery, errorMSG);
        
        if (temp_ShopsFormDBData == null)
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
            Edit_ShopForm_Object edit_shopForm_object = new Edit_ShopForm_Object(inputArea, this, rowData.get(0),
                    rowData.get(1), rowData.get(2), rowData.get(3), rowData.get(4));
            
            shopFormObjects.add(edit_shopForm_object);
            
            // Adding Object To GUI
            add_To_Container(inputArea, edit_shopForm_object, 0, get_And_Increase_YPos(), 1, 1, 0.25, 0.25, "both", 0, 0, null);
            
            // Resize GUI
            resize_GUI();
        }
    }
    
    //##################################################################################################################
    // Methods
    //##################################################################################################################
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
        if (shopFormObjects.size() == 0)
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
        
        int pos = - 1; // Due to pos+=1, pos will be greater than list size
        Iterator<Add_ShopForm_Object> it = shopFormObjects.iterator();
        boolean hasDataChanged = false;
        while (it.hasNext())
        {
            // Assigning current shopForm object in list to variable
            Add_ShopForm_Object shopForm_Object = it.next();
            
            // Get PDID of ShopForm Object
            Integer PDID = shopForm_Object.get_PDID();
            
            if (PDID != null) // Shop Object is in DB
            {
                // create dummy value for error below
                Edit_ShopForm_Object editShopForm_Object = new Edit_ShopForm_Object(parentContainer, this);
                
                // Convert to Edit_ShopForm_Object
                if (shopForm_Object instanceof Edit_ShopForm_Object)
                {
                    editShopForm_Object = (Edit_ShopForm_Object) shopForm_Object;
                }
                
                // Check if the data changed
                if (! (editShopForm_Object.has_Data_Changed()))
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
        if (! (insertValues.equals("")))
        {
            insertValues = insertValues.substring(0, insertValues.length() - 1) + ";;";
            
            insertStatement += insertValues;
            updates[pos += 1] = (insertStatement);
        }
        else if (! hasDataChanged)
        {
            return null;
        }
        
        //############################################################
        // Return values
        //############################################################
        return updates;
    }
    
    public String get_Selected_IngredientID()
    {
        return edit_IngredientsForm.get_Selected_IngredientID();
    }
    
    public MyJDBC get_DB()
    {
        return db;
    }
    
    @Override
    protected Boolean are_You_Sure(String title, String msg)
    {
        return super.are_You_Sure(title, msg);
    }
}

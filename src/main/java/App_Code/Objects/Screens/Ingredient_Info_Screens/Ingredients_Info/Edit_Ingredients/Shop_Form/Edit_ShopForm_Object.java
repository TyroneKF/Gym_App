package App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Edit_Ingredients.Shop_Form;

import App_Code.Objects.Database_Objects.JDBC.MyJDBC;
import App_Code.Objects.Database_Objects.JDBC.Null_MYSQL_Field;
import App_Code.Objects.Screens.Ingredient_Info_Screens.Ingredients_Info.Add_Ingredients.Shop_Form.Add_ShopForm_Object;
import org.javatuples.Pair;
import javax.swing.*;
import java.awt.*;
import java.sql.Types;
import java.util.LinkedHashSet;



public class Edit_ShopForm_Object extends Add_ShopForm_Object
{
    //##################################################################################################################
    // Variables
    //##################################################################################################################
    private String shopName_OG, productName_OG, productPrice_OG, quantityPerPack_OG;
    private Edit_Shop_Form edit_shop_form;
    private MyJDBC db;
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public Edit_ShopForm_Object(Container parentContainer, Edit_Shop_Form edit_shop_form, String PDID, String shopName,
                                String productName, String productPrice, String quantityPerPack)
    {
        //#######################################################
        // Setting Variables
        //#######################################################
        super(parentContainer, edit_shop_form);
        this.edit_shop_form = edit_shop_form;
        this.db = edit_shop_form.get_DB();
        
        this.shopName_OG = shopName;
        this.productName_OG = productName;
        this.productPrice_OG = productPrice;
        this.quantityPerPack_OG = quantityPerPack;
        
        //#######################################################
        // Set GUI Objects
        //#######################################################
        set_PDID(Integer.parseInt(PDID));   //Set PDID
        
        get_ProductShop_JComboBox().setSelectedItem(shopName); // Set ShopName
        
        get_ProductName_TxtField().setText(productName); // Set Product Name
        
        get_Product_Price_TxtField().setText(productPrice); // Set Product Price
        
        get_Product_Quantity_Per_Pack_TxtField().setText(quantityPerPack); // Set Volume Info
    }
    
    public Edit_ShopForm_Object(Container parentContainer, Edit_Shop_Form edit_shop_form)
    {
        super(parentContainer, edit_shop_form);
    }
    
    //##################################################################################################################
    // Constructor
    //##################################################################################################################
    public boolean has_Data_Changed()
    {
        boolean hasDataChanged = false;
        
        String shopName_current = get_ProductShops_TXT();
        String productName_current = get_ProductName_Txt();
        String productPrice_current = get_ProductPrice_Txt();
        String quantityPerPack_current = get_Product_Quantity_Per_Pack_Txt();
        
        if (! (shopName_current.equals(shopName_OG)))
        {
            hasDataChanged = true;
        }
        else if (! (productName_current.equals(productName_OG)))
        {
            hasDataChanged = true;
        }
        else if (! (productPrice_current.equals(productPrice_OG)))
        {
            hasDataChanged = true;
        }
        else if (! (quantityPerPack_current.equals(quantityPerPack_OG)))
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
        
        String msg = String.format("""
                        Are you sure you want to permanently delete:
                        the product: '%s' from '%s' as a product for this ingredient.""",
                productName, chosenShop);
        
        if (! (are_You_Sure("Delete Product", msg))) { return; }
        
        //################################################
        // Get Ingredient PDID
        //################################################
        Integer PDID = get_PDID();
        
        //################################################
        // If Object In DB Remove From DB
        //################################################
        if (PDID != null)
        {
            //################################################
            // Get Ingredient ID
            //################################################
            String selectedIngredientID = edit_shop_form.get_Selected_IngredientID();
            
            if (selectedIngredientID == null)
            {
                JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Unable to get selected ingredients information to delete product:\n\"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
                return;
            }
            
            //###################################################
            // Delete Supplier From ingredients_in_meal (PDID)
            //###################################################
            String updateQuery1 = """
                    UPDATE ingredients_in_sections_of_meal
                    SET  pdid = ?
                    WHERE pdid = ?;""";
                    
            //###################################################
            // Delete Supplier From ingredientInShops
            //###################################################
            String updateQuery2 = "DELETE FROM ingredient_in_shops WHERE pdid = ?;";
            
            //###################################################
            // Update
            //###################################################
            String errorMSG = String.format("Unable to remove product: \"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop);
            
            LinkedHashSet<Pair<String, Object[]>> queries_And_Params = new LinkedHashSet<>()
            {{
                
                add(new Pair<>(updateQuery1, new Object[]{ new Null_MYSQL_Field(Types.INTEGER), PDID }));
                add(new Pair<>(updateQuery2, new Object[]{PDID}));
            }};
            
            if (! (db.upload_Data_Batch2(queries_And_Params, errorMSG))) { return; }
        }
        
        //################################################
        // Remove Row Object
        //################################################
        shop_Form_Objects.remove(this);
        remove_From_Parent_Container();
        
        //################################################
        // Remove Row Object
        //################################################
        JOptionPane.showMessageDialog(mealPlanScreen.getFrame(), String.format("Successfully, removed product: \"%s\" from \"%s\" as a Supplier for this ingredient.", productName, chosenShop));
    }
    
    private boolean are_You_Sure(String title, String msg)
    {
        return edit_shop_form.are_You_Sure(title, msg);
    }
}


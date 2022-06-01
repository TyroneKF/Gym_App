package App_Code.Objects.API.Nutritionix;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;


public class NutritionIx_API
{
    private String pathToNutrientsCSV = "src/App_Code/Objects/API/Nutritionix/Resources/Nutritionix API v2 - Full Nutrient USDA .csv";
    private int attr_ID_Col = 1, attr_Name_Col = 4;
    private BufferedReader csvReader = null;

    //######################################################

    // full_nutrients
    private String secondArrayName = "full_nutrients";
    private String secondArrayKeyName = "attr_id";
    private String secondArrayValueKeyName = "value";

    // photo
    private String thirdArrayName = "photo";
    private String appID = "22210106", appKey = "7e4f361cf3d659726c2e1ead771ec52e";


    private ArrayList<String> natural_Nutrients_API_DesiredFields = new ArrayList<>(Arrays.asList(
            "food_name",
            "brand_name",
            "serving_qty",
            "serving_unit",
            "serving_weight_grams",
            "nf_calories",
            "nf_total_fat",
            "nf_saturated_fat",
            "nf_cholesterol",
            "nf_sodium",
            "nf_total_carbohydrate",
            "nf_dietary_fiber",
            "nf_sugars",
            "nf_protein",
            "nf_potassium",
            "nf_p",

            "full_nutrients",

            "nix_brand_name",
            "nix_brand_id",
            "nix_item_name",
            "nix_item_id",

            "photo",
            "thumb",
            "highres"
    ));


    //######################################################

    private ArrayList<String> search_Instant_API_DesiredFields = new ArrayList<>(Arrays.asList(
            "branded",
            "food_name",
            "brand_name",

            "photo",
            "thumb",
            "highres",

            "nix_item_id"
    ));

    //#################################################################################################################
    public static void main(String[] args)
    {
        NutritionIx_API api = new NutritionIx_API();
//        api.get_POST_V2NaturalNutrients("100g of chicken");
//        api.get_GET_V2SearchInstant("Ben & Jerry's");
        api.get_GET_V2SearchItem("5f637ca24b187f7f76a08a0e");
    }
    //#################################################################################################################
    public NutritionIx_API()
    {

    }
    //#################################################################################################################
    public LinkedHashMap<String, Object> get_POST_V2NaturalNutrients(String food)
    {
       return parseFurtherNutritionalInfo(get_POST_V2NaturalNutrientsAction(food));
    }

    private LinkedHashMap<String, Object> get_POST_V2NaturalNutrientsAction(String food)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################
            // API END Point Link
            URL url = new URL("https://trackapi.nutritionix.com/v2/natural/nutrients");

            // Create Connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the Request Method
            con.setRequestMethod("POST");

            // Set the Request Content-Type Header Parameter
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            // Headers JSon
            con.setRequestProperty("Content-Type", "application/json");   // Set Response Format Type
            con.setRequestProperty("x-app-id", appID);
            con.setRequestProperty("x-app-key", appKey);
            con.setRequestProperty("x-remote-user-id", "0");

            // Ensure the Connection Will Be Used to Send Content
            con.setDoOutput(true);

            // Create Request Body Json (Custom JSON String)
            String jsonInputString = String.format("{\n  \"query\":\"%s\",\n  \"timezone\": \"UK\"\n}", food);

            return parseJsonResponse("POST", con, jsonInputString, "foods", natural_Nutrients_API_DesiredFields);
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError getNutritionalInfo() \n'' %s ''", e);
            return null;
        }
    }

    //#####################################################
    private LinkedHashMap<String, Object> parseFurtherNutritionalInfo(LinkedHashMap<String, Object> foodNutritionalInfo)
    {
        if (foodNutritionalInfo == null)
        {
            return null;
        }

        try
        {
            //##########################################################################################
            // Parsing Full Nutrients
            //##########################################################################################

            JSONArray ar2 = (JSONArray) foodNutritionalInfo.get(secondArrayName);

            //#############################################
            // Getting Info For attr_id in full_nutrients
            //#############################################
            Iterator<Object> iterator2 = ar2.iterator();
            while (iterator2.hasNext())
            {
                // Info From JSON Object
                JSONObject jsonObject = (JSONObject) iterator2.next();
                String attr_id = jsonObject.get(secondArrayKeyName).toString();
                Object attr_Value = jsonObject.get(secondArrayValueKeyName);

                // Extract Name From CSV File by Attr_ID
                String attr_Name = getAttr_Name_By_AttrID(String.format("%s", attr_id));

                if (attr_Name != null)
                {
                    foodNutritionalInfo.put(attr_Name, attr_Value);
                }
                else
                {
                    System.out.printf("\n\nError  parseFurtherNutritionalInfo() \nIssues Extracting Attribute Name for attr_id = %s", attr_id);
                    return null;
                }
            }

            //#######################################################################
            // Removing Old Data
            //#######################################################################
            foodNutritionalInfo.remove(secondArrayName);
            foodNutritionalInfo.remove(thirdArrayName);

            //#######################################################################
            // Print
            //#######################################################################
            System.out.printf("\n\n########################''");
            foodNutritionalInfo.entrySet().forEach(entry -> {
                System.out.printf("\n%s : %s", entry.getKey(), entry.getValue());
            });

            return foodNutritionalInfo;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError  parseFurtherNutritionalInfo() \n'' %s ''", e);
            return null;
        }
    }

    private String getAttr_Name_By_AttrID(String attr_id)
    {
        //#############################################
        // Checking If  Path To CSV Exists
        //#############################################
        try
        {
            csvReader = new BufferedReader(new FileReader(pathToNutrientsCSV));
        }
        catch (FileNotFoundException e)
        {
            System.out.printf("\n\nparseFurtherNutritionalInfo() couldn't Read CSV file from Path:\n%s \n\nError: \ne", pathToNutrientsCSV, e);
            return null;
        }

        //########################################
        // Reading Lines From File From CSV
        //#######################################
        try
        {
            // skip the first line (column names)
            csvReader.readLine();

            // Reading through the rest of the file
            String row;

            while ((row = csvReader.readLine()) != null)
            {
                String[] csvRowData = row.split(","); // the whole row data col1,col2,col3.....

                if (attr_id.equals(csvRowData[attr_ID_Col - 1]))
                {
                    csvReader.close();
                    return csvRowData[attr_Name_Col - 1];
                }
            }

            System.out.printf("\n\ngetAttr_Info_By_AttrID() Error \nCouldn't get Nutritional Name For %s", attr_id);
            csvReader.close();
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                csvReader.close();
            }
            catch (IOException ex)
            {
                System.out.printf("\n\ngetAttr_Info_By_AttrID() Error \nError Closing Csv.Reader \nError:\n", ex);
            }
            return null;
        }
        //###############################
    }

    //#################################################################################################################
    public ArrayList<LinkedHashMap<String, Object>> get_GET_V2SearchInstant(String product)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################
            // API END Point Link
            URL url = new URL("https://trackapi.nutritionix.com/v2/search/instant");

            // Create Connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the Request Method
            con.setRequestMethod("POST");

            con.setDoOutput(true);

            // Headers JSon
            con.setRequestProperty("x-app-id", appID);
            con.setRequestProperty("x-app-key", appKey);

            // Set the Request Content-Type Header Parameter
            con.setRequestProperty("Content-Type", "application/json");

            // Create Request Body Json (Custom JSON String)
            String jsonInputString = String.format("{\n  \"query\":\"%s\",\n  \"common\":\"false\",\n  \"branded_region\": \"2\"\n}", product);


//           / return parseJsonResponse("POST", con, jsonInputString,"branded" , search_Instant_API_DesiredFields);

            try (OutputStream os = con.getOutputStream())
            {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the Response From Input Stream
            StringBuilder response;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8")))
            {
                response = new StringBuilder();
                String responseLine = null;

                while ((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
            }

            //#####################################################################
            // Parsing Response & Storing Data
            //#####################################################################
            System.out.printf("\n\n########################''");

            ArrayList<LinkedHashMap<String, Object>> productResults = new ArrayList();


            String jsonString = response.toString(); // convert stringBuilder object to string from  process above
            JSONObject jsonObjectFromString = new JSONObject(jsonString); // convert string to JSON Object

            JSONArray jsonArray = jsonObjectFromString.getJSONArray("branded"); // getting main json array

            // Looping through json Array and storing data
            Iterator<Object> iterator = jsonArray.iterator();
            while (iterator.hasNext())
            {
                JSONObject jsonObject = (JSONObject) iterator.next();

                LinkedHashMap<String, Object> foodNutritionalInfo = new LinkedHashMap<>();

                System.out.printf("\n\n########################''");

                for (String key : jsonObject.keySet())
                {
                    Object keyData = jsonObject.get(key);
                    if (search_Instant_API_DesiredFields.contains(key))
                    {
                        if (key.equals("photo"))
                        {
                            JSONObject picJSOnObj = (JSONObject) keyData;

                            for (String picKey : picJSOnObj.keySet())
                            {
                                Object picKeyData = picJSOnObj.get(picKey);
                                System.out.printf("\n%s : %s", picKey, picKeyData);

                                foodNutritionalInfo.put(picKey, picKeyData);
                            }
                            continue;
                        }

                        System.out.printf("\n%s : %s", key, keyData);

                        foodNutritionalInfo.put(key, keyData);
                    }
                }
                productResults.add(foodNutritionalInfo);
            }

            return productResults;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError getNutritionalInfo() \n'' %s ''", e);
            return null;
        }
    }

    //#################################################################################################################
    public LinkedHashMap<String, Object> get_GET_V2SearchItem(String nix_item_id)
    {
        return parseFurtherNutritionalInfo(get_GET_V2SearchItemAction(nix_item_id));
    }

    private LinkedHashMap<String, Object> get_GET_V2SearchItemAction(String nix_item_id)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################
            // API END Point Link
            URL url = new URL(String.format("https://trackapi.nutritionix.com/v2/search/item?nix_item_id=%s", nix_item_id));

            // Create Connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the Request Method
            con.setRequestMethod("GET");

            // Set the Request Content-Type Header Parameter
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            // Headers JSon
//            con.setRequestProperty("Content-Type", "application/json");   // Set Response Format Type
            con.setRequestProperty("x-app-id", appID);
            con.setRequestProperty("x-app-key", appKey);

            // Ensure the Connection Will Be Used to Send Content
            con.setDoOutput(true);

            return parseJsonResponse("GET", con,null,"foods", natural_Nutrients_API_DesiredFields );
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError getNutritionalInfo() \n'' %s ''", e);
            return null;
        }
    }
    //#################################################################################################################

    private LinkedHashMap<String, Object> parseJsonResponse (String process, HttpURLConnection con, String jsonInputString, String mainArrayName, ArrayList<String> desiredFields)
    {
        try
        {
            if(process.equalsIgnoreCase("post"))
            {
                try (OutputStream os = con.getOutputStream())
                {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            // Read the Response From Input Stream
            StringBuilder response;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8")))
            {
                response = new StringBuilder();
                String responseLine = null;

                while ((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
            }

            //#####################################################################
            // Parsing Response & Storing Data
            //#####################################################################
//            System.out.printf("\n\n########################''");
            LinkedHashMap<String, Object> foodNutritionalInfo = new LinkedHashMap<>();

            String jsonString = response.toString(); // convert stringBuilder object to string from  process above
            JSONObject jsonObjectFromString = new JSONObject(jsonString); // convert string to JSON Object

            JSONArray foods = jsonObjectFromString.getJSONArray(mainArrayName); // getting main json array

            // Looping through json Array and storing data
            Iterator<Object> iterator = foods.iterator();
            while (iterator.hasNext())
            {
                JSONObject jsonObject = (JSONObject) iterator.next();
                for (String key : jsonObject.keySet())
                {
                    Object keyData = jsonObject.get(key);
//                    System.out.printf("\n%s : %s", key, keyData);
                    if (desiredFields.contains(key))
                    {
                        if (key.equals("photo"))
                        {
                            JSONObject picJSOnObj = (JSONObject) keyData;

                            for (String picKey : picJSOnObj.keySet())
                            {
                                if (desiredFields.contains(picKey))
                                {
                                    Object picKeyData = picJSOnObj.get(picKey);
                                    System.out.printf("\n%s : %s", picKey, picKeyData);

                                    foodNutritionalInfo.put(picKey, picKeyData);
                                }
                            }
                            continue;
                        }

                        System.out.printf("\n%s : %s", key, keyData);

                        foodNutritionalInfo.put(key, keyData);
                    }
                }
            }
            return foodNutritionalInfo;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError parseJsonResponse() \n'' %s ''", e);
            return null;
        }
    }
}

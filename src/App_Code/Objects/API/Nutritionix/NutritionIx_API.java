package App_Code.Objects.API.Nutritionix;


import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;


public class NutritionIx_API
{
    private final String pathToNutrientsCSV = "src/App_Code/Objects/API/Nutritionix/Resources/Nutritionix API v2 - Full Nutrient USDA .csv";
    private final int attr_ID_Col = 1, attr_Name_Col = 4;

    //######################################################

    private final String appID = "22210106", appKey = "7e4f361cf3d659726c2e1ead771ec52e";


    private final ArrayList<String> natural_Nutrients_API_DesiredFields = new ArrayList<>(Arrays.asList(
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

    private final ArrayList<String> search_Instant_API_DesiredFields = new ArrayList<>(Arrays.asList(
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
        api.get_POST_V2SearchInstant("Ben & Jerry's");
//        api.get_GET_V2SearchItem("5f637ca24b187f7f76a08a0e");

    }

    //#################################################################################################################
    public NutritionIx_API()
    {

    }

    //#################################################################################################################
    public LinkedHashMap<String, Object> get_POST_V2NaturalNutrients(String food)
    {
        LinkedHashMap<String, Object> foodInfo =  parseFurtherNutritionalInfo(get_POST_V2NaturalNutrientsAction(food));

        System.out.printf("\n\n########################''");
        foodInfo.entrySet().forEach(entry -> {
            System.out.printf("\n%s : %s", entry.getKey(), entry.getValue());
        });

        return foodInfo;
    }

    private LinkedHashMap<String, Object> get_POST_V2NaturalNutrientsAction(String food)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################

            String
                    urlLink = "https://trackapi.nutritionix.com/v2/natural/nutrients",
                    process = "POST",
                    mainArrayName = "foods"
            ;

            // Set the Request Content-Type Header Parameter
            ArrayList<Pair<String, String>> properties = new ArrayList<>(Arrays.asList(
                    new Pair<>("Content-Type", "application/json"),
                    new Pair<>("x-app-id", appID),
                    new Pair<>("x-app-key", appKey),
                    new Pair<>("x-remote-user-id", "0")
            ));

            // Create Request Body Json (Custom JSON String)
            String jsonInputString = String.format("{\n  \"query\":\"%s\",\n  \"timezone\": \"UK\"\n}", food);

            //#################################
            // Getting Parsed Json Results
            //#################################
            ArrayList<LinkedHashMap<String, Object>> results = parseJsonResponse2(process, urlLink,properties,jsonInputString,mainArrayName, natural_Nutrients_API_DesiredFields );

            if (results == null)
            {
                return null;
            }
            return results.get(0);
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

            // full_nutrients
            String secondArrayName = "full_nutrients";
            JSONArray ar2 = (JSONArray) foodNutritionalInfo.get(secondArrayName);

            //#############################################
            // Getting Info For attr_id in full_nutrients
            //#############################################
            Iterator<Object> iterator2 = ar2.iterator();
            while (iterator2.hasNext())
            {
                // Info From JSON Object
                JSONObject jsonObject = (JSONObject) iterator2.next();
                String secondArrayKeyName = "attr_id";
                String attr_id = jsonObject.get(secondArrayKeyName).toString();
                String secondArrayValueKeyName = "value";
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

            //#######################################################################
            // Print
            //#######################################################################

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
        BufferedReader csvReader = null;
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
    public ArrayList<LinkedHashMap<String, Object>> get_POST_V2SearchInstant(String product)
    {
        return get_POST_V2SearchInstantAction(product);
    }

    private ArrayList<LinkedHashMap<String, Object>> get_POST_V2SearchInstantAction(String product)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################

            String
                    urlLink = "https://trackapi.nutritionix.com/v2/search/instant",
                    process = "POST",
                    mainArrayName = "branded";

            // Set the Request Content-Type Header Parameter
            ArrayList<Pair<String, String>> properties = new ArrayList<>(Arrays.asList(
                    new Pair<>("Content-Type", "application/json"),
                    new Pair<>("x-app-id", appID),
                    new Pair<>("x-app-key", appKey)
            ));

            // Create Request Body Json (Custom JSON String)
            String jsonInputString = String.format("{\n  \"query\":\"%s\",\n  \"common\":\"false\",\n  \"branded_region\": \"2\"\n}", product);

            //#################################
            // Getting Parsed Json Results
            //#################################
            return parseJsonResponse2(process, urlLink,properties,jsonInputString,mainArrayName, search_Instant_API_DesiredFields);
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
        LinkedHashMap<String, Object> foodInfo =  parseFurtherNutritionalInfo(get_GET_V2SearchItemAction(nix_item_id));

        System.out.printf("\n\n########################''");
        foodInfo.entrySet().forEach(entry -> {
            System.out.printf("\n%s : %s", entry.getKey(), entry.getValue());
        });

        return foodInfo;
    }

    private LinkedHashMap<String, Object> get_GET_V2SearchItemAction(String nix_item_id)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################

            String
                    urlLink = String.format("https://trackapi.nutritionix.com/v2/search/item?nix_item_id=%s", nix_item_id),
                    process = "GET",
                    mainArrayName = "foods";

            // Set the Request Content-Type Header Parameter
            ArrayList<Pair<String, String>> properties = new ArrayList<>(Arrays.asList(
                    new Pair<>("Content-Type", "application/json; utf-8"),
                    new Pair<>("x-app-id", appID),
                    new Pair<>("x-app-key", appKey)
            ));

            //#################################
            // Getting Parsed Json Results
            //#################################
            ArrayList<LinkedHashMap<String, Object>> results = parseJsonResponse2(process, urlLink, properties,null,mainArrayName, natural_Nutrients_API_DesiredFields);

            if (results == null)
            {
                return null;
            }
            return results.get(0);
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError getNutritionalInfo() \n'' %s ''", e);
            return null;
        }
    }
    //#################################################################################################################

    private ArrayList<LinkedHashMap<String, Object>> parseJsonResponse2(String process, String urlLink, ArrayList<Pair<String, String>> properties, String jsonInputString, String mainArrayName, ArrayList<String> desiredFields)
    {
        try
        {
            //#####################################################################
            // Getting Data From API End Point
            //#####################################################################
            // API END Point Link
            URL url = new URL(urlLink);

            // Create Connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the Request Method
            process = process.toUpperCase();
            con.setRequestMethod(process);

            //
            con.setDoOutput(true);


            // Setting Header
            for (Pair<String, String> property : properties)
            {
                con.setRequestProperty(property.getValue0(), property.getValue1());
            }

            //#####################################################################
            // Reading JSON Data FROM API
            //#####################################################################
            if (process.equals("POST"))
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

            ArrayList<LinkedHashMap<String, Object>> productResults = new ArrayList();

            String jsonString = response.toString(); // convert stringBuilder object to string from  process above
            JSONObject jsonObjectFromString = new JSONObject(jsonString); // convert string to JSON Object

            JSONArray foods = jsonObjectFromString.getJSONArray(mainArrayName); // getting main json array

            // Looping through json Array and storing data
            Iterator<Object> iterator = foods.iterator();
            while (iterator.hasNext())
            {
                JSONObject jsonObject = (JSONObject) iterator.next();

                LinkedHashMap<String, Object> foodNutritionalInfo = new LinkedHashMap<>();

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
//                                    System.out.printf("\n%s : %s", picKey, picKeyData);

                                    foodNutritionalInfo.put(picKey, picKeyData);
                                }
                            }
                            continue;
                        }

//                        System.out.printf("\n%s : %s", key, keyData);

                        foodNutritionalInfo.put(key, keyData);
                    }
                }

                productResults.add(foodNutritionalInfo);
            }

            return productResults;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError parseJsonResponse() \n'' %s ''", e);
            return null;
        }
    }
}

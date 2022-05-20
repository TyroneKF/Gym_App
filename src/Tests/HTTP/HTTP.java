package Tests.HTTP;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;


public class HTTP
{
    private String mainArrayName = "foods";

    private String SecondArrayName = "full_nutrients";
    private String secondArrayKeyName = "attr_id";
    private String secondArrayValueKeyName = "value";

    private ArrayList<String> desiredNutritionalFields = new ArrayList<>(Arrays.asList(
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

            "photo"
    ));

    private LinkedHashMap<String, Object> foodNutritionalInfo = new LinkedHashMap<>();

    public static void main(String[] args)
    {
        new HTTP();

    }

    HTTP()
    {
        if (getNutritionalInfo("100g of chicken"))
        {
            parseFurtherNutritionalInfo();
        }
    }

    public boolean getNutritionalInfo(String food)
    {
        try
        {
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
            con.setRequestProperty("x-app-id", "22210106");
            con.setRequestProperty("x-app-key", "7e4f361cf3d659726c2e1ead771ec52e");
            con.setRequestProperty("x-remote-user-id", "0");

            // Ensure the Connection Will Be Used to Send Content
            con.setDoOutput(true);

            // Create Request Body Json (Custom JSON String)
            String jsonInputString = String.format("{\n  \"query\":\"%s\",\n  \"timezone\": \"US/Eastern\"\n}", food);

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
                System.out.println("\n" + response);
            }

            //#####################################################################
            // Parsing Response & Storing Data
            //#####################################################################
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
                    System.out.printf("\n%s : %s", key, keyData);
                    if (desiredNutritionalFields.contains(key))
                    {
                        foodNutritionalInfo.put(key, keyData);
                    }
                }
            }

            System.out.printf("\n\n########################''");

            foodNutritionalInfo.entrySet().forEach(entry -> {
                System.out.printf("\n%s : %s", entry.getKey(), entry.getValue());
            });

            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError getNutritionalInfo() \n'' %s ''", e);
            return false;
        }
    }

    public boolean parseFurtherNutritionalInfo()
    {
        try
        {
            System.out.printf("\n\n########################''");

            //####################################
            // Parsing Full Nutrients
            //####################################
            JSONArray ar = (JSONArray) foodNutritionalInfo.get(SecondArrayName);

            Iterator<Object> iterator = ar.iterator();
            while (iterator.hasNext())
            {
                JSONObject jsonObject = (JSONObject) iterator.next();
                System.out.printf("\n%s : %s", jsonObject.get(secondArrayKeyName), jsonObject.get(secondArrayValueKeyName));

            }
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError  parseFurtherNutritionalInfo() \n'' %s ''", e);
            return false;
        }

    }


}

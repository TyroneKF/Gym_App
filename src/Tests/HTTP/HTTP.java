package Tests.HTTP;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.*;


public class HTTP
{
    private String mainArrayName = "foods";

    // full_nutrients
    private String secondArrayName = "full_nutrients";
    private String secondArrayKeyName = "attr_id";
    private String secondArrayValueKeyName = "value";

    // photo
    private String thirdArrayName = "photo";

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

            "photo",
            "thumb",
            "highres"
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

            //#######################################################################
            // Parsing Full Nutrients
            //#######################################################################
            JSONArray ar2 = (JSONArray) foodNutritionalInfo.get(secondArrayName);


            //#####################################
            // Parsing Full Nutrients
            //#####################################
            Iterator<Object> iterator2 = ar2.iterator();
            while (iterator2.hasNext())
            {

                JSONObject jsonObject = (JSONObject) iterator2.next();

                String attr_id = jsonObject.get(secondArrayKeyName).toString();
                String attr_Name = getAttr_ID_Name(attr_id);
                Object attr_Value = jsonObject.get(secondArrayValueKeyName);

                System.out.printf("\n%s : %s", attr_id, attr_Value );

                if(attr_Name != null)
                {

                }
                else
                {
                    System.out.printf("""
                            \n\nError  parseFurtherNutritionalInfo() 
                            \nIssues Extracting Attribute Name for attr_id = %s""", attr_id );
                    return false;
                }
            }

            //#######################################################################
            // Parsing Photo Info
            //#######################################################################
            JSONObject jsonObject = (JSONObject) foodNutritionalInfo.get(thirdArrayName);

            Set<String> keys = jsonObject.keySet();
            for (String key : keys)
            {
                if (desiredNutritionalFields.contains(key))
                {
                    foodNutritionalInfo.put(key, jsonObject.get(key));
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
            return true;
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError  parseFurtherNutritionalInfo() \n'' %s ''", e);
            return false;
        }
    }

    public String getAttr_ID_Name(String attr_id)
    {
        try
        {
            return "";
        }
        catch (Exception e)
        {
            System.out.printf("\n\nError  getAttr_ID_Name() \n'' %s ''", e);
            return null;
        }
    }

/*    private boolean getCSVData(String pathToCsv)
    {
        //############################################################################################
        // Get File From Path
        //############################################################################################
        BufferedReader csvReader = null;
        try
        {
            csvReader = new BufferedReader(new FileReader(pathToCsv));
        }
        catch (FileNotFoundException e)
        {
            System.out.printf("\n\nFile Not Found Error!\n");
            e.printStackTrace();
            return false;
        }

        //############################################################################################
        // Reading Lines From File From Path
        //############################################################################################

        //Creating Arraylists for the columns of the  CSV data we need for the GA
        int noOfColumns = usefulColsInData.length;

        //####################################
        // Adding data from file to memory
        //####################################
        try
        {
            // skip the first line (column names)
            csvReader.readLine();

            String row;

            // Reading through the rest of the file
            int rowCount = 0;
            while ((row = csvReader.readLine())!=null)
            {
                String[] rowData = row.split(","); // the whole row data col1,col2,col3.....
                //System.out.printf("\n\nColumns In Row %d!", rowData.length);
                //System.out.printf("\nRow Data: %s", Arrays.toString(rowData));

                // System.out.printf("\nDouble Row Data:");

                // For each column in the data get the useful columns data we need
                int doubleDataPos = 0;
                Double[] doubleData = new Double[usefulColsInData.length];// Row Data, storing the info we only need

                for (int i : usefulColsInData)
                {
                    int index = i - 1;
                    String columnData = rowData[index]; // a specific column in the row data of the CSV File

                    Double conversionValue;
                    if (columnData.equals("N/A"))
                    {
                        conversionValue = Double.NaN;
                    }
                    else
                    {
                        conversionValue = Double.valueOf(columnData);
                    }

                    doubleData[doubleDataPos] = conversionValue;
                    //System.out.printf(" %s,", conversionValue);
                    doubleDataPos++;
                }

                collectionOfColumnData.add(doubleData);

                rowCount++;
            }
            csvReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        // Set How many days are in the trading session
        daysInTradingSession = collectionOfColumnData.size(); // number of days in Trading Session
        return true;
    }*/


}

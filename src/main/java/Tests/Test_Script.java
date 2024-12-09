package Tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test_Script
{

    public static void main(String[] args)
    {
        System.out.printf("%s",doesStringContainCharacters("fhffk ! 'fZXxx "));
    }

    public static boolean doesStringContainCharacters(String input)
    {
        // pattern anything that isn't an alphabet
        Pattern p1 = Pattern.compile("'", Pattern.CASE_INSENSITIVE);

        // Apply condition
        Matcher m1 = p1.matcher(input.replaceAll("\\s+", ""));


        boolean b1 = m1.find();

        if (b1)
        {
            return true;
        }

        return false;
    }


}

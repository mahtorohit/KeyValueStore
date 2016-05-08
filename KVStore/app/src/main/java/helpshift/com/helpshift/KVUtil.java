package helpshift.com.helpshift;

import java.util.List;

/**
 * Created by Rohit on 09/05/16.
 */
public class KVUtil {
    public static boolean isNumeric(String str){
        String regexStr = "^[0-9]*$";
        if(str.trim().matches(regexStr)) {
            return true;
        }else{
            return false;
        }
    }

    static String formatedArray(List<String> keys) {
        String returnString= "";
        for(String k : keys){
            returnString = returnString +"'"+k+"',";
        }
        returnString =  returnString.substring(0,returnString.length()-1);
        return returnString;
    }
}

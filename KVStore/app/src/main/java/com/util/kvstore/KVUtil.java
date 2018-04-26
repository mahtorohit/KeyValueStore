package com.util.kvstore;

import java.util.List;

/**
 * Created by Rohit on 09/05/16.
 */
public class KVUtil {

    public static enum DATATYPE{
        INT(0),
        BOOL(1),
        LONG(2),
        STRING(3),
        DATE(4);
        private final int value;
        DATATYPE(final int newValue) {
            value = newValue;
        }
        public int getValue() { return value; }
    }


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
         int q = DATATYPE.INT.getValue();
        returnString =  returnString.substring(0,returnString.length()-1);
        return returnString;
    }
}

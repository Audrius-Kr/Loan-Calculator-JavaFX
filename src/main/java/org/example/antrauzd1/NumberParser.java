package org.example.antrauzd1;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberParser {
    public static double percentageStringToDouble(String value) throws ParseException {
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        return percentFormat.parse(value).doubleValue();
    }
}
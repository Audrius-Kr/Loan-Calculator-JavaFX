package org.example.antrauzd1;

import java.text.NumberFormat;



public class NumberFormatter {
    public static String doubleToPercentageString(double value) {
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(2); // Ensures two decimal places
        return percentFormat.format(value);
    }
}
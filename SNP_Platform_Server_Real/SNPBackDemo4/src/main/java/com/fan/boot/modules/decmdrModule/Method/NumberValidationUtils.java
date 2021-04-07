package com.fan.boot.modules.decmdrModule.Method;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberValidationUtils {
    public NumberValidationUtils() {
    }

    private boolean isMatch(String regex, String orginal) {
        if (orginal != null && !orginal.trim().equals("")) {
            Pattern pattern = Pattern.compile(regex);
            Matcher isNum = pattern.matcher(orginal);
            return isNum.matches();
        } else {
            return false;
        }
    }

    public boolean isPositiveInteger(String orginal) {
        return this.isMatch("^\\+{0,1}[1-9]\\d*", orginal);
    }

    public boolean isNegativeInteger(String orginal) {
        return this.isMatch("^-[1-9]\\d*", orginal);
    }

    public boolean isWholeNumber(String orginal) {
        return this.isMatch("[+-]{0,1}0", orginal) || this.isPositiveInteger(orginal) || this.isNegativeInteger(orginal);
    }

    public boolean isPositiveDecimal(String orginal) {
        return this.isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal);
    }

    public boolean isNegativeDecimal(String orginal) {
        return this.isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal);
    }

    public boolean isDecimal(String orginal) {
        return this.isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    public boolean isRealNumber(String orginal) {
        return this.isWholeNumber(orginal) || this.isDecimal(orginal) || this.isScientificNotation(orginal);
    }

    public boolean isScientificNotation(String orginal) {
        return this.isMatch("[+-]?(?:0|[1-9]\\d*)(?:\\.\\d*)?(?:[eE][+-]?\\d+)?", orginal);
    }
}

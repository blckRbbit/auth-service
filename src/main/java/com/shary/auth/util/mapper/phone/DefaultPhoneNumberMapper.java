package com.shary.auth.util.mapper.phone;

import com.shary.auth.util.mapper.PhoneNumberMapper;

public class DefaultPhoneNumberMapper implements PhoneNumberMapper {

    @Override
    public String convert(String phone) {
        String[] symbols = phone.split("");
        StringBuilder builder = new StringBuilder();
        for (String symbol : symbols) {
            if (isDigit(symbol)) {
                builder.append(symbol);
            }
        }

        String result = builder.toString();

        if (result.length() > 7 && result.startsWith("7")) {
            result = "8" + result.substring(1);
        }

        return result;
    }

    private boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

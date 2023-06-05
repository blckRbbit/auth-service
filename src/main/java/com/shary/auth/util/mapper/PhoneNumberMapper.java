package com.shary.auth.util.mapper;

@FunctionalInterface
public interface PhoneNumberMapper {
    String convert(String phone);
}

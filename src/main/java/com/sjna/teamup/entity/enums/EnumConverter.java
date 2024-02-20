package com.sjna.teamup.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EnumConverter<T extends Enum<T> & EnumFlagable<T>> implements AttributeConverter<T, Character> {

    private final Class<T> clazz;

    public EnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Character convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.get();
    }

    @Override
    public T convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return Enum.valueOf(clazz, "NONE");
        }
        T[] enums = clazz.getEnumConstants();
        for (T anEnum : enums) {
            if (anEnum.get() == dbData) {
                return anEnum;
            }
        }
        return null;
    }
}
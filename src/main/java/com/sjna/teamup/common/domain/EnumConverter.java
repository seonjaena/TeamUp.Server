package com.sjna.teamup.common.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EnumConverter<T extends Enum<T> & EnumFlag<T>> implements AttributeConverter<T, Character> {

    private final Class<T> clazz;

    public EnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Character convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.getFlag();
    }

    @Override
    public T convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return Enum.valueOf(clazz, "NONE");
        }
        T[] enums = clazz.getEnumConstants();
        for (T anEnum : enums) {
            if (anEnum.getFlag() == dbData) {
                return anEnum;
            }
        }
        return null;
    }
}
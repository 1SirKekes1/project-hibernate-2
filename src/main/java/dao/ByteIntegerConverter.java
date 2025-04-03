package dao;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ByteIntegerConverter implements AttributeConverter<Byte, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Byte attribute) {
        return attribute != null ? attribute.intValue() : null;
    }

    @Override
    public Byte convertToEntityAttribute(Integer dbData) {
        return dbData != null ? dbData.byteValue() : null;
    }
}

package dao;


import entity.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {
    @Override
    public String convertToDatabaseColumn(Rating rating) {
        if (rating == null) return null;
        return rating.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        for (Rating rating : Rating.values()) {
            if (rating.getValue().equals(dbValue)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown rating: " + dbValue);
    }
}

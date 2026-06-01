package com.serendib.api.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.stream.Collectors;

// @Converter(autoApply = false) = we apply this manually per field
// Converts float[] (Java) ←→ "[0.1,0.2,0.3,...]" (PostgreSQL vector)
@Converter
public class FloatArrayToVectorConverter
        implements AttributeConverter<float[], String> {

    // float[] → String for INSERT/UPDATE
    @Override
    public String convertToDatabaseColumn(float[] attribute) {
        if (attribute == null) return null;

        String values = new StringBuilder("[")
                .append(
                        java.util.stream.IntStream.range(0, attribute.length)
                                .mapToObj(i -> String.valueOf(attribute[i]))
                                .collect(Collectors.joining(","))
                )
                .append("]")
                .toString();

        return values;
    }

    // String → float[] for SELECT
    @Override
    public float[] convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;

        // Remove brackets: "[0.1,0.2,0.3]" → "0.1,0.2,0.3"
        String cleaned = dbData.replace("[", "").replace("]", "");
        String[] parts = cleaned.split(",");

        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
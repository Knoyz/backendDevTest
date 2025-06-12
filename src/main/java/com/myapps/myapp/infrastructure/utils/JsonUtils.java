package com.myapps.myapp.infrastructure.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException("Utility class cannot be instantiated");
        // Private constructor to prevent instantiation
    }

    /**
     * Converts a JSON string to a Java object of the specified type.
     *
     * @param json  The JSON string to convert.
     * @param clazz The class of the type to convert to.
     * @param <T>   The type of the object to return.
     * @return An instance of the specified type containing the data from the JSON
     *         string.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON", e);
        }

    }

    /**
     * Converts a Java object to a JSON string.
     *
     * @param object The object to convert.
     * @return A JSON string representation of the object.
     */
    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON", e);
        }
    }

    public static List<String> parseJsonToList(String json) {
        try {
            if (json == null || json.isBlank()) {
                log.warn("Empty or null JSON response");
                return Collections.emptyList();
            }
            String cleanedJson = json.strip().replace("[", "").replace("]", "");
            if (cleanedJson.isEmpty()) {
                log.warn("Empty JSON response after cleaning: {}", json);
                return Collections.emptyList();
            }
            return Arrays.asList(cleanedJson.split(","));
        } catch (Exception e) {
            log.error("Error parsing JSON response: {}", json, e);
            throw new IllegalArgumentException("Failed to parse JSON to list", e);
        }
    }

}

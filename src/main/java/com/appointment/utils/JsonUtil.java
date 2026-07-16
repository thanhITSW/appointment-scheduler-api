package com.appointment.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.appointment.exception.InternalProcessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    static {
        // Register JavaTimeModule to handle Java 8 date/time types like Instant
        mapper.registerModule(new JavaTimeModule());
        
        // Configure date format
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        
        // Configure to handle Hibernate proxies
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String writeObjectToJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error when write object to json: {}", obj);
            throw new InternalProcessException(e.getMessage());
        }
    }

    public static <T> T parseJson2Java(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("JSON parser string convert to object exception", e);
            return null;
        }
    }

    public static <T> T convertObjectToObject(Object object, Class<T> clazz) {
        return mapper.convertValue(object, clazz);
    }
}

package io.github.gogleowner.jacksonobjectmapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonObjectMapperDateFormatTest {

    @Test
    void basicSerializeAndDeserialize() throws Exception {
        ObjectMapper basicObjectMapper = new ObjectMapper();

        // json -> object : will occur exception. Cannot construct instance of `java.time.LocalDateTime` (no Creators, like default construct, exist)
        String testJson = "{" +
                "\"id\": \"blablabla\"," +
                "\"date\": \"2020-01-11 12:00:00\"" +
                "}";
        assertThrows(InvalidDefinitionException.class, () -> basicObjectMapper.readValue(testJson, Document.class));

        // object -> json : date field value is LocalDatetime.toString() json object.
        Document document = new Document("blablabla", LocalDateTime.of(2020, 1, 11, 12, 0, 0));
        String json = basicObjectMapper.writeValueAsString(document);
        assertNotEquals(json, testJson);
        /*
        actual result is
        {
            "id":"blablabla",
            "date":{
                "year":2020,
                "month":"JANUARY",
                "nano":0,
                .....
            }
         }
         */
    }

    @Test
    void customSerializeAndDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModules(
                        new SimpleModule()
                                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                );

        Document document = new Document("blablabla", LocalDateTime.of(2020, 1, 11, 12, 0, 0));
        String testJson = "{" +
                "\"id\":\"blablabla\"," +
                "\"date\":\"2020-01-11 12:00:00\"" +
                "}";

        // object -> json : using LocalDateTimeSerializer
        assertEquals(testJson, objectMapper.writeValueAsString(document));

        // json -> object : using LocalDateTimeDeserializer
        assertEquals(document, objectMapper.readValue(testJson, Document.class));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String id;
        private LocalDateTime date;
    }

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
        public LocalDateTimeSerializer() {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.format(dateTimeFormatter));
        }
    }

    static class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
        public LocalDateTimeDeserializer() {
            super(LocalDateTime.class);
        }
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDateTime.parse(p.getValueAsString(), dateTimeFormatter);
        }
    }
}

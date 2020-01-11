package io.github.gogleowner.jacksonobjectmapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonObjectMapperEnumTest {
    @Test
    void basicEnumSerializeAndDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // desc 필드의 값을 json에 필드로 쓰고 싶지만 enum 키 값으로 된다.
        String testJson = "{\"id\":\"blabla\",\"status\":\"Fail\"}";
        StatusHolder statusHolder = new StatusHolder("blabla", Status.F);

        // object -> json
        assertNotEquals(testJson, objectMapper.writeValueAsString(statusHolder));

        // json -> object : will occur exception. Cannot deserialize value of type com...$Status` from String "Fail": not one of the values accepted for Enum class
        Assertions.assertThrows(InvalidFormatException.class, () -> objectMapper.readValue(testJson, StatusHolder.class));
    }

    @Test
    void customSerializeAndDeserialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(
                        new SimpleModule()
                                .addSerializer(Status.class, new StatusSerializer())
                                .addDeserializer(Status.class, new StatusDeserializer())
                );

        // desc 필드의 값을 json에 필드로 쓰고 싶지만 enum 키 값으로 된다.
        String testJson = "{\"id\":\"blabla\",\"status\":\"Fail\"}";
        StatusHolder statusHolder = new StatusHolder("blabla", Status.F);

        // object -> json
        assertEquals(testJson, objectMapper.writeValueAsString(statusHolder));

        // json -> object
        assertEquals(statusHolder, objectMapper.readValue(testJson, StatusHolder.class));

        // json -> object. when invalid status value
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"id\":\"blabla\",\"status\":\"***INVALID***\"}", StatusHolder.class));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHolder {
        private String id;
        private Status status;
    }

    enum Status {
        P("Pass"), F("Fail");
        private String desc;

        Status(String desc) {
            this.desc = desc;
        }


    }

    public static class StatusSerializer extends StdSerializer<Status> {
        public StatusSerializer() {
            super(Status.class);
        }

        @Override
        public void serialize(Status value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.desc);
        }
    }

    public static class StatusDeserializer extends StdDeserializer<Status> {
        public StatusDeserializer() {
            super(Status.class);
        }

        @Override
        public Status deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            for (Status status : Status.values()) {
                if (status.desc.equals(p.getValueAsString())) {
                    return status;
                }
            }

            throw new IllegalArgumentException(
                    "Status field hos not value : " + p.getValueAsString() +
                            " / status: " + Arrays.toString(Status.values()));
        }
    }
}

package org.flywaydb.core.internal.util;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flywaydb.core.api.FlywayException;

import java.io.File;
import java.io.FileWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {
    public static String jsonToFile(String folder, String filename, String json, String... properties) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        for (String property : properties) {
            obj = obj.getAsJsonObject(property);
        }
        return jsonToFile(folder, filename, obj);
    }

    public static String jsonToFile(String folder, String filename, Object json) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        String fullFilename = folder + File.separator + filename + ".json";
        try (FileWriter fileWriter = new FileWriter(fullFilename)) {
            gson.toJson(json, fileWriter);
            return fullFilename;
        } catch (Exception e) {
            throw new FlywayException("Unable to write JSON to file: " + e.getMessage());
        }
    }

    public static Object parseJsonArray(String json) {
        return JsonParser.parseString(json).getAsJsonArray();
    }
}
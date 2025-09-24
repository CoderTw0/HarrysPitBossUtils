package com.lukflug.examplemod8forge.module.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PersistenceHelper {
    private static final Gson gson = new Gson();
    private static final File configFile = new File("config/BossHelper.json");
    private static JsonObject data = new JsonObject();

    public static void load() {
        try {
            if (configFile.exists()) {
                FileReader reader = new FileReader(configFile);
                data = gson.fromJson(reader, JsonObject.class);
                reader.close();
                if (data == null) data = new JsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            if (!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson(data, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getInt(String module, String setting, int def) {
        if (data.has(module)) {
            JsonObject modObj = data.getAsJsonObject(module);
            if (modObj.has(setting)) return modObj.get(setting).getAsInt();
        }
        return def;
    }

    public static void setInt(String module, String setting, int value) {
        JsonObject modObj;
        if (data.has(module)) {
            modObj = data.getAsJsonObject(module);
        } else {
            modObj = new JsonObject();
            data.add(module, modObj);
        }
        modObj.addProperty(setting, value);
    }

    public static boolean getBoolean(String module, String setting, boolean def) {
        if (data.has(module)) {
            JsonObject modObj = data.getAsJsonObject(module);
            if (modObj.has(setting)) return modObj.get(setting).getAsBoolean();
        }
        return def;
    }

    public static void setBoolean(String module, String setting, boolean value) {
        JsonObject modObj;
        if (data.has(module)) {
            modObj = data.getAsJsonObject(module);
        } else {
            modObj = new JsonObject();
            data.add(module, modObj);
        }
        modObj.addProperty(setting, value);
    }
}

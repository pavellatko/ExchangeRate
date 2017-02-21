package Logic;

import DataModel.ApiResponse;
import DataModel.RateObject;
import com.google.gson.*;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Created by pavel on 2/20/17.
 */
public class ResponseParser {
    private Gson gson;
    public ResponseParser() {
        gson = new GsonBuilder().registerTypeAdapter(RateObject.class, new RatesDeserializer()).
                setDateFormat("yyyy-MM-dd").create();
    }

    private class RatesDeserializer implements JsonDeserializer<RateObject> {
        @Nullable
        public RateObject deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context) throws JsonParseException {
            RateObject rate = null;
            if (json.isJsonObject()) {
                Set<Map.Entry<String, JsonElement>> entries =
                        json.getAsJsonObject().entrySet();
                if (entries.size() > 0) {
                    Map.Entry<String, JsonElement> entry = entries.iterator().next();
                    rate = new RateObject(entry.getKey(), entry.getValue().getAsDouble());
                }
            }
            return rate;
        }
    }

    public ApiResponse parse(String data) throws JsonSyntaxException {
        return gson.fromJson(data, ApiResponse.class);
    }
}

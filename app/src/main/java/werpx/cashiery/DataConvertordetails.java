package werpx.cashiery;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import werpx.cashiery.productmodels.Product;


public class DataConvertordetails {

    @TypeConverter
    public static List<Product> stringToSomeObjectList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Product>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Product> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }
}
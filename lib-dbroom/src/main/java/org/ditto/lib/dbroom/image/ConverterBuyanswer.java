package org.ditto.lib.dbroom.image;

import android.arch.persistence.room.TypeConverter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConverterBuyanswer {
    private static final Gson gson = new GsonBuilder().create();


    //---------------
    @TypeConverter
    public static String buyanswerCommandContent2GsonString(ImageCommand.Content content) {

        if (content == null)
            return null;

        return gson.toJson(content, ImageCommand.Content.class);
    }

    @TypeConverter
    public static ImageCommand.Content gsonString2BuyanswerCommandContent(String gsonString) {

        if (Strings.isNullOrEmpty(gsonString))
            return null;


        return gson.fromJson(gsonString, ImageCommand.Content.class);
    }


}
package com.edu.worx.global.utils;

import android.os.Bundle;
import android.os.Message;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyJsonReader {


    public boolean readJsonStream(FileInputStream in, String mailid) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader, mailid);
        } finally {
            reader.close();
        }
    }

    private void close() {
    }

    public boolean readMessagesArray(JsonReader reader, String mailid) throws IOException {
        List<Message> messages = new ArrayList<Message>();
        boolean found = false;
        reader.beginArray();
        while (reader.hasNext()) {
            found = readMessage(reader, mailid);
            if ( found == true)
                return found;
        }
        reader.endArray();
        return found;
    }


    public boolean readMessage(JsonReader reader, String mailid) throws IOException {
        String st_name = null;
        String st_mailid = null;


        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                st_name = reader.nextString();
            } else if (name.equals("mailid")) {
                st_mailid = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return st_mailid.equals(mailid);
    }
/*
    public List<Double> readDoublesArray(JsonReader reader) throws IOException {
        List<Double> doubles = new ArrayList<Double>();

        reader.beginArray();
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }

    public User readUser(JsonReader reader) throws IOException {
        String username = null;
        int followersCount = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                username = reader.nextString();
            } else if (name.equals("followers_count")) {
                followersCount = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new User(username, followersCount);
    }
    */
}
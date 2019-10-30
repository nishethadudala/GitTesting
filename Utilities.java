package com.matilda.git.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utilities {

    ResponseClone responseClone = new ResponseClone();

    public  boolean isJSONValid(String requestJSON) {
        try {
            new JSONObject(requestJSON);
        } catch (JSONException ex) {
            try {
                new JSONArray(requestJSON);
            } catch (JSONException ex1) {
                responseClone.responseCode = "1001";
                responseClone.responseDesc = "Invalid Request JSON";
                return false;
            }
        }
        return true;
    }
    public boolean isURLValid(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }

        catch (Exception e) {
            responseClone.responseCode = "1002";
            responseClone.responseDesc = "Badly formed URL";
            return false;
        }
    }

    public  boolean isLocalPathValid(String localPath) {

            Path path = Paths.get(localPath);
            if(Files.exists(path))
                return true;
            else
                responseClone.responseCode = "1003";
                responseClone.responseDesc = "Local Path does not exist";
                return false;

    }
}

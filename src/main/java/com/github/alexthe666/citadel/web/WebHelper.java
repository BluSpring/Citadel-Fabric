package com.github.alexthe666.citadel.web;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class WebHelper {

    @Nullable
    public static BufferedReader getURLContents(@NonNull String urlString, @NonNull String backupFileLoc){
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            
            return new BufferedReader(reader);
        } catch (Exception e) { // Malformed URL, Offline, etc.
            e.printStackTrace();
        }
        
        try { // Backup
            return new BufferedReader(new InputStreamReader(WebHelper.class.getClassLoader().getResourceAsStream(backupFileLoc), StandardCharsets.UTF_8));
        } catch (NullPointerException e) { // Can't parse backupFileLoc
            e.printStackTrace();
        }
        
        return null;
    }

}

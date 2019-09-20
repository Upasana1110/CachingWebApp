package com.example.ezoic.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.ezoic.model.CacheManager;
import org.springframework.stereotype.Service;


@Service("cachingService")
public class CachingServiceImpl implements CachingService {

    private static final String USER_AGENT = "Mozilla/5.0";
    public int timeOutt = 0;
    CacheManager cacheManager = CacheManager.getInstance();

    private String getHTTPResponse(URL url){
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            System.out.println("List all headers:");
            Map<String, List<String>> map = con.getHeaderFields();

            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println();
            System.out.println("Get Header by key:");

            String server = con.getHeaderField("Cache-Control");
            String[] cacheControl = server.split(",");
            String[] arr = cacheControl[0].split("=");//max-age = 600
            timeOutt = Integer.parseInt(arr[1]);

            if (server == null) {
                System.out.println("Key 'Content-Type' is not found!");
            } else {
                System.out.println("Cache-Control: " + timeOutt);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception ex) {
            return "<html><body><H1>Internal Server Error</H1></Body></HTML>";
        }
    }

    @Override
    public String getWebsiteData(String queryUrl, Optional<Long> timeout){
        URL url = null;

        try {
            url = new URL(queryUrl);
            String response = cacheManager.GetIfExists(queryUrl, timeout);
            if(response == null){
                response = getHTTPResponse(url);
                cacheManager.AddToCache(queryUrl, response, timeout);
            }

            System.out.println(response);
            return response;
        } catch (MalformedURLException e){
            return "<html><body><H1>Incorrect Url</H1></Body></HTML>";
        }
    }
}
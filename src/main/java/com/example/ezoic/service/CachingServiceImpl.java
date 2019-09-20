package com.example.ezoic.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.example.ezoic.model.CacheManager;
import org.springframework.stereotype.Service;


@Service("cachingService")
public class CachingServiceImpl implements CachingService {

    private static final String USER_AGENT = "Mozilla/5.0";
    public Optional<Long> timeOutt = Optional.empty();
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

            String server = con.getHeaderField("Cache-Control");

            if (server == null) {
                System.out.println("Key 'Cache-Control' is not found");
            } else if(server.contains("no-cache")){
                // Do not cache if the website says no cache
                timeOutt = Optional.of(0L);
            }

            else {
                String[] cacheControl = server.split(",");
                String[] arr = cacheControl[0].split("=");//max-age = 600
                timeOutt = Optional.of(Long.parseLong(arr[1]));
                timeOutt = Optional.of(TimeUnit.SECONDS.toMillis(timeOutt.get()));
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

            // use the user specified timeout
            if(!timeOutt.isPresent()){
                timeOutt = timeout;
            }

            url = new URL(queryUrl);
            String response = cacheManager.GetIfExists(queryUrl, timeOutt);
            if(response == null){
                response = getHTTPResponse(url);

                cacheManager.AddToCache(queryUrl, response, timeOutt);

                // every time after storing reset the timeout
                timeOutt = Optional.empty();
            }

            System.out.println(response);
            return response;
        } catch (MalformedURLException e){
            return "<html><body><H1>Incorrect Url</H1></Body></HTML>";
        }
    }

    @Override
    public String clearCache(String queryUrl){
        URL url = null;

        try {
            url = new URL(queryUrl);
            cacheManager.clearCache(queryUrl);

            return "<html><body><H1>Cache is clear for this url</H1></Body></HTML>";
        } catch (MalformedURLException e){
            return "<html><body><H1>Incorrect Url</H1></Body></HTML>";
        }
    }

    @Override
    public String getCache(){
        ArrayList<String> list = cacheManager.getCache();
        String listOfCache = "<ul>";
        for(int i = 0; i < list.size() ; i++){
            listOfCache += "<li>"+list.get(i);
        }
        listOfCache += "</ul>";
        if(list.size() == 0){
            return "<html><body><H1>The Cache is empty</H1></Body></HTML>";
        }
        return listOfCache;
    }
}
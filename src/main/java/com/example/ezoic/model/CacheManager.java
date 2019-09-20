package com.example.ezoic.model;

import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class CacheManager {
    private HashMap<String, CacheEntry> cache = new HashMap<>();

    private static CacheManager Instance = null;

    private CacheManager(){}

    // TIMEOUT IS 5 MINUTES.
    private static long DEFAULT_TIMEOUT = 300000;

    // Create one instance used throughout the application.
    public static CacheManager getInstance(){
        if (Instance == null) {
            Instance = new CacheManager();
        }
        return Instance;
    }

    public String GetIfExists(String url, Optional<Long> timeout){

        // Remove all slashes from the end of the url.
        url = url.replaceAll("/$", "");

        // Time of request
        Date currentDate = new Date();

        if(cache.containsKey(url)){
            CacheEntry cacheEntry = cache.get(url);

            // If the user has specified a new timeout use it else use existing one.
            if(timeout.isPresent()){
                cacheEntry.TimeToLive = timeout.get();
            }
             else {
                // no change use the existing timeout.
            }

            long timeDiff = currentDate.getTime() - cacheEntry.TimeOfEntry.getTime();


            // See if we can use the cached data.
            if(timeDiff < cacheEntry.TimeToLive){
                System.out.println("Retrieved from cache.");
                return cacheEntry.WebsiteBody;
            }
        }

        // Not found in cache.
        System.out.println("Not present in cache or expired.");
        return null;
    }

    public void AddToCache(String url, String websiteData,  Optional<Long> timeout){

        // Remove all slashes from the end of the url.
        url = url.replaceAll("/$", "");

        CacheEntry cacheEntry = new CacheEntry(url, websiteData, timeout.orElse(DEFAULT_TIMEOUT));
        this.cache.put(url, cacheEntry);
    }
}

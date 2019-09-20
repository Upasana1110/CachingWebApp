package com.example.ezoic.model;

import java.util.Date;

public class CacheEntry {

    private String Url;

    // The actual response of the website body.
    String WebsiteBody;

    // The time data was stored, used to identify the freshness of data.
    Date TimeOfEntry;

    // Time of expiry of the cache.
    long TimeToLive;

    public CacheEntry(String url, String websiteBody, long timeToLive){
        this.Url = url;
        this.WebsiteBody = websiteBody;
        this.TimeToLive = timeToLive;

        // store the current time as the time of entry in cache
        this.TimeOfEntry = new Date();
    }
}

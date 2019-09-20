package com.example.ezoic.controller;

import java.util.Optional;

import com.example.ezoic.service.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// This represents the urls that will be handled by the service
@RestController
public class MainController {

    @Autowired
    private CachingService cachingService;

    // Main source from where the request flows
    @RequestMapping(value = "/cachingService", method = RequestMethod.GET)
    public String getWebsiteData(@RequestParam Optional<String> queryUrl,
                                 @RequestParam Optional<Long> timeout)
            throws Exception{
        String url = (queryUrl.isPresent())? queryUrl.get() : "";
        return cachingService.getWebsiteData(url, timeout);
    }


}

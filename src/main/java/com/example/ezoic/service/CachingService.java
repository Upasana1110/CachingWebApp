package com.example.ezoic.service;

import java.util.Optional;

public interface CachingService {
    String getWebsiteData(String url, Optional<Long> timeout) throws Exception;

    String clearCache(String url) throws Exception;

    String getCache() throws Exception;

}
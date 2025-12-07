
package com.kbaa.flexiblehttpclient;

public final class FlexibleHttpClient {
    
    private FlexibleHttpClient() {
        throw new UnsupportedOperationException("This is a utility class");
    }
    
    public static FlexibleHttpClientBuilder builder(){
        return new FlexibleHttpClientBuilder();
    }
}

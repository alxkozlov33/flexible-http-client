package com.kbaa.flexiblehttpclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

public class FlexibleHttpClientBuilder {
    private String httpProxyHost;
    private Integer httpProxyPort;
    private String httpProxyUser;
    private String httpProxyPassword;
    
    private String socksProxyHost;
    private Integer socksProxyPort;
    
    private RequestConfig config;
    private int timeout = 30000;
    
    
    public FlexibleHttpClientBuilder withHttpProxy(String host, int port) {
        this.httpProxyHost = host;
        this.httpProxyPort = port;
        return this;
    }

    public FlexibleHttpClientBuilder withSocksProxy(String host, int port) {
        this.socksProxyHost = host;
        this.socksProxyPort = port;
        return this;
    }
    
    public FlexibleHttpClientBuilder withHttpProxyAuth(String userName, String password) {
        this.httpProxyUser = userName;
        this.httpProxyPassword = password;
        return this;
    }

    public FlexibleHttpClientBuilder withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    
    public FlexibleHttpClientBuilder withRequestConfig(RequestConfig config) {
        this.config = config;
        return this;
    }
    
    public CloseableHttpClient build() {
        if (config == null){
            config = buildDefaultRequestConfig(); 
        }
        HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(config);
        try {
            ConnectionType connectionType = getConnectionType();
            if (connectionType.hasSocks()){
                builder.setConnectionManager(createSocksConnectionManager());
            }
            if (connectionType.hasHttp()){
                builder.setProxy(new HttpHost(httpProxyHost, httpProxyPort));
            }
            if (connectionType.hasAuth()){
                builder.setDefaultCredentialsProvider(createCredentialsProvider());
            }
            return builder.build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create HTTP client", ex);
        }
    }
    
    private ConnectionType getConnectionType(){
        boolean hasSocks = socksProxyHost != null && socksProxyPort != null;
        boolean hasHttp = httpProxyHost != null && httpProxyPort != null;
        boolean hasAuth = httpProxyUser != null && httpProxyPassword != null;
        
        if (hasAuth && !hasHttp){
            throw new IllegalStateException("Missing host and port for HttpProxy");
        }
        
        if (hasSocks && hasHttp){
            return hasAuth ? ConnectionType.SOCKS_AND_HTTP_PROXY_WITH_AUTH 
                : ConnectionType.SOCKS_AND_HTTP_PROXY;
        } else if (hasSocks){
            return ConnectionType.SOCKS_PROXY;
        } else if (hasHttp){
            return hasAuth ? ConnectionType.HTTP_PROXY_WITH_AUTH 
                : ConnectionType.HTTP_PROXY;
        }
        
        return ConnectionType.DIRECT;
    }

    private enum ConnectionType {
        DIRECT (false, false, false),
        SOCKS_PROXY (true, false, false),
        HTTP_PROXY (false, true, false),
        HTTP_PROXY_WITH_AUTH (false, true, true),
        SOCKS_AND_HTTP_PROXY (true, true, false), 
        SOCKS_AND_HTTP_PROXY_WITH_AUTH (true, true, true);
        
        private final boolean socks;
        private final boolean http;
        private final boolean auth;
        
        private ConnectionType(boolean socks, boolean http, boolean auth){
            this.socks = socks;
            this.http = http;
            this.auth = auth;
        }
        
        private boolean hasSocks(){
            return socks;
        }
        
        private boolean hasHttp(){
            return http;
        }
        
        private boolean hasAuth(){
            return auth;
        }
    }
    
    private PoolingHttpClientConnectionManager createSocksConnectionManager() throws NoSuchAlgorithmException {
        ConnectionSocketFactory plainSF = createPlainConnectionSocketFactory();
        SSLConnectionSocketFactory sslSF = createSSLConnectionSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", plainSF)
            .register("https", sslSF)
            .build();
        return new PoolingHttpClientConnectionManager(registry);
    }
    
    private PlainConnectionSocketFactory createPlainConnectionSocketFactory() {
        return new PlainConnectionSocketFactory() {
            @Override
            public Socket createSocket(HttpContext context) throws IOException {
                return createSocketWithSocksProxy();
            }
        };
    }
    
    private SSLConnectionSocketFactory createSSLConnectionSocketFactory() throws NoSuchAlgorithmException{
        return new SSLConnectionSocketFactory(SSLContext.getDefault()) {
            @Override
            public Socket createSocket(HttpContext context) throws IOException {
                return createSocketWithSocksProxy();
            }
        };
    }
    
    private Socket createSocketWithSocksProxy(){
        Proxy socksProxy = new Proxy(
            Proxy.Type.SOCKS, 
            new InetSocketAddress(socksProxyHost, socksProxyPort)
        );
        return new Socket(socksProxy);
    }
    
    private CredentialsProvider createCredentialsProvider() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(httpProxyHost, httpProxyPort),
            new UsernamePasswordCredentials(httpProxyUser, httpProxyPassword)
        );
        return credentialsProvider;
    }
    
    private RequestConfig buildDefaultRequestConfig() {
        return RequestConfig.custom()
            .setConnectTimeout(timeout)
            .setSocketTimeout(timeout)
            .setConnectionRequestTimeout(timeout)
            .build();
    }
    
}

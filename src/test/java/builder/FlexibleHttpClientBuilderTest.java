package builder;

import com.kbaa.flexiblehttpclient.FlexibleHttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author kbaa
 */
public class FlexibleHttpClientBuilderTest {
    
    @Test
    void shouldCreateDirectConnectionClient() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder();
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Test
    void shouldCreateClientWithTimeout() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder()
            .withTimeout(5000);
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client with timeout should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Test
    void shouldCreateClientWithHttpProxy() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder()
            .withHttpProxy("proxy.example.com", 8080);
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client with HTTP proxy should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Test
    void shouldCreateClientWithSocksProxy() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder()
            .withSocksProxy("socks.example.com", 1080);
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client with SOCKS proxy should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Test
    void shouldCreateClientWithSocksAndHTTPProxy() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder()
            .withSocksProxy("socks.example.com", 1080)
            .withHttpProxy("proxy.example.com", 8080);
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client with SOCKS and HTTP proxy should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }
    
    @Test
    void shouldCreateClientHTTPProxyWithAuth() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder()
            .withHttpProxyAuth("user", "password")
            .withHttpProxy("proxy.example.com", 8080);
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client with HTTP proxy + auth should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }

    @Test
    void shouldCreateClientWithSocksAndHTTPProxyWithAuth() {
        FlexibleHttpClientBuilder builder = new FlexibleHttpClientBuilder()
            .withSocksProxy("socks.example.com", 1080)
            .withHttpProxyAuth("user", "password")
            .withHttpProxy("proxy.example.com", 8080);
        CloseableHttpClient client = builder.build();
        assertNotNull(client, "Client with SOCKS and HTTP proxy + auth should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }
}

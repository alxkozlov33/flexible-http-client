package facade;

import com.kbaa.flexiblehttpclient.FlexibleHttpClient;
import com.kbaa.flexiblehttpclient.FlexibleHttpClientBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author kbaa
 */
public class FlexibleHttpClientTest {
    
    @Test
    void shouldCreateBuilderThroughFacade() {
        FlexibleHttpClientBuilder builder = FlexibleHttpClient.builder();
        assertNotNull(builder, "Builder from facade should not be null");
        assertInstanceOf(
            FlexibleHttpClientBuilder.class, 
            builder, 
            "Should return FlexibleHttpClientBuilder instance"
        );
    }
    
    @Test
    void shouldCreateClientThroughFacade() {
        CloseableHttpClient client = FlexibleHttpClient
            .builder()
            .build();
        assertNotNull(client, "Client from facade should not be null");
        try {
            client.close();
        } catch (Exception e) {
            // ignore
        }
    }

    
}

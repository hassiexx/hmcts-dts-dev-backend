package boo.hassie.java.hmcts.dts.tasks.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public ModelResolver modelResolver() {
        final ObjectMapper copy = Json.mapper().copy();
        return new ModelResolver(copy.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
    }
}

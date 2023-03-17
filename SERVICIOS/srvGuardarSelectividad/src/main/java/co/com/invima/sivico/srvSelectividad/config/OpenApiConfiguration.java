package co.com.invima.sivico.srvSelectividad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI(@Value("0.0.1-SNAPSHOT") String appVersion) {
        return new OpenAPI()
                .info(new Info().title("Informacion").version(appVersion)
                        .description("API encargada de gestionar las operaciones.")
                        .license(new License().name("Apache 2.0").url("http://demo.org")));
    }

}

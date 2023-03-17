package co.com.invima.sivico.srvEnviarDocumentos.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("co.com.invima.sivico.srvEnviarDocumentos")
@EntityScan("co.com.invima.modelopapf.entities")
@PropertySource("classpath:application.properties")
public class AppConfig {

}

package co.com.invima.sivico.srvIntermedioDocumentos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@Configuration
@ConfigurationProperties(prefix = "servicio")
@Primary
public class ConfigProperties {
	
	String urlMaestra;
	String urlAutenticacion;

}

package co.com.invima.sivico.srvSelectividad.client;

import co.com.invima.sivico.srvSelectividad.config.ConfigProperties;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AutenticacionClient {

    private final ConfigProperties configProperties;

    private final WebClient webClient;

    @Autowired
    public AutenticacionClient(WebClient.Builder webClientBuilder, ConfigProperties configProperties) {
        this.configProperties = configProperties;

        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 150000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                });

        this.webClient = webClientBuilder
                .baseUrl(this.configProperties.getUrlAutenticacion())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    public GenericResponseDTO infoUserAutenticado(String token){

        HashMap<String, Object> entrada = new HashMap<>();

        HashMap<String, Object> request = new HashMap<>();
        request.put("access_token",token);
        entrada.put("request",request);
        GenericResponseDTO responseDTO = GenericResponseDTO.builder().build();
        try {
            responseDTO =  webClient.post()
                    .uri("/autenticacion/userInfo")
                    .body(Mono.just(entrada), HashMap.class)
                    .retrieve()
                    .onStatus(httpStatus -> HttpStatus.INTERNAL_SERVER_ERROR.equals(httpStatus),
                            clientResponse -> clientResponse
                                    .bodyToMono(GenericResponseDTO.class)
                                    .flatMap(body -> {
                                        var message = body.getObjectResponse().toString();
                                        return Mono.error(new Exception(message));
                                    }))
                    .bodyToMono(GenericResponseDTO.class)
                    .block();
        }catch (Exception e){
            responseDTO = GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).build();
        }
        return responseDTO;

    }

}

package co.com.invima.sivico.srvSelectividad.service;

import co.com.invima.modelopapf.dto.generic.GenericRequestDTO;
import co.com.invima.sivico.srvSelectividad.client.AutenticacionClient;
import co.com.invima.sivico.srvSelectividad.config.ConfigProperties;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;
import co.com.invima.sivico.srvSelectividad.repository.StoredProcedureRepository;
import co.com.invima.sivico.srvSelectividad.service.mapper.GenericMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SelectividadServiceImpl implements SelectividadService {

    private final StoredProcedureRepository storedProcedureRepository;

    private final GenericMapper mapper;

    private final WebClient webClient;

    private final ConfigProperties configProperties;

    private final AutenticacionClient autenticacionClient;

    Gson g = new Gson();


    @Autowired
    public SelectividadServiceImpl(WebClient.Builder webClientBuilder,
                                   ConfigProperties configProperties, StoredProcedureRepository storedProcedureRepository, GenericMapper mapper, AutenticacionClient autenticacionClient) {

        this.configProperties = configProperties;
        this.storedProcedureRepository = storedProcedureRepository;
        this.mapper = mapper;
        this.autenticacionClient = autenticacionClient;


        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 150000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                });

        this.webClient = webClientBuilder
                .baseUrl(this.configProperties.getUrlOnudi())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    @Override
    public GenericResponseDTO postGuardarSelectividad(GenericRequestDTO requestDTO, String autorization, Boolean verificar){

        if (verificar&&!validarToken(autorization))
            return GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build();

        GenericResponseDTO genericResponse = webClient.post()
                .uri("/informacionSelectividad")
                .header("Authorization",autorization)
                .body(Mono.just(requestDTO), GenericRequestDTO.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        clientResponse -> clientResponse
                                .bodyToMono(GenericResponseDTO.class)
                                .flatMap(body -> {
                                    var message = body.getObjectResponse().toString();
                                    return Mono.error(new NoSuchElementException(message));
                                }))
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        clientResponse -> clientResponse
                                .bodyToMono(GenericResponseDTO.class)
                                .flatMap(body -> {
                                    var message = body.getObjectResponse().toString();
                                    return Mono.error(new Exception(message));
                                }))
                .bodyToMono(GenericResponseDTO.class)
                .block();

        if (genericResponse != null && genericResponse.getStatusCode() == HttpStatus.OK.value()) {

            if (genericResponse.getObjectResponse() != null ) {


                JsonElement json = g.toJsonTree(genericResponse.getObjectResponse());

                final String SP = "USP_GuardarSelectividad_I";

                String responseSp = storedProcedureRepository.executeStoredProcedure(SP, json.toString());

                return mapper.genericMapping(responseSp);
            }
        } else {
            return GenericResponseDTO.builder()
                    .message("El recurso solicitado no puede ser encontrado")
                    .objectResponse(null)
                    .statusCode(HttpStatus.NOT_FOUND.value()).build();
        }

        return GenericResponseDTO.builder()
                .message("No se ha podido guardar la selectividad")
                .objectResponse(null)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }


    @Override
    public GenericResponseDTO postGuardarSelectividadError(GenericRequestDTO requestDTO,  String autorization) {

        if (!validarToken(autorization))
            return GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build();


        GenericResponseDTO genericResponse= postGuardarSelectividad(requestDTO,autorization, false);

        if (genericResponse != null && genericResponse.getStatusCode() != HttpStatus.OK.value()) {

            if (genericResponse.getObjectResponse() != null ) {

                JsonElement json = g.toJsonTree(genericResponse.getObjectResponse());

                final String SP = "USP_GuardarSelectividadError_I";

                String responseSp = storedProcedureRepository.executeStoredProcedure(SP, json.toString());

                return mapper.genericMapping(responseSp);
            }
        }

        return GenericResponseDTO.builder()
                .message("No se ha podido guardar la selectividad")
                .objectResponse(null)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }

    public Boolean validarToken(String autorization) {
        String token = "";

        try {
            token = autorization.substring(7, autorization.length());
        }catch (Exception e){
            token = "";
        }

        GenericResponseDTO responseDTO = autenticacionClient.infoUserAutenticado(token);

        if (!Objects.isNull(responseDTO)&&responseDTO.getStatusCode()==200)
            return true;
        else
            return false;

    }


}

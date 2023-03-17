package co.com.invima.sivico.srvEnviarDocumentos.service;


import co.com.invima.sivico.srvEnviarDocumentos.client.AutenticacionClient;
import co.com.invima.sivico.srvEnviarDocumentos.config.ConfigProperties;
import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericResponseDTO;
import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericSPBodyDTO;
import co.com.invima.sivico.srvEnviarDocumentos.repository.StoredProcedureRepository;
import co.com.invima.sivico.srvEnviarDocumentos.service.mapper.GenericMapper;
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

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EnviarDocumentosServiceImpl implements EnviarDocumentosService {

    private final StoredProcedureRepository storedProcedureRepository;

    private final GenericMapper mapper;

    private final WebClient webClient;

    private final ConfigProperties config;

    private final AutenticacionClient autenticacionClient;

    @Autowired
    public EnviarDocumentosServiceImpl(WebClient.Builder webClientBuilder,
                                       ConfigProperties config, GenericMapper mapper, StoredProcedureRepository storedProcedureRepository, AutenticacionClient autenticacionClient) {
        this.config = config;
        this.mapper = mapper;
        this.storedProcedureRepository = storedProcedureRepository;
        this.autenticacionClient = autenticacionClient;
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 150000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(150000, TimeUnit.MILLISECONDS));
                });

        this.webClient = webClientBuilder
                .baseUrl(this.config.getUrlintermedio())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }


    public GenericResponseDTO postEnviarDocumentos(GenericSPBodyDTO requestDTO, String autorization) {
        for (GenericSPBodyDTO.Archivo temp : requestDTO.getArchivos()) {
            if (!Objects.isNull(temp.getArchivo()) && temp.getArchivo().getBytes().length > 5242880)
                return GenericResponseDTO.builder().message("Alguno de los ficheros tiene un tamaño máximo a 5 MB").build();
        }
        // Si da error llamando al nuevo microservicio  entonces guarda  info en SP
        GenericResponseDTO genericResponse = webClient.get()
                .uri(uribuilder -> uribuilder
                        .path("/consultar")
                        .build())
                .header("Authorization", autorization)
                .retrieve()
                .bodyToMono(GenericResponseDTO.class)
                .block();
        if (genericResponse.getStatusCode() == HttpStatus.OK.value()) {
            // enviar a Sesuite desde el intermedio , como no se encuentra aun , se debe enviar al sharepoint
            return enviarSharePoint(requestDTO, autorization);
            // return genericResponse;
        } else {
            return guardarArchivoSP(requestDTO);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> enviarDocumentos(GenericSPBodyDTO requestDTO, String autorization) {
        if (!validarToken(autorization))
            return new ResponseEntity<>(GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build(), HttpStatus.UNAUTHORIZED);


        try {
            return new ResponseEntity<>(postEnviarDocumentos(requestDTO, autorization), HttpStatus.OK);
        } catch (Exception e) {
            GenericResponseDTO genericResponseDTO = guardarArchivoSP(requestDTO);
            return genericResponseDTO.getStatusCode().equals(HttpStatus.OK.value())
                    ? new ResponseEntity<>(genericResponseDTO, HttpStatus.OK)
                    : new ResponseEntity<>(GenericResponseDTO.builder().message(HttpStatus.INTERNAL_SERVER_ERROR).statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GenericResponseDTO> consulta(String autorization) {
        if (validarToken(autorization))
            return new ResponseEntity<>(GenericResponseDTO.builder().statusCode(HttpStatus.OK.value()).message("OK").build(), HttpStatus.OK);
        else
            return new ResponseEntity<>(GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build(), HttpStatus.UNAUTHORIZED);
    }

    public GenericResponseDTO guardarArchivoSP(GenericSPBodyDTO requestDTO) {

        final String SP = "USP_GuardaArchivosError_I";

        String jsonIn = mapper.requestDtoToJsonStr(requestDTO);

        String responseSp = storedProcedureRepository.executeStoredProcedure(SP, jsonIn);

        return mapper.genericMapping(responseSp);
    }

    public int GetOriginalLengthInBytes(String base64string) throws IOException {

        byte[] result = DatatypeConverter.parseBase64Binary(base64string);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(result);
        if (baos.size() >= 5120)
            System.out.println("Error");
        else
            System.out.println("Bien");
        baos.size();

        return 0;

    }

    public GenericResponseDTO enviarSharePoint(GenericSPBodyDTO requestDTO, String autorization) {

        GenericResponseDTO genericResponse = webClient.post()
                .uri("/enviarSharePoint")
                .header("Authorization", autorization)
                .body(Mono.just(requestDTO), GenericSPBodyDTO.class)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus),
                        clientResponse -> clientResponse
                                .bodyToMono(GenericResponseDTO.class)
                                .flatMap(body -> {
                                    var message = body.getObjectResponse().toString();
                                    return Mono.error(new NoSuchElementException(message));
                                }))
                .onStatus(httpStatus -> HttpStatus.INTERNAL_SERVER_ERROR.equals(httpStatus),
                        clientResponse -> clientResponse
                                .bodyToMono(GenericResponseDTO.class)
                                .flatMap(body -> {
                                    var message = body.getObjectResponse().toString();
                                    return Mono.error(new Exception(message));
                                }))
                .bodyToMono(GenericResponseDTO.class)
                .block();

        if (genericResponse.getStatusCode() == HttpStatus.OK.value()) {
            return genericResponse;
        } else {
            return guardarArchivoSP(requestDTO);
        }
    }

    public Boolean validarToken(String autorization) {
        String token = "";

        try {
            token = autorization.substring(7, autorization.length());
        } catch (Exception e) {
            token = "";
        }

        GenericResponseDTO responseDTO = autenticacionClient.infoUserAutenticado(token);

        if (!Objects.isNull(responseDTO) && responseDTO.getStatusCode() == 200)
            return true;
        else
            return false;

    }
}

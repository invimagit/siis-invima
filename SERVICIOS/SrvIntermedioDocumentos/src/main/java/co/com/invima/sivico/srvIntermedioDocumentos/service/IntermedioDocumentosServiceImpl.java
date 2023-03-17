package co.com.invima.sivico.srvIntermedioDocumentos.service;


import co.com.invima.sivico.srvIntermedioDocumentos.client.AutenticacionClient;
import co.com.invima.sivico.srvIntermedioDocumentos.config.ConfigProperties;
import co.com.invima.sivico.srvIntermedioDocumentos.dto.*;
import co.com.invima.sivico.srvIntermedioDocumentos.repository.StoredProcedureRepository;
import co.com.invima.sivico.srvIntermedioDocumentos.service.mapper.GenericMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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


import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class IntermedioDocumentosServiceImpl implements IntermedioDocumentosService {

    private final StoredProcedureRepository storedProcedureRepository;

    private final GenericMapper mapper;

    private final WebClient webClient;

    private final ConfigProperties config;

    private final AutenticacionClient autenticacionClient;

    @Autowired
    public IntermedioDocumentosServiceImpl(WebClient.Builder webClientBuilder,
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
                .baseUrl(this.config.getUrlMaestra())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    @Override
    public ResponseEntity<GenericResponseDTO> consulta(String autorization) {
       if (validarToken(autorization))
           return new ResponseEntity<>(GenericResponseDTO.builder().statusCode(HttpStatus.OK.value()).message("OK").build(),HttpStatus.OK) ;
       else
           return new ResponseEntity<>(GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build(),HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<GenericResponseDTO> enviarSharePoint(GenericSPBodyDTO request, String autorization) {
        if (!validarToken(autorization))
            return new ResponseEntity<>(GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build(),HttpStatus.UNAUTHORIZED);

        //Separar los elementos archivos para enviar share
        GenericResponseDTO genericResponsefinal= null;

        JSONObject json = new JSONObject();
        json.put("Usuario", "Vuce");
        HashMap<String,String> cabecera= new HashMap<>();
        String numeroRegistro= ((HashMap<String, String>) request.getCabecera()).get("numeroRegistro");

        GenericResponseDTO genericResponse = GenericResponseDTO.builder().build();
        List<ResponseShareDTO> listaresponseShare= new ArrayList<>();

        for (GenericSPBodyDTO.Archivo temp : request.getArchivos()) {

            MaestraTranfDTO maestraTranfDTO = MaestraTranfDTO.builder()
                    .archivoBase64(temp.getArchivo())
                    .idTramite("")
                    .idExpediente("")
                    .numeroRadicado(temp.getNumeroRadicado())
                    .numeroActaSala(0)
                    .idSala("")
                    .idVisita(1L)
                    .tipo("A")
                    .extension("pdf")
                    .idSolicitud("2")
                    .usuarioCrea("Integración ONUDI")
                    .nombreDocumento(temp.getTituloDocumento())
                    .numeroFolios(temp.getNumeroFolios())
                    .build();

            genericResponse = webClient.post()
                    .uri("/MaestraDocumentoSP/crearDirectorio/")
                    .body(Mono.just(maestraTranfDTO), MaestraTranfDTO.class)
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
            if (!Objects.isNull(genericResponse) && genericResponse.getStatusCode() == HttpStatus.OK.value()) {
                ObjectMapper mapper = new ObjectMapper();
                ResponseShareDTO responseShareDTO = mapper.convertValue(genericResponse.getObjectResponse(), ResponseShareDTO.class);
                responseShareDTO.setNumeroRadicado(temp.getNumeroRadicado());
                responseShareDTO.setNumeroRegistro(numeroRegistro);
                listaresponseShare.add(responseShareDTO);
                genericResponse.setObjectResponse(listaresponseShare);
                } else {
                return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
            }
        }
        // Enviar al SP Onudi.USP_GuardarDocumento_I
        GenericResponseDTO guardarresponse=  guardarDocumentoSP(genericResponse,json);
        if (!Objects.isNull(guardarresponse) && guardarresponse.getStatusCode() == HttpStatus.OK.value())
            return new ResponseEntity<>(guardarresponse, HttpStatus.OK);
        else
            return new ResponseEntity<>(guardarresponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    public  GenericResponseDTO guardarDocumentoSP(GenericResponseDTO requestDTO,JSONObject json) {

        RequestAuditoriaDTO paramSP= RequestAuditoriaDTO.builder()
                .cabecera(json)
                .entrada(requestDTO.getObjectResponse()).build();

        final String SP = "USP_GuardarDocumento_I";

        String jsonIn = mapper.requestDtoToJsonStr(paramSP);

        String responseSp = storedProcedureRepository.executeStoredProcedure(SP, jsonIn);

        return mapper.genericMapping(responseSp);
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

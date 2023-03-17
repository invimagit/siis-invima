package co.com.invima.sivico.srvEnviarDocumentos.controller;


import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericRequestDTO;
import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericResponseDTO;
import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericSPBodyDTO;
import co.com.invima.sivico.srvEnviarDocumentos.service.EnviarDocumentosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(
        name = "Onudi Informacion - Controller",
        description = "Controller que gestiona las operaciones Onudi para información"
)
@CrossOrigin(origins = "*", methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/v1/")
public class EnviarDocumentosController {

    private final EnviarDocumentosService service;

    @Operation(summary = "Consulta EnviarDocumentos si responde bien o no ",
            description = "Operación encargada de realizar una consulta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje de confirmación",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
    })
    @GetMapping("consultar")
    public ResponseEntity<GenericResponseDTO> postIntermedioDocumentos(@RequestHeader("Authorization") String autorization) throws IOException {


        return service.consulta(autorization);
    }

    @Operation(summary = "Operación que envia documentos",
            description = "Operación que envia documentos , si da error accede a un intermedio.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Contiene información de archivos",
            content =  @Content(schema = @Schema(
                    type = "RequestDto",
                    example = "{\n" +
                            "\t\"cabecera\": {\n" +
                            "\t\t\"numeroRegistro\": \"REG-123456\",\n" +
                            "\t\t\"solicitante\": \"Carnes la mejor\",\n" +
                            "\t\t\"destinatario\": \"Carnes la mejor\",\n" +
                            "\t\t\"numeroRegistroSanitario\": \"RS-123456\",\n" +
                            "\t\t\"pais\": \"Venezuela\",\n" +
                            "\t\t\"grupoAlimenticio\": \"\",\n" +
                            "\t\t\"indentificación\": \"1234567890\",\n" +
                            "\t\t\"sitioControl\": \"Puerto de Buenaventura\"\n" +
                            "\t},\n" +
                            "\t\"Archivos\": [{\n" +
                            "\t\t\t\"tituloDocumento\": \"Concepto libre\",\n" +
                            "\t\t\t\"fechaRadicado\": \"15/07/2022\",\n" +
                            "\t\t\t\"numeroFolios\": \"2\",\n" +
                            "\t\t\t\"numeroRadicado\": \"RAD-1234\",\n" +
                            "\t\t\t\"Archivo\": \"Formato de Archivo en base 64\"\n" +
                            "\n" +
                            "\t\t},\n" +
                            "\t\t{\n" +
                            "\t\t\t\"tituloDocumento\": \"Visto bueno de importación\",\n" +
                            "\t\t\t\"fechaRadicado\": \"15/07/2022\",\n" +
                            "\t\t\t\"numeroFolios\": \"5\",\n" +
                            "\t\t\t\"numeroRadicado\": \"RAD-1235\",\n" +
                            "\t\t\t\"Archivo\": \"Formato de Archivo en base 64\"\n" +
                            "\n" +
                            "\t\t},\n" +
                            "\t\t{\n" +
                            "\t\t\t\"tituloDocumento\": \"Registro de importación\",\n" +
                            "\t\t\t\"fechaRadicado\": \"15/07/2022\",\n" +
                            "\t\t\t\"numeroFolios\": \"30\",\n" +
                            "\t\t\t\"numeroRadicado\": \"RAD-1236\",\n" +
                            "\t\t\t\"Archivo\": \"Formato de Archivo en base 64\"\n" +
                            "\n" +
                            "\t\t}\n" +
                            "\t]\n" +
                            "}")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje de confirmación",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
    })
    @PostMapping("enviarDocumentos")
    public ResponseEntity<GenericResponseDTO> postEnviarDocumentos(@RequestBody GenericSPBodyDTO requestDTO, @RequestHeader("Authorization") String autorization) {
        return service.enviarDocumentos(requestDTO,autorization);
    }




}

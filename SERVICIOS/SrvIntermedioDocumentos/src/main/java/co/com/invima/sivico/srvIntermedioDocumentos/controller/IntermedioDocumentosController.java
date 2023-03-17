package co.com.invima.sivico.srvIntermedioDocumentos.controller;


import co.com.invima.sivico.srvIntermedioDocumentos.dto.GenericRequestDTO;
import co.com.invima.sivico.srvIntermedioDocumentos.dto.GenericResponseDTO;
import co.com.invima.sivico.srvIntermedioDocumentos.dto.GenericSPBodyDTO;
import co.com.invima.sivico.srvIntermedioDocumentos.service.IntermedioDocumentosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class IntermedioDocumentosController {

    private final IntermedioDocumentosService service;


    @Operation(summary = "Consulta el intermedio si responde bien o no ",
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


    @PostMapping("enviarSharePoint")
    @Operation(summary = "Enviar archivo a Share point")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Enviar archivo a Share point",
            content = @Content(schema = @Schema(implementation = GenericSPBodyDTO.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se ha creado satisfactoriamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDTO.class))}),
    })
    public ResponseEntity<GenericResponseDTO> enviarSharePoint(@RequestBody GenericSPBodyDTO request, @RequestHeader("Authorization") String autorization) {


        return service.enviarSharePoint(request, autorization);
    }

}

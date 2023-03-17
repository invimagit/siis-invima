package co.com.invima.sivico.srvSelectividad.controller;

import co.com.invima.modelopapf.dto.generic.GenericRequestDTO;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;
import co.com.invima.sivico.srvSelectividad.service.SelectividadService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(
        name = "Onudi informacion selectividad - Controller",
        description = "Controller que gestiona las operaciones Onudi para selectividad"
)
@CrossOrigin(origins = "*", methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/v1/")
public class SelectividadController {

    private final SelectividadService service;


    @Operation(summary = "Guardar selectividad",
            description = "Operación encargada de guardar selectividad.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Contiene información de auditoría y parámetros de búsqueda",
            content =  @Content(schema = @Schema(
                    type = "RequestDto",
                    example = "{\"Auditoria\":{\"IP\":\"192.168.0.1\",\"Usuario\":\"Leonardo\"}}")))
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
    @PostMapping("guardarSelectividad")
    public ResponseEntity<GenericResponseDTO> postGuardarSelectividad(@RequestBody GenericRequestDTO requestDTO, @RequestHeader("Authorization") String autorization) {

        return ResponseEntity.status(HttpStatus.OK).body(service.postGuardarSelectividad(requestDTO,autorization,true));
    }


    @Operation(summary = "Guardar selectividad si ocurre un error",
            description = "Operación encargada de guardar selectividad si ocurre un error.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Contiene información de auditoría y parámetros de búsqueda",
            content =  @Content(schema = @Schema(
                    type = "RequestDto",
                    example = "{\"Auditoria\":{\"IP\":\"192.168.0.1\",\"Usuario\":\"Leonardo\"}}")))
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
    @PostMapping("guardarSelectividadError")
    public ResponseEntity<GenericResponseDTO> postGuardarSelectividadError(@RequestBody GenericRequestDTO requestDTO, @RequestHeader("Authorization") String autorization) {

        return ResponseEntity.status(HttpStatus.OK).body(service.postGuardarSelectividadError(requestDTO,autorization));
    }

}

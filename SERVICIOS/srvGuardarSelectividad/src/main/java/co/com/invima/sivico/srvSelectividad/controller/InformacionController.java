package co.com.invima.sivico.srvSelectividad.controller;

import co.com.invima.modelopapf.dto.generic.GenericRequestDTO;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;
import co.com.invima.sivico.srvSelectividad.service.InformacionService;
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
        name = "Onudi Informacion - Controller",
        description = "Controller que gestiona las operaciones Onudi para información"
)
@CrossOrigin(origins = "*", methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/v1/")
public class InformacionController {

    private final InformacionService service;


    @Operation(summary = "Muestra la informacion de la  selectividad",
            description = "Operación encargada de mostrar selectividad.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Contiene información de auditoría y parámetros de búsqueda",
            content =  @Content(schema = @Schema(
                    type = "RequestDto",
                    example = "{\"Auditoria\":{\"IP\":\"192.168.0.1\",\"Usuario\":\"Leonardo\"},\"Parametros\":{\"idSolicitud\":\"20109\",\"idEtapa\": \"1\",\n" +
                            "    \"idTipoActividad\": \"1\"}}")))
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
    @PostMapping("informacionSelectividad")
    public ResponseEntity<GenericResponseDTO> postInformacionSelectividad(@RequestBody GenericRequestDTO requestDTO, @RequestHeader("Authorization") String autorization) {

        return ResponseEntity.status(HttpStatus.OK).body(service.postInformacionSelectividad(requestDTO, autorization));
    }

}

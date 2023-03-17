package co.com.invima.sivico.srvSelectividad.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SolicitudDTO {

    private Long idSolicitud;
    private String tipoActividad;
    private String radicado;
    private String nit;
    private String tipoInspeccion;
    private String numeroBL;
    private String puerto;
    private String bodega;
    private String bultos;
    private String contenedor;
    /*{"IdSolicitud":20109,"tipoActividad":"Exportación","radicado":"20221025368",
            "nit":"1024589913","tipoInspeccion":"01","numeroBL":"TOR8843,BRT-S12345678911,LCD35466,1234",
            "puerto":"Terminal maritimo de Santa Marta","bodega":"Almagrario","bultos":"1","contenedor":" SSTJ1234563, SUEU3421231"}
            */

}

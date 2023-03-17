package co.com.invima.sivico.srvIntermedioDocumentos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseShareDTO implements Serializable {

    String urlSharedPoint;
    Long   idDocumento;
    String numeroRadicado;
    String urlSharedPointEdicion;
    String numeroRegistro;
}

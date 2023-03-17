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
public class MaestraTranfDTO  implements Serializable {

    Long      idTipoDocumental;
    String    nombreDocumento;  // tituloDocumento
    String    descripcionDocumento;
    String    archivoBase64;
    String    numeroRadicado;
    String    numeroRegistro;
    String    numeroFolios;
    Long      idVisita;
    String      idSolicitud;
    String      idTramite;
    String      idExpediente;
    String   idSala;
    String  extension;
    Integer numeroActaSala;
    String tipo;
    String usuarioCrea;
}

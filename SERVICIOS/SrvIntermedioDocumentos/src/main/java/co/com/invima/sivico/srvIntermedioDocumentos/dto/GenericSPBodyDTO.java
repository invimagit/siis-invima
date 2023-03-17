package co.com.invima.sivico.srvIntermedioDocumentos.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericSPBodyDTO implements Serializable {

    @JsonProperty("cabecera")
    private Object cabecera;

    @JsonProperty("Archivos")
    private List<Archivo> Archivos;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Archivo {

        private String tituloDocumento;
        private String fechaRadicado;
        private String numeroFolios;
        private String numeroRadicado;
        private String archivo;

    }



}

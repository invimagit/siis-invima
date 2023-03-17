package co.com.invima.sivico.srvIntermedioDocumentos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericRequestDTO implements Serializable {

    @JsonProperty("Auditoria")
    private Object Auditoria;

    @JsonProperty("Parametros")
    private Object Parametros;

}

package co.com.invima.sivico.srvSelectividad.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class GenericResponseDTO implements Serializable {

    private Object message;
    private Object objectResponse;
    private Integer totalRegistros;
    private Integer statusCode;

}

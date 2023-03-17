package co.com.invima.sivico.srvSelectividad.service;

import co.com.invima.modelopapf.dto.generic.GenericRequestDTO;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SelectividadService {

    GenericResponseDTO postGuardarSelectividad(GenericRequestDTO requestDTO, String autorization, Boolean verificar);

    GenericResponseDTO postGuardarSelectividadError(GenericRequestDTO requestDTO, String autorization);
}

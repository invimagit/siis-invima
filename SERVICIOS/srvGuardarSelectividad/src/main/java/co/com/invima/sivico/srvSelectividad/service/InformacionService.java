package co.com.invima.sivico.srvSelectividad.service;

import co.com.invima.modelopapf.dto.generic.GenericRequestDTO;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;

public interface InformacionService {

     GenericResponseDTO postInformacionSelectividad(GenericRequestDTO requestDTO, String autorization);

}

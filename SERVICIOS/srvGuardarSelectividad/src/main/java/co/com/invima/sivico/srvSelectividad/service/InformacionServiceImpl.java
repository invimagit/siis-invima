package co.com.invima.sivico.srvSelectividad.service;

import co.com.invima.modelopapf.dto.generic.GenericRequestDTO;
import co.com.invima.sivico.srvSelectividad.client.AutenticacionClient;
import co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO;
import co.com.invima.sivico.srvSelectividad.repository.StoredProcedureRepository;
import co.com.invima.sivico.srvSelectividad.service.mapper.GenericMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformacionServiceImpl implements InformacionService {

    private final StoredProcedureRepository storedProcedureRepository;

    private final GenericMapper mapper;

    private final AutenticacionClient autenticacionClient;

   @Override
    public GenericResponseDTO postInformacionSelectividad(GenericRequestDTO requestDTO, String autorization) {
       if (!validarToken(autorization))
           return GenericResponseDTO.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message("No autorizado a acceder al recurso solicitado.").build();

        final String SP = "USP_InformacionSelectividad_S";

        String jsonIn = mapper.requestDtoToJsonStr(requestDTO);

        String responseSp = storedProcedureRepository.executeStoredProcedure(SP, jsonIn);

        return mapper.genericMapping(responseSp);
    }

    public Boolean validarToken(String autorization) {
        String token = "";

        try {
            token = autorization.substring(7, autorization.length());
        }catch (Exception e){
            token = "";
        }

        co.com.invima.sivico.srvSelectividad.dto.GenericResponseDTO responseDTO = autenticacionClient.infoUserAutenticado(token);

        if (!Objects.isNull(responseDTO)&&responseDTO.getStatusCode()==200)
            return true;
        else
            return false;

    }

}

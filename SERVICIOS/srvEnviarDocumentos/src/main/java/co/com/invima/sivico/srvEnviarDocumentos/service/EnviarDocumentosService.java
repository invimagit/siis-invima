package co.com.invima.sivico.srvEnviarDocumentos.service;


import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericResponseDTO;
import co.com.invima.sivico.srvEnviarDocumentos.dto.GenericSPBodyDTO;
import org.springframework.http.ResponseEntity;

public interface EnviarDocumentosService {

     ResponseEntity<GenericResponseDTO> enviarDocumentos(GenericSPBodyDTO requestDTO, String autorization);

     ResponseEntity<GenericResponseDTO> consulta(String autorization);
}

package co.com.invima.sivico.srvIntermedioDocumentos.service;


import co.com.invima.sivico.srvIntermedioDocumentos.dto.GenericRequestDTO;
import co.com.invima.sivico.srvIntermedioDocumentos.dto.GenericResponseDTO;
import co.com.invima.sivico.srvIntermedioDocumentos.dto.GenericSPBodyDTO;
import org.springframework.http.ResponseEntity;

public interface IntermedioDocumentosService {

     ResponseEntity<GenericResponseDTO> consulta(String autorization);

     ResponseEntity<GenericResponseDTO> enviarSharePoint(GenericSPBodyDTO requestDTO, String autorization);

}

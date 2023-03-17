package co.com.invima.sivico.srvIntermedioDocumentos.repository;

public interface StoredProcedureRepository {

    String executeStoredProcedure(String spName, String jsonIn);

}

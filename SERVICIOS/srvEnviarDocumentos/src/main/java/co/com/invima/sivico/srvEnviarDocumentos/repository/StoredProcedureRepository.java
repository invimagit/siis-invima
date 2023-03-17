package co.com.invima.sivico.srvEnviarDocumentos.repository;

public interface StoredProcedureRepository {

    String executeStoredProcedure(String spName, String jsonIn);

}

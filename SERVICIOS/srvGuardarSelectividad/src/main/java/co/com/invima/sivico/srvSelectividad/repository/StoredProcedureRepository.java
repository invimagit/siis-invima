package co.com.invima.sivico.srvSelectividad.repository;

public interface StoredProcedureRepository {

    String executeStoredProcedure(String spName, String jsonIn);

}

package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.model.filter.SubsidiaryFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;

import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.SUBSIDIARY_NOT_FOUND;

public interface SubsidiaryRepository extends UnovationFilterRepository <Subsidiary, String, SubsidiaryFilter>{

    List<Subsidiary> findByMatrixId(String matrixId);

    default Subsidiary findById(String id) {
        Subsidiary subsidiary = findOne(id);
        if(subsidiary == null) throw UnovationExceptions.notFound().withErrors(SUBSIDIARY_NOT_FOUND);
        return subsidiary;
    }

}

package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.model.filter.SubsidiaryFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

import java.util.List;

public interface SubsidiaryRepository extends UnovationFilterRepository <Subsidiary, String, SubsidiaryFilter>{

    List<Subsidiary> findByMatrixId(String matrixId);

}

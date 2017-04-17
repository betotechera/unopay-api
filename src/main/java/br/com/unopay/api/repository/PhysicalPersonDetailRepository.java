package br.com.unopay.api.repository;

import br.com.unopay.api.model.LegalPersonDetail;
import br.com.unopay.api.model.PhysicalPersonDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhysicalPersonDetailRepository extends CrudRepository<PhysicalPersonDetail,String>{}


package br.com.unopay.api.repository;

import br.com.unopay.api.model.LegalPersonDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalPersonDetailRepository extends CrudRepository<LegalPersonDetail,String>{}


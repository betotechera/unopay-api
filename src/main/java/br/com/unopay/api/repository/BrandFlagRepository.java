package br.com.unopay.api.repository;

import br.com.unopay.api.model.BrandFlag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BrandFlagRepository extends CrudRepository<BrandFlag, String> {

    List<BrandFlag> findAll();

}

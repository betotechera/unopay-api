package br.com.unopay.api.repository;

import br.com.unopay.api.model.BrandFlag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface BrandFlagRepository extends CrudRepository<BrandFlag, String> {

    List<BrandFlag> findAll();
    Optional<BrandFlag> findById(String id);

}

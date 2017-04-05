package br.com.unopay.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

public interface UnovationFilterRepository<T,ID extends Serializable, F> extends JpaSpecificationExecutor, CrudRepository<T, ID> {

    default Page<T> findAll(F filter, Pageable pageable){
        return findAll(new Filter<T>(filter), pageable);
    }

    default List<T> findAll(F filter){
        return findAll(new Filter<T>(filter));
    }

}

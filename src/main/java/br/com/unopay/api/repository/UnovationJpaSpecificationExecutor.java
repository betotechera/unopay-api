package br.com.unopay.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UnovationJpaSpecificationExecutor<T, F> extends JpaSpecificationExecutor {

    default Page<T> findAll(F filter, Pageable pageable){
        return findAll(new Filter<T>(filter), pageable);
    }

    default List<T> findAll(F spec){
        return findAll(new Filter<T>(spec));
    }

}

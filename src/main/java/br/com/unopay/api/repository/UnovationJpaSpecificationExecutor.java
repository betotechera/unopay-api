package br.com.unopay.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UnovationJpaSpecificationExecutor<T, R> extends JpaSpecificationExecutor {

    default Page<T> findAll(R spec, Pageable pageable){
        return findAll(new Filter<T>(spec), pageable);
    }

    default List<T> findAll(R spec){
        return findAll(new Filter<T>(spec));
    }

}

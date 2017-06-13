package br.com.unopay.api.repository.filter;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface UnovationFilterRepository<MODEL,ID extends Serializable, FILTER> extends JpaSpecificationExecutor<MODEL>, CrudRepository<MODEL, ID> {

    default Page<MODEL> findAll(FILTER filter, Pageable pageable){
        return findAll(new Filter<>(filter), pageable);
    }

    default List<MODEL> findAll(FILTER filter){
        return findAll(new Filter<>(filter));
    }

}

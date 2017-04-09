package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.Subsidiary;
import br.com.unopay.api.bacen.repository.SubsidiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubsidiaryService {

    @Autowired
    private SubsidiaryRepository repository;

    public Subsidiary create(Subsidiary id) {
        return null;
    }

    public void update(String id, Subsidiary subsidiary) {
    }

    public Subsidiary findById(String id) {
        return null;
    }

    public void delete(String id) {

    }

    public List<Subsidiary> findByMatrixId(String matrixId) {
        return repository.findByMatrixId(matrixId);
    }
}

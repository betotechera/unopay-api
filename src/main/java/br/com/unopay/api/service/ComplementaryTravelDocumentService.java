package br.com.unopay.api.service;


import br.com.unopay.api.model.ComplementaryTravelDocument;
import br.com.unopay.api.repository.ComplementaryTravelDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplementaryTravelDocumentService {

    private ComplementaryTravelDocumentRepository repository;

    @Autowired
    public ComplementaryTravelDocumentService(ComplementaryTravelDocumentRepository repository) {
        this.repository = repository;
    }

    public ComplementaryTravelDocument create(ComplementaryTravelDocument travelDocument) {
        return repository.save(travelDocument);
    }

    public ComplementaryTravelDocument findById(String id) {
        return repository.findOne(id);
    }
}

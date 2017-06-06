package br.com.unopay.api.service;


import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.repository.TravelDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelDocumentService {

    private TravelDocumentRepository repository;

    @Autowired
    public TravelDocumentService(TravelDocumentRepository repository) {
        this.repository = repository;
    }

    public TravelDocument create(TravelDocument travelDocument) {
        return repository.save(travelDocument);
    }

    public TravelDocument findById(String id) {
        return repository.findOne(id);
    }
}

package br.com.unopay.api.service;


import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.repository.TravelDocumentRepository;
import static br.com.unopay.api.uaa.exception.Errors.TRAVEL_DOCUMENT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Optional;
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
        Optional<TravelDocument> document = repository.findById(id);
        return document.orElseThrow(()-> UnovationExceptions.notFound().withErrors(TRAVEL_DOCUMENT_NOT_FOUND));
    }

    public List<TravelDocument> findAll(){
        return repository.findAll();
    }
}

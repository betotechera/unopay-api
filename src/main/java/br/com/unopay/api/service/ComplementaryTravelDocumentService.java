package br.com.unopay.api.service;


import br.com.unopay.api.model.ComplementaryTravelDocument;
import br.com.unopay.api.repository.ComplementaryTravelDocumentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplementaryTravelDocumentService {

    private ComplementaryTravelDocumentRepository repository;

    @Autowired
    public ComplementaryTravelDocumentService(ComplementaryTravelDocumentRepository repository) {
        this.repository = repository;
    }

    public ComplementaryTravelDocument save(ComplementaryTravelDocument travelDocument) {
        return repository.save(travelDocument);
    }

    public List<ComplementaryTravelDocument> findAll(){
        return repository.findAll();
    }

    public ComplementaryTravelDocument findById(String id) {
        return repository.findOne(id);
    }
}

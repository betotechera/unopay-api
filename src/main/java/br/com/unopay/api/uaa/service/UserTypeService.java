package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeService {

    @Autowired
    private UserTypeRepository repository;

    public List<UserType> findAll(){
        return repository.findAll();
    }
}

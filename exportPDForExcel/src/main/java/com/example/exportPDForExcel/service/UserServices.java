package com.example.exportPDForExcel.service;

import java.util.List;

import com.example.exportPDForExcel.model.User;
import com.example.exportPDForExcel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServices {

    @Autowired
    private UserRepository repo;

    public List<User> listAll() {
        return repo.findAll(Sort.by("email").ascending());
    }

}
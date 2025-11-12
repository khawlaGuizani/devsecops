package tn.esprit.spring.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DataSource dataSource;

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);

    @Override
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(User u) {
        User utilisateur = null;
        try {
            log.info("Adding user...");
            utilisateur = userRepository.save(u);
            log.info("User added successfully");
        } catch (Exception e) {
            log.error("Error in addUser(): " + e);
        }
        return utilisateur;
    }

    @Override
    public User updateUser(User u) {
        User userUpdated = null;
        try {
            log.info("Updating user...");
            userUpdated = userRepository.save(u);
            log.info("User updated successfully");
        } catch (Exception e) {
            log.error("Error in updateUser(): " + e);
        }
        return userUpdated;
    }

    @Override
    public void deleteUser(String id) {
        try {
            log.info("Deleting user...");
            userRepository.deleteById(Long.parseLong(id));
            log.info("User deleted successfully");
        } catch (Exception e) {
            log.error("Error in deleteUser(): " + e);
        }
    }

    @Override
    public User retrieveUser(String id) {
        User u = null;
        try {
            u = userRepository.findById(Long.parseLong(id)).get();
        } catch (Exception e) {
            log.error("Error retrieving user: " + e);
        }
        return u;
    }

    // ===== NOUVELLE MÉTHODE SÉCURISÉE (PreparedStatement) =====

    }


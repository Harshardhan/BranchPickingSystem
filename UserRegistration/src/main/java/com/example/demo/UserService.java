package com.example.demo;

import java.util.List;


import com.example.demo.excpetions.InValidUserException;
import com.example.demo.excpetions.UserAlreadyExistsException;
import com.example.demo.excpetions.UserNotFoundException;

public interface UserService {

    User createUser(User user) 
        throws InValidUserException, UserAlreadyExistsException;

    User updateUser(Long id, User updatedUser) 
        throws UserNotFoundException;

    void deleteUser(Long id) 
        throws UserNotFoundException;

    User getUserById(Long id) 
        throws UserNotFoundException;

    List<User> getAllUsers();

    User getUserByUserName(String username) throws UserNotFoundException;
}

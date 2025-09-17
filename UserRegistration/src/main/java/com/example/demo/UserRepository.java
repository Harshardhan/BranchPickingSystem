package com.example.demo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    List<User> findByRolesContaining(Role role);

    
    List<User> findByUserStatus(UserStatus userStatus);

    List<User> findByFirstName(String firstName);

    Optional<User> findByEmailId(String emailId);


    List<User> findByLastName(String lastName);

    List<User> findByMobileNumber(String mobileNumber);
}

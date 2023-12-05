package com.example.test1.UserHandle;

import com.example.test1.UserHandle.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<User, String> {
}

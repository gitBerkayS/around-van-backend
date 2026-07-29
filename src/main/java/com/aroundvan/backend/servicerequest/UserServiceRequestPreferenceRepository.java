package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserServiceRequestPreferenceRepository
        extends JpaRepository<UserServiceRequestPreference, UserServiceRequestPreferenceId> {

    List<UserServiceRequestPreference> findAllByUser(User user);

    void deleteAllByUser(User user);
}

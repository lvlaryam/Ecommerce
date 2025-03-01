package com.app.ecommerce.core.userToken;

import com.app.ecommerce.core.user.User;
import com.app.ecommerce.core.user.utils.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByAccessTokenAndUserRoleAndUser(String accessToken, UserRole userRole, User user);
}

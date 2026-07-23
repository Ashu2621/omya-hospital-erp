package com.omya.hospital.config;

import com.omya.hospital.entity.Role;
import com.omya.hospital.entity.User;
import com.omya.hospital.repository.RoleRepository;
import com.omya.hospital.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

  private static final Logger log = LoggerFactory.getLogger(
    AdminBootstrap.class
  );

  @Bean
  CommandLineRunner bootstrapAdmin(
    UserRepository userRepository,
    RoleRepository roleRepository,
    PasswordEncoder encoder,
    @Value("${app.bootstrap.admin.enabled:false}") boolean enabled,
    @Value("${app.bootstrap.admin.username:}") String username,
    @Value("${app.bootstrap.admin.password:}") String password
  ) {
    return args -> {
      if (!enabled) {
        return;
      }
      if (username.isBlank() || password.length() < 12) {
        throw new IllegalStateException(
          "Development admin bootstrap requires a username and a password of at least 12 characters"
        );
      }
      if (userRepository.findByUsername(username).isPresent()) {
        return;
      }
      Role adminRole = roleRepository
        .findByRoleName("ADMIN")
        .orElseThrow(() ->
          new IllegalStateException("ADMIN role migration is missing")
        );
      User admin = new User();
      admin.setUsername(username);
      admin.setPasswordHash(encoder.encode(password));
      admin.setFullName("Hospital Administrator");
      admin.setRole(adminRole);
      admin.setActive(true);
      userRepository.save(admin);
      log.info("Development administrator created for username={}", username);
    };
  }
}

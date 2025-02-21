package com.michael.document.config;

import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.base.RequestContext;
import com.michael.document.enumeration.Authority;
import com.michael.document.repository.RoleRepository;
import com.michael.document.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

@Configuration
public class AppConfig {

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {

            if (roleRepository.count() == 0) {
                var userRole = new RoleEntity();
                userRole.setName(Authority.USER.name());
                userRole.setAuthorities(Authority.USER);
                //   userRole.setCreatedBy(0L);
                roleRepository.save(userRole);

                var adminRole = new RoleEntity();
                adminRole.setName(Authority.ADMIN.name());
                adminRole.setAuthorities(Authority.ADMIN);
                //  adminRole.setCreatedBy(0L);
                roleRepository.save(adminRole);

                var superAdminRole = new RoleEntity();
                superAdminRole.setName(Authority.SUPER_ADMIN.name());
                superAdminRole.setAuthorities(Authority.SUPER_ADMIN);
                //  superAdminRole.setCreatedBy(0L);
                roleRepository.save(superAdminRole);

                var managerRole = new RoleEntity();
                managerRole.setName(Authority.MANAGER.name());
                managerRole.setAuthorities(Authority.MANAGER);
                //  managerRole.setCreatedBy(0L);
                roleRepository.save(managerRole);
            }

            RequestContext.start();
        };
    }
}

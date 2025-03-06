package com.michael.document.config;

import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.base.RequestContext;
import com.michael.document.enumerations.Authority;
import com.michael.document.repositories.RoleRepository;
import com.michael.document.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.michael.document.constants.AppConstant.STRENGTH;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }


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

    //    @Bean
//    public AuditorAware<Long> auditorAware() {
//        return new ApplicationAuditAware();
//    }
}

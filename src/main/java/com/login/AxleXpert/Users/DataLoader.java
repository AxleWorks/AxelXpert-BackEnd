package com.login.AxleXpert.Users;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return; // don't seed if already present

        User manager1 = new User();
        manager1.setUsername("manager.alex");
        manager1.setPassword("password");
        manager1.setRole("manager");
        manager1.setEmail("manager.alex@example.com");
        manager1.setBranchId(1L);
        manager1.setIs_Active(true);

        User manager2 = new User();
        manager2.setUsername("manager.sara");
        manager2.setPassword("password");
        manager2.setRole("manager");
        manager2.setEmail("manager.sara@example.com");
        manager2.setBranchId(2L);
        manager2.setIs_Active(true);

        User emp1 = new User();
        emp1.setUsername("employee.john");
        emp1.setPassword("password");
        emp1.setRole("employee");
        emp1.setEmail("john.employee@example.com");
        emp1.setBranchId(1L);
        emp1.setIs_Active(true);

        User emp2 = new User();
        emp2.setUsername("employee.lisa");
        emp2.setPassword("password");
        emp2.setRole("employee");
        emp2.setEmail("lisa.employee@example.com");
        emp2.setBranchId(2L);
        emp2.setIs_Active(true);

        userRepository.saveAll(Arrays.asList(manager1, manager2, emp1, emp2));
    }
}

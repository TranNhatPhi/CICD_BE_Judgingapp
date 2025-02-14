package com.project.judging.Utils;

import com.project.judging.Entities.*;
import com.project.judging.Repositories.*;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@Component
public class InitializeDB implements CommandLineRunner {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InitializeDB.class);
    private final JudgeRepository judgeRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolRepository schoolRepository;

    public InitializeDB(JudgeRepository judgeRepository,
                        PasswordEncoder passwordEncoder,
                        SchoolRepository schoolRepository) {
        this.judgeRepository = judgeRepository;
        this.passwordEncoder = passwordEncoder;
        this.schoolRepository = schoolRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (schoolRepository.count() == 0) {
            School school1 = new School();
            school1.setSchoolName("SCIT_UOW");
            schoolRepository.save(school1);

            School school2 = new School();
            school2.setSchoolName("OTHER");
            schoolRepository.save(school2);
        }

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                log.error("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String rawUsername = properties.getProperty("admin.username");
        String rawPassword = properties.getProperty("admin.password");
        if (rawUsername == null || rawPassword == null) {
            log.error("Admin username or password is not set in config.properties");
            return;
        }

        if (!judgeRepository.existsByRole("admin")) {
            Judge admin = new Judge();
            admin.setAccount(rawUsername);
            admin.setPwd(passwordEncoder.encode(rawPassword));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("johnle@uow.edu.au");
            admin.setPhone("0482049306");
            admin.setDescription("Admin Account");
            admin.setRole("admin");
            judgeRepository.save(admin);
        } else {
            log.info("Admin account already exists");
        }
    }
}

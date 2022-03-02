package com.itis.pfr;

import com.itis.pfr.enums.Roles;
import com.itis.pfr.models.Users;
import com.itis.pfr.repositories.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootApplication
public class PfrApplication {

	public static void main(String[] args) {
		SpringApplication.run(PfrApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UsersRepository repository) {
		return args -> {
			Optional<Users> admin = repository.findUsersByEmail("admin@mail.com");
			if(admin.isEmpty()) {
				String encodedPassword = new BCryptPasswordEncoder().encode("test");
				Users u = new Users(
						"admin",
						"admin",
						"admin@mail.com",
						encodedPassword,
						"France",
						List.of(Roles.ADMIN),
						LocalDateTime.now()
						);
				repository.insert(u);
			};
		};
	}

}

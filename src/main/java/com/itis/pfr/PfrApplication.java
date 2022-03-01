package com.itis.pfr;

import com.itis.pfr.enums.Roles;
import com.itis.pfr.models.Users;
import com.itis.pfr.repositories.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class PfrApplication {

	public static void main(String[] args) {
		SpringApplication.run(PfrApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UsersRepository repository) {
		return args -> {
			List<Users> usersList = repository.findAll();
			if(Objects.equals(usersList.size(), 0)) {
				Users u = new Users(
						"admin",
						"admin",
						"admin@mail.com",
						"test",
						"France",
						List.of(Roles.ADMIN),
						LocalDateTime.now()
						);
				repository.insert(u);
			};
		};
	}
}

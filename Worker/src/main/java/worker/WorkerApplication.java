package worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class WorkerApplication {
	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
	}
}

@Service
class CommandLine implements CommandLineRunner {
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserRepo usersRepo;
	public CommandLine(UserRepo usersRepo) {
		this.usersRepo = usersRepo;
	}
	@Override
	public void run(String... args) {
		usersRepo.save(User.builder()
				.username("BOOTSTRAP")
				.password(passwordEncoder.encode("BOOTSTRAP"))
				.email("BOOTSTRAP")
				.role("ROLE_BOOTSTRAP")
				.build());
	}
}
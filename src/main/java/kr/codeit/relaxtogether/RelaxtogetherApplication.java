package kr.codeit.relaxtogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RelaxtogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelaxtogetherApplication.class, args);
	}

}

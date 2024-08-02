package depth.mvp.ns;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NsApplication.class, args);
	}

}

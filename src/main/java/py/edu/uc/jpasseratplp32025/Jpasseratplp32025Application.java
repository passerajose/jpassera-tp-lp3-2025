package py.edu.uc.jpasseratplp32025;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "py.edu.uc.jpasseratplp32025.controller")
public class Jpasseratplp32025Application {

	public static void main(String[] args) {
		SpringApplication.run(Jpasseratplp32025Application.class, args);
	}

}

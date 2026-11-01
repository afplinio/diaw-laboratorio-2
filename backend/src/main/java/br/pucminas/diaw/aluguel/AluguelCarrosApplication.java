package br.pucminas.diaw.aluguel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AluguelCarrosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AluguelCarrosApplication.class, args);
    }
}

package br.com.zyclos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZyclosApplication {

    public static void main(String[] args) {

        SpringApplication.run(ZyclosApplication.class, args);

        System.out.println("API ZYCLOS no ar!");
    }

}

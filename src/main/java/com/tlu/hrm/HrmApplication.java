package com.tlu.hrm;

//import io.jsonwebtoken.security.Keys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrmApplication.class, args);
		
//		byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
//        System.out.println(Base64.getEncoder().encodeToString(key));
	}

}

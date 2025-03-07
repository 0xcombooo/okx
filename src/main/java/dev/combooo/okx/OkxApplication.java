package dev.combooo.okx;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableSpringUtil
@EnableScheduling
public class OkxApplication {

	public static void main(String[] args) {
		SpringApplication.run(OkxApplication.class, args);
	}

}

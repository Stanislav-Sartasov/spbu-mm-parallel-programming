package ru.turbogoose.deanery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.turbogoose.deanery.core.Exam;
import ru.turbogoose.deanery.core.sets.LazySet;
import ru.turbogoose.deanery.core.sets.OptimisticSet;
import ru.turbogoose.deanery.core.sets.Set;

@SpringBootApplication
public class DeaneryApplication {
	Logger logger = LoggerFactory.getLogger(DeaneryApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DeaneryApplication.class, args);
	}

	@Bean
	public Set<Exam> getSetImpl(@Value("${set-impl: }") String setImpl) {
		if ("lazy".equalsIgnoreCase(setImpl)) {
			logger.info("Using LazySet");
			return new LazySet<>();
		}
		logger.info("Using OptimisticSet");
		return new OptimisticSet<>();
	}
}

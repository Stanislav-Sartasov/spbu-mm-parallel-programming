package ru.turbogoose.deanery;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadTest extends Simulation {
    public static final int RPS = 1000;

    public LoadTest() {
        ScenarioBuilder scenario = setupScenario();
        OpenInjectionStep injectionStrategy = constantUsersPerSec(RPS).during(Duration.ofMinutes(3));
        HttpProtocolBuilder protocol = http.baseUrl("http://localhost:8080");

        setUp(scenario
                .injectOpen(injectionStrategy)
                .protocols(protocol));
    }

    private static ScenarioBuilder setupScenario() {
        return CoreDsl.scenario("Load test")
                .feed(setupFeedData())
                .randomSwitch().on(
                        percent(9.0)
                                .then(http("Add").post("/exams")
                                        .queryParam("studentId", "#{studentId}")
                                        .queryParam("courseId", "#{courseId}")
                                        .check(responseTimeInMillis().saveAs("timeout"))))
                .randomSwitch().on(
                        percent(90.0)
                                .then(http("Contains").get("/exams")
                                        .queryParam("studentId", "#{studentId}")
                                        .queryParam("courseId", "#{courseId}")
                                        .check(responseTimeInMillis().saveAs("timeout"))))
                .randomSwitch().on(
                        percent(1.0)
                                .then(http("Remove").delete("/exams")
                                        .queryParam("studentId", "#{studentId}")
                                        .queryParam("courseId", "#{courseId}")
                                        .check(responseTimeInMillis().saveAs("timeout"))));
    }

    private static Iterator<Map<String, Object>> setupFeedData() {
        return Stream.generate(() -> {
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("studentId", ThreadLocalRandom.current().nextLong());
            stringObjectMap.put("courseId", ThreadLocalRandom.current().nextLong());
            return stringObjectMap;
        }).iterator();
    }
}

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
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class StressTest extends Simulation {
    public static final Integer TIMEOUT = 10000;

    public StressTest() {
        ScenarioBuilder scenario = setupScenario();
        OpenInjectionStep injectionStrategy = rampUsersPerSec(10)
                .to(10000)
                .during(Duration.ofMinutes(5));
        HttpProtocolBuilder protocol = http.baseUrl("http://localhost:8080");

        setUp(scenario
                .injectOpen(injectionStrategy)
                .protocols(protocol));
    }

    private static ScenarioBuilder setupScenario() {
        return CoreDsl.scenario("Stress test")
                .feed(setupFeedData())
                .exec(session -> session.set("timeout", 0))
                .randomSwitch().on(
                        percent(9.0)
                                .then(http("Add").post("/exams")
                                        .queryParam("studentId", "#{studentId}")
                                        .queryParam("courseId", "#{courseId}")
                                        .check(
                                                responseTimeInMillis().saveAs("timeout"),
                                                status().saveAs("status"))))
                .stopInjectorIf("Timeout reached", session -> session.getInt("timeout") > TIMEOUT)
                .pause(Duration.ofMillis(TIMEOUT))
                .randomSwitch().on(
                        percent(90.0)
                                .then(http("Contains").get("/exams")
                                        .queryParam("studentId", "#{studentId}")
                                        .queryParam("courseId", "#{courseId}")
                                        .check(
                                                responseTimeInMillis().saveAs("timeout"),
                                                status().saveAs("status"))))
                .stopInjectorIf("Timeout reached", session -> session.getInt("timeout") > TIMEOUT)
                .pause(Duration.ofMillis(TIMEOUT))
                .randomSwitch().on(
                        percent(1.0)
                                .then(http("Remove").delete("/exams")
                                        .queryParam("studentId", "#{studentId}")
                                        .queryParam("courseId", "#{courseId}")
                                        .check(
                                                responseTimeInMillis().saveAs("timeout"),
                                                status().saveAs("status"))))
                .pause(Duration.ofMillis(TIMEOUT))
                .stopInjectorIf("Timeout reached", session -> session.getInt("timeout") > TIMEOUT);
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

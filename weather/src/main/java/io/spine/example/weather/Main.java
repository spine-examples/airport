/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.example.weather;

import com.google.common.flogger.FluentLogger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.flogger.FluentLogger.forEnclosingClass;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static spark.Spark.defaultResponseTransformer;
import static spark.Spark.get;
import static spark.Spark.port;

final class Main {

    private static final FluentLogger log = forEnclosingClass();
    private static final ExecutorService meteoLabThread = newSingleThreadExecutor();
    private static final int PORT = 4242;

    /**
     * Prevents the utility class instantiation.
     */
    private Main() {
    }

    public static void main(String[] args) {
        MeasurementRepository repository = new MeasurementRepository();
        MeteoLab lab = new MeteoLab();

        startUpLab(repository, lab);
        setUpRequestHandler(repository);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void startUpLab(MeasurementRepository repository, MeteoLab lab) {
        meteoLabThread.execute(() -> {
            while (true) {
                sleepUninterruptibly(Duration.ofSeconds(4));
                Measurement newMeasurement = lab.measure();
                repository.store(newMeasurement);
            }
        });

    }

    private static void setUpRequestHandler(MeasurementRepository repository) {
        port(PORT);
        defaultResponseTransformer(Object::toString);
        get("/events", (request, response) -> {
            String sinceParam = request.queryParams("since");
            checkArgument(sinceParam != null && !sinceParam.isEmpty());
            long epochSeconds = Long.parseLong(sinceParam);
            Instant since = Instant.ofEpochSecond(epochSeconds);
            Instant upTo = Instant.now();
            Measurements measurements = repository.between(since, upTo);
            log.atSevere().log("Found `%d` measurements between %s and %s.",
                             measurements.measurements().size(), since, upTo);
            return measurements;
        });
    }
}

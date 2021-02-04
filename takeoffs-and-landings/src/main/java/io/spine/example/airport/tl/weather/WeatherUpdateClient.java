/*
 * Copyright 2021, TeamDev. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

package io.spine.example.airport.tl.weather;

import io.spine.example.airport.tl.PollingClient;
import io.spine.logging.Logging;
import io.spine.net.Url;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

/**
 * A client of the {@code Weather} monitoring system.
 *
 * <p>Pulls and publishes the weather updates into the system.
 */
public class WeatherUpdateClient implements PollingClient, Logging {

    private static final Duration REQUEST_FREQUENCY = Duration.ofSeconds(5);
    private final OkHttpClient client = new OkHttpClient();
    private final WeatherUpdateEndpoint endpoint = new WeatherUpdateEndpoint();
    private final Url weatherService;
    private Instant lastEventTime;
    private volatile boolean running = true;

    public WeatherUpdateClient(Instant lastEventTime, Url weatherService) {
        this.lastEventTime = checkNotNull(lastEventTime);
        this.weatherService = checkNotNull(weatherService);
    }

    public WeatherUpdateClient(Url weatherService) {
        this(Instant.now(), weatherService);
    }

    @Override
    public void start() {
        while (running) {
            fetchWeatherUpdates();
            sleepUninterruptibly(REQUEST_FREQUENCY);
        }
    }

    private void fetchWeatherUpdates() {
        Instant lastEvent = lastEventTime;
        lastEventTime = Instant.now();
        Request getEvents = new Request.Builder()
                .get()
                .url(weatherService.getSpec() + "/events?since=" + lastEvent.getEpochSecond())
                .build();
        try {
            ResponseBody responseBody = client.newCall(getEvents)
                                              .execute()
                                              .body();
            checkNotNull(responseBody);
            String responseJson = responseBody.string();
            WeatherMeasurement measurement = WeatherMeasurement.fromJson(responseJson);
            endpoint.receiveNew(measurement);
        } catch (IOException e) {
            logger().atSevere()
                    .withCause(e)
                    .log();
        }
    }

    @Override
    public void close() throws Exception {
        running = false;
        endpoint.close();
    }
}

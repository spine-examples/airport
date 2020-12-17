/*
 * Copyright 2020, TeamDev. All rights reserved.
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

package io.spine.example.weather;

import com.google.common.flogger.FluentLogger;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;

import static com.google.common.flogger.FluentLogger.forEnclosingClass;
import static java.time.Instant.now;

public final class MeteoLab {

    private static final FluentLogger log = forEnclosingClass();

    private static final int RANDOMNESS_BOUND = 40;
    private final Random rand = new SecureRandom();
    private Measurement lastMeasurement = new Measurement(0, 0, 0, Instant.ofEpochSecond(1));

    @SuppressWarnings("NumericCastThatLosesPrecision") // OK for randomly generated values.
    public Measurement measure() {
        double temperatureChange = rand.nextGaussian() * rand.nextInt(RANDOMNESS_BOUND);
        float newTemperature = (float) (lastMeasurement.temperature() + temperatureChange);

        double windSpeedChange = rand.nextGaussian() * rand.nextInt(RANDOMNESS_BOUND);
        float newWindSpeed = (float) (lastMeasurement.windSpeed() + windSpeedChange);

        double windDirectionChange = rand.nextGaussian() * rand.nextInt(RANDOMNESS_BOUND);
        float newWindDirection = (float) (lastMeasurement.windDirection() + windDirectionChange);

        lastMeasurement = new Measurement(newWindSpeed, newWindDirection, newTemperature, now());

        log.atFine().log("New measurement taken. New weather: `%s`.", lastMeasurement);

        return lastMeasurement;
    }
}

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

package io.spine.example.airport.tl.weather;

import io.spine.core.UserId;
import io.spine.example.airport.tl.Temperature;
import io.spine.example.airport.tl.TemperatureChanged;
import io.spine.example.airport.tl.WindSpeed;
import io.spine.example.airport.tl.WindSpeedChanged;
import io.spine.server.integration.ThirdPartyContext;

import static com.google.common.base.Preconditions.checkNotNull;

public final class WeatherUpdateEndpoint implements AutoCloseable {

    private static final UserId actor = UserId
            .newBuilder()
            .setValue("M-42")
            .build();
    private final ThirdPartyContext weatherContext = ThirdPartyContext.singleTenant("Weather");
    private WeatherMeasurement previous = WeatherMeasurement.unknown();

    public void receiveNew(WeatherMeasurement measurement) {
        checkNotNull(measurement);
        if (!previous.isUnknown()) {
            postTemperatureChanged(measurement);
            postWindChanged(measurement);
        }
        previous = measurement;
    }

    private void postWindChanged(WeatherMeasurement measurement) {
        WindSpeedChanged event = WindSpeedChanged
                .newBuilder()
                .setNewSpeed(windSpeed(measurement.windSpeed(), measurement.windDirection()))
                .setPreviousSpeed(windSpeed(previous.windSpeed(), previous.windDirection()))
                .vBuild();
        weatherContext.emittedEvent(event, actor);
    }

    private void postTemperatureChanged(WeatherMeasurement measurement) {
        TemperatureChanged event = TemperatureChanged
                .newBuilder()
                .setNewTemperature(temperature(measurement.temperature()))
                .setPreviousTemperature(temperature(previous.temperature()))
                .vBuild();
        weatherContext.emittedEvent(event, actor);
    }

    private static Temperature temperature(float value) {
        return Temperature
                .newBuilder()
                .setDegreesCelsius(value)
                .build();
    }

    private static WindSpeed windSpeed(float speed, float direction) {
        return WindSpeed
                .newBuilder()
                .setValue(speed)
                .setAzimuth(direction)
                .build();
    }

    @Override
    public void close() throws Exception {
        weatherContext.close();
    }
}

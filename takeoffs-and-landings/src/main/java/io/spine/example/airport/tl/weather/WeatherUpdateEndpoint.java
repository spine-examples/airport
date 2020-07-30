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
import io.spine.example.airport.tl.TemperatureChanged;
import io.spine.example.airport.tl.WindSpeedChanged;
import io.spine.server.integration.ThirdPartyContext;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An adapter which publishes weather updates as domain events.
 */
public final class WeatherUpdateEndpoint implements AutoCloseable {

    private static final UserId actor = UserId
            .newBuilder()
            .setValue("Meteolab M-42")
            .build();
    private final ThirdPartyContext weatherContext = ThirdPartyContext.singleTenant("Weather");
    private WeatherMeasurement previous = WeatherMeasurement.unknown();

    /**
     * Publishes the given measurement as weather update events.
     */
    public void receiveNew(WeatherMeasurement measurement) {
        checkNotNull(measurement);
        if (!previous.isUnknown()) {
            TemperatureChanged event = TemperatureChanged
                    .newBuilder()
                    .setNewTemperature(measurement.toTemperature())
                    .setPreviousTemperature(measurement.toTemperature())
                    .vBuild();
            weatherContext.emittedEvent(event, actor);
            WindSpeedChanged event1 = WindSpeedChanged
                    .newBuilder()
                    .setNewSpeed(measurement.toWindSpeed())
                    .setPreviousSpeed(previous.toWindSpeed())
                    .vBuild();
            weatherContext.emittedEvent(event1, actor);
        }
        previous = measurement;
    }

    @Override
    public void close() throws Exception {
        weatherContext.close();
    }
}

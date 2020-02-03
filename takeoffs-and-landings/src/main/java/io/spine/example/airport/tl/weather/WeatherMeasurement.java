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

import com.google.common.base.Objects;
import com.google.gson.Gson;
import io.spine.example.airport.tl.Temperature;
import io.spine.example.airport.tl.WindSpeed;

import static io.spine.util.Preconditions2.checkNotEmptyOrBlank;

public final class WeatherMeasurement {

    private static final Gson parser = new Gson();
    private static final WeatherMeasurement unknownWeather = new WeatherMeasurement(0, 0, 0);

    private final float windSpeed;
    private final float windDirection;
    private final float temperature;

    private WeatherMeasurement(float windSpeed, float windDirection, float temperature) {
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
    }

    public static WeatherMeasurement fromJson(String json) {
        checkNotEmptyOrBlank(json);
        WeatherMeasurement measurement = parser.fromJson(json, WeatherMeasurement.class);
        return measurement;
    }

    public static WeatherMeasurement unknown() {
        return unknownWeather;
    }

    public float windSpeed() {
        return windSpeed;
    }

    public float windDirection() {
        return windDirection;
    }

    public float temperature() {
        return temperature;
    }

    public boolean isUnknown() {
        return this == unknownWeather;
    }

    public WindSpeed toWindSpeed() {
        return WindSpeed
                .newBuilder()
                .setValue(windSpeed())
                .setAzimuth(windDirection())
                .build();
    }

    public Temperature toTemperature() {
        return Temperature
                .newBuilder()
                .setDegreesCelsius(temperature())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeatherMeasurement)) {
            return false;
        }
        WeatherMeasurement that = (WeatherMeasurement) o;
        return Double.compare(that.windSpeed, windSpeed) == 0 &&
                Double.compare(that.windDirection, windDirection) == 0 &&
                Double.compare(that.temperature, temperature) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(windSpeed, windDirection, temperature);
    }
}

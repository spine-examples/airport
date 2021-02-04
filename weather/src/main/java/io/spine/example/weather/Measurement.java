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

package io.spine.example.weather;

import com.google.common.base.Objects;
import com.google.gson.Gson;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Measurement implements Comparable<Measurement> {

    private static final Gson printer = new Gson();

    private final float windSpeed;
    private final float windDirection;
    private final float temperature;
    private final Instant whenMeasured;

    Measurement(float windSpeed, float windDirection, float temperature, Instant measured) {
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
        this.whenMeasured = checkNotNull(measured);
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

    public boolean isIn(Instant start, Instant end) {
        checkNotNull(start);
        checkNotNull(end);

        checkArgument(start.isBefore(end));

        return start.isBefore(whenMeasured) && end.isAfter(whenMeasured);
    }

    public String toJson() {
        return printer.toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public int compareTo(Measurement other) {
        checkNotNull(other);
        return whenMeasured.compareTo(other.whenMeasured);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Measurement)) {
            return false;
        }
        Measurement that = (Measurement) o;
        return Double.compare(that.windSpeed, windSpeed) == 0 &&
                Double.compare(that.windDirection, windDirection) == 0 &&
                Double.compare(that.temperature, temperature) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(windSpeed, windDirection, temperature);
    }
}

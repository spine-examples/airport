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

package io.spine.example.airport.supplies;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.kg;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Supplies of a single plane for a single flight.
 */
public final class PlaneSupplies {

    private final AirplaneId id;
    private final @kg long fuelMass;
    private final Instant whenFueled;
    private final Instant whenFrostChecked;
    private final @Nullable Instant whenPreFlightCheckComplete;

    private PlaneSupplies(Builder builder) {
        this.id = builder.id;
        this.fuelMass = builder.fuelMass;
        this.whenFueled = builder.whenFueled;
        this.whenFrostChecked = builder.whenFrostChecked;
        this.whenPreFlightCheckComplete = builder.whenPreFlightCheckComplete;
    }

    public AirplaneId id() {
        return id;
    }

    public long fuelMass() {
        return fuelMass;
    }

    public Instant whenFueled() {
        return whenFueled;
    }

    public Instant whenFrostChecked() {
        return whenFrostChecked;
    }

    public @Nullable Instant whenPreFlightCheckComplete() {
        return whenPreFlightCheckComplete;
    }

    /**
     * Creates a new instance of {@code Builder} for {@code PlaneSupplies} instances.
     *
     * @return new instance of {@code Builder}
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * A builder for the {@code PlaneSupplies} instances.
     */
    public static final class Builder {

        private AirplaneId id;
        private @kg long fuelMass;
        private Instant whenFueled;
        private Instant whenFrostChecked;
        private Instant whenPreFlightCheckComplete;
        private boolean preFlightCheckSuccessful;

        /**
         * Prevents direct instantiation.
         */
        private Builder() {
        }

        public Builder setId(AirplaneId id) {
            this.id = checkNotNull(id);
            return this;
        }

        public Builder setFuelMass(@kg long fuelMass) {
            this.fuelMass = fuelMass;
            return this;
        }

        public Builder setWhenFueled(Instant whenFueled) {
            this.whenFueled = checkNotNull(whenFueled);
            return this;
        }

        public Builder setWhenFrostChecked(Instant whenFrostChecked) {
            this.whenFrostChecked = checkNotNull(whenFrostChecked);
            return this;
        }

        public Builder setWhenPreFlightCheckComplete(Instant whenPreFlightCheckComplete) {
            this.whenPreFlightCheckComplete = checkNotNull(whenPreFlightCheckComplete);
            return this;
        }

        /**
         * Creates a new instance of {@code PlaneSupplies}.
         *
         * @return new instance of {@code PlaneSupplies}
         */
        public PlaneSupplies build() {
            return new PlaneSupplies(this);
        }
    }
}

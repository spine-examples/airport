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

package io.spine.example.airport.supplies;

import org.checkerframework.checker.units.qual.kg;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PlaneSupplies {

    private final AircraftId id;
    private final @kg long fuelMass;
    private final Instant whenFueled;
    private final Instant whenFrostChecked;
    private final Instant whePreFlightCheckStarted;
    private final Instant whePreFlightCheckComplete;
    private final boolean preFlightCheckSuccessful;

    private PlaneSupplies(Builder builder) {
        this.id = builder.id;
        this.fuelMass = builder.fuelMass;
        this.whenFueled = builder.whenFueled;
        this.whenFrostChecked = builder.whenFrostChecked;
        this.whePreFlightCheckStarted = builder.whePreFlightCheckStarted;
        this.whePreFlightCheckComplete = builder.whePreFlightCheckComplete;
        this.preFlightCheckSuccessful = builder.preFlightCheckSuccessful;
    }

    public AircraftId id() {
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

    public Instant whePreFlightCheckStarted() {
        return whePreFlightCheckStarted;
    }

    public Instant whePreFlightCheckComplete() {
        return whePreFlightCheckComplete;
    }

    public boolean preFlightCheckSuccessful() {
        return preFlightCheckSuccessful;
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

        private AircraftId id;
        private @kg long fuelMass;
        private Instant whenFueled;
        private Instant whenFrostChecked;
        private Instant whePreFlightCheckStarted;
        private Instant whePreFlightCheckComplete;
        private boolean preFlightCheckSuccessful;

        /**
         * Prevents direct instantiation.
         */
        private Builder() {
        }

        public Builder setId(AircraftId id) {
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

        public Builder setWhePreFlightCheckStarted(Instant whePreFlightCheckStarted) {
            this.whePreFlightCheckStarted = checkNotNull(whePreFlightCheckStarted);
            return this;
        }

        public Builder setWhePreFlightCheckComplete(Instant whePreFlightCheckComplete) {
            this.whePreFlightCheckComplete = checkNotNull(whePreFlightCheckComplete);
            return this;
        }

        public Builder setPreFlightCheckSuccessful(boolean preFlightCheckSuccessful) {
            this.preFlightCheckSuccessful = preFlightCheckSuccessful;
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

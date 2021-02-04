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

package io.spine.example.tsa;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Passenger {

    private final String id;
    private final @Nullable String fullName;
    private final @Nullable String passportNumber;
    private final @Nullable String nationality;
    private final @Nullable String passportCountry;
    private final @Nullable String flightNumber;
    private final Status status;
    private final Instant encounteredAt;

    private Passenger(Builder builder) {
        this.id = builder.id;
        this.fullName = builder.fullName;
        this.passportNumber = builder.passportNumber;
        this.nationality = builder.nationality;
        this.passportCountry = builder.passportCountry;
        this.flightNumber = builder.flightNumber;
        this.status = builder.status;
        this.encounteredAt = builder.encounteredAt;
    }

    public String id() {
        return id;
    }

    public @Nullable String fullName() {
        return fullName;
    }

    public @Nullable String passportNumber() {
        return passportNumber;
    }

    public @Nullable String nationality() {
        return nationality;
    }

    public @Nullable String passportCountry() {
        return passportCountry;
    }

    public @Nullable String flightNumber() {
        return flightNumber;
    }

    public Status status() {
        return status;
    }

    public Instant encounteredAt() {
        return encounteredAt;
    }

    private String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Passenger)) {
            return false;
        }
        Passenger passenger = (Passenger) o;
        return Objects.equal(id, passenger.id) &&
                Objects.equal(fullName, passenger.fullName) &&
                Objects.equal(passportNumber, passenger.passportNumber) &&
                Objects.equal(nationality, passenger.nationality) &&
                Objects.equal(passportCountry, passenger.passportCountry) &&
                Objects.equal(flightNumber, passenger.flightNumber) &&
                status == passenger.status &&
                Objects.equal(encounteredAt, passenger.encounteredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, fullName, passportNumber, nationality, passportCountry,
                                flightNumber, status, encounteredAt);
    }

    public enum Status {

        NOT_ATTEMPTED,
        PASSED,
        DENIED,
        DETAINED;

        public static Status random() {
            Status[] options = Status.values();
            Random rand = new SecureRandom();
            int index = rand.nextInt(options.length);
            return options[index];
        }
    }

    /**
     * A builder for the {@code Passenger} instances.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public enum Nationality {

        Altmer,
        Argonian,
        Bosmer,
        Breton,
        Dunmer,
        Imperial,
        Khajiit,
        Nord,
        Orsimer,
        Redguard;

        public static Nationality random() {
            Nationality[] options = Nationality.values();
            Random rand = new SecureRandom();
            int index = rand.nextInt(options.length);
            return options[index];
        }
    }

    /**
     * Creates a new instance of {@code Builder} for {@code Passenger} instances.
     *
     * @return new instance of {@code Builder}
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String id;
        private String fullName;
        private String passportNumber;
        private String nationality;
        private String passportCountry;
        private String flightNumber;
        private Status status;
        private Instant encounteredAt;

        public Builder setId(String id) {
            this.id = checkNotNull(id);
            return this;
        }

        public Builder setFullName(String fullName) {
            this.fullName = checkNotNull(fullName);
            return this;
        }

        public Builder setPassportNumber(String passportNumber) {
            this.passportNumber = checkNotNull(passportNumber);
            return this;
        }

        public Builder setNationality(String nationality) {
            this.nationality = checkNotNull(nationality);
            return this;
        }

        public Builder setPassportCountry(String passportCountry) {
            this.passportCountry = checkNotNull(passportCountry);
            return this;
        }

        public Builder setFlightNumber(String flightNumber) {
            this.flightNumber = checkNotNull(flightNumber);
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = checkNotNull(status);
            return this;
        }

        public Builder setEncounteredAt(Instant encounteredAt) {
            this.encounteredAt = checkNotNull(encounteredAt);
            return this;
        }

        /**
         * Prevents direct instantiation.
         */
        private Builder() {
        }

        /**
         * Creates a new instance of {@code Passenger}.
         *
         * @return new instance of {@code Passenger}
         */
        public Passenger build() {
            return new Passenger(this);
        }
    }
}

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

import com.google.common.collect.ImmutableList;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

final class SecurityGate {

    private static final Random rand = new SecureRandom();
    private static final ImmutableList<String> NAMES = ImmutableList.of(
            "Dasher",
            "Dancer",
            "Prancer",
            "Vixen",
            "Comet",
            "Cupid",
            "Dunder",
            "Blixem"
    );

    Passenger registerNext() {
        sleepUninterruptibly(randomTimeout());
        String name = NAMES.get(rand.nextInt(NAMES.size()));
        Passenger passenger = Passenger
                .newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFullName(name)
                .setPassportNumber(name + rand.nextInt())
                .setFlightNumber("42")
                .setNationality(Passenger.Nationality.random().name())
                .setPassportCountry(Passenger.Nationality.random().name())
                .setEncounteredAt(Instant.now())
                .setStatus(Passenger.Status.random())
                .build();
        return passenger;
    }

    @SuppressWarnings("MagicNumber")
    private static Duration randomTimeout() {
        return Duration.ofSeconds(rand.nextInt(60) + 10);
    }
}

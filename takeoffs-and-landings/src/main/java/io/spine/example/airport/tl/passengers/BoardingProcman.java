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

package io.spine.example.airport.tl.passengers;

import io.spine.core.External;
import io.spine.example.airport.security.BoardingComplete;
import io.spine.example.airport.security.PassengerBoarded;
import io.spine.example.airport.security.PassengerDeniedBoarding;
import io.spine.example.airport.tl.FlightId;
import io.spine.server.event.React;
import io.spine.server.model.Nothing;
import io.spine.server.procman.ProcessManager;
import io.spine.server.tuple.EitherOf2;

import static io.spine.server.tuple.EitherOf2.withA;
import static io.spine.server.tuple.EitherOf2.withB;

public class BoardingProcman extends ProcessManager<FlightId, Boarding, Boarding.Builder> {

    // #docfragment "BoardingProcman"
    @React
    EitherOf2<BoardingComplete, Nothing> on(@External PassengerBoarded event) {
        PassengerId passenger = event.getId();
        builder().addBoarded(passenger);
        return completeOrNothing();
    }

    @React
    EitherOf2<BoardingComplete, Nothing> on(@External PassengerDeniedBoarding event) {
        PassengerId passenger = event.getId();
        builder().addWillNotBeBoarded(passenger);
        return completeOrNothing();
    }
    // #enddocfragment "BoardingProcman"

    private EitherOf2<BoardingComplete, Nothing> completeOrNothing() {
        int headCount = builder().getBoardedCount();
        int passengerCount = headCount + builder().getWillNotBeBoardedCount();
        return passengerCount == state().getExpectedPassengers()
               ? withA(BoardingComplete
                               .newBuilder()
                               .setFlight(id())
                               .setHeadCount(passengerCount)
                               .vBuild())
               : withB(nothing());
    }
}

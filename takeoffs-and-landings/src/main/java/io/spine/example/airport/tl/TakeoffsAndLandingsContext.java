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

package io.spine.example.airport.tl;

import io.spine.example.airport.tl.passengers.BoardingProcman;
import io.spine.server.BoundedContext;
import io.spine.server.ServerEnvironment;
import io.spine.server.storage.memory.InMemoryStorageFactory;
import io.spine.server.transport.memory.SingleThreadInMemTransportFactory;

/**
 * A factory for the {@code Takeoffs and Landings} {@link BoundedContext} instance.
 */
final class TakeoffsAndLandingsContext {

    static final String CONTEXT_NAME = "Takeoffs and Landings";

    /**
     * Prevents the utility class instantiation.
     */
    private TakeoffsAndLandingsContext() {
    }

    /**
     * Assembles the {@code Takeoffs and Landings} context.
     */
    static BoundedContext build() {
        ServerEnvironment env = ServerEnvironment.instance();
        env.configureStorage(InMemoryStorageFactory.newInstance());
        env.configureTransport(SingleThreadInMemTransportFactory.newInstance());

        return BoundedContext
                .singleTenant(CONTEXT_NAME)
                .add(new FlightRepository())
                .add(new AircraftRepository())
                .add(BoardingProcman.class)
                .build();
    }
}

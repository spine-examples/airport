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

import io.spine.server.BoundedContext;
import io.spine.server.BoundedContextBuilder;
import io.spine.server.Server;

import java.io.IOException;

final class TakeoffsAndLandings {

    static final String CONTEXT_NAME = "Takeoffs and Landings";
    private static final int PORT = 8484;

    /**
     * Prevents the utility class instantiation.
     */
    private TakeoffsAndLandings() {
    }

    public static void main(String[] args) throws IOException {
        Server server = Server
                .atPort(PORT)
                .add(context())
                .build();
        server.start();
        server.awaitTermination();
    }

    private static BoundedContextBuilder context() {
        return BoundedContext
                .singleTenant(CONTEXT_NAME)
                .add(FlightAggregate.class);
    }
}

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

package io.spine.example.tsa;

import java.time.Instant;

import static java.lang.Long.parseLong;
import static java.time.Instant.ofEpochSecond;
import static spark.Spark.get;
import static spark.Spark.port;

final class Main {

    private static final int PORT = 8282;

    /**
     * Prevents the utility class instantiation.
     */
    private Main() {
    }

    public static void main(String[] args) {
        port(PORT);
        PassengerRepository repository = new PassengerRepository();
        get("/passenger", (request, response) -> {
            String sinceParam = request.queryParams("since");
            Instant since = ofEpochSecond(parseLong(sinceParam));

            String uptoParam = request.queryParams("upto");
            Instant upto = ofEpochSecond(parseLong(uptoParam));

            return repository.all(since, upto);
        });
    }
}

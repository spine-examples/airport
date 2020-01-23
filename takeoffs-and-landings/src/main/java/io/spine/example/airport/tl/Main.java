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

import io.spine.example.airport.tl.weather.WeatherUpdateClient;
import io.spine.net.Url;
import io.spine.server.BoundedContextBuilder;
import io.spine.server.Server;

import static java.util.concurrent.ForkJoinPool.commonPool;

final class Main {

    private static final int PORT = 8484;
    private static final Url WEATHER_SERVICE = Url
            .newBuilder()
            .setSpec("https://weather.example.spine.io")
            .build();

    /**
     * Prevents the utility class instantiation.
     */
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        BoundedContextBuilder context = TakeoffsAndLandings.buildContext();
        Server server = Server.atPort(PORT)
                              .add(context)
                              .build();
        server.start();
        WeatherUpdateClient weatherClient = new WeatherUpdateClient(WEATHER_SERVICE);
        commonPool().execute(weatherClient::start);
        server.awaitTermination();
        weatherClient.close();
    }
}
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

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.spine.example.airport.tl.passengers.PassengerClient;
import io.spine.example.airport.tl.supplies.SuppliesEventConsumer;
import io.spine.example.airport.tl.weather.WeatherUpdateClient;
import io.spine.net.Url;
import io.spine.server.BoundedContextBuilder;
import io.spine.server.Server;

import static java.util.concurrent.ForkJoinPool.commonPool;

final class Main {

    private static final int PORT = 8484;
    private static final Url WEATHER_SERVICE = Url
            .newBuilder()
            .setSpec("http://localhost:4242")
            .build();
    private static final Url SECURITY_SERVICE = Url
            .newBuilder()
            .setSpec("http://localhost:8282")
            .build();
    private static final int SUPPLIES_PORT = 4545;

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
        WeatherUpdateClient weatherClient = connectToWeather();
        SuppliesEventConsumer suppliesEventConsumer = connectToSupplies();
        PassengerClient passengerClient = connectToSecurity();

        server.awaitTermination();
        weatherClient.close();
        suppliesEventConsumer.close();
        passengerClient.close();
    }

    private static SuppliesEventConsumer connectToSupplies() {
        ManagedChannel suppliesChannel = NettyChannelBuilder
                .forAddress("localhost", SUPPLIES_PORT)
                .usePlaintext()
                .executor(commonPool())
                .build();
        SuppliesEventConsumer consumer = new SuppliesEventConsumer(suppliesChannel);
        consumer.subscribeToEvents();
        return consumer;
    }

    private static WeatherUpdateClient connectToWeather() {
        WeatherUpdateClient weatherClient = new WeatherUpdateClient(WEATHER_SERVICE);
        commonPool().execute(weatherClient::start);
        return weatherClient;
    }

    private static PassengerClient connectToSecurity() {
        PassengerClient passengerClient = new PassengerClient(SECURITY_SERVICE);
        commonPool().execute(passengerClient::start);
        return passengerClient;
    }
}

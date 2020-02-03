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
import io.spine.server.BoundedContext;
import io.spine.server.CommandService;
import io.spine.server.GrpcContainer;
import io.spine.server.QueryService;
import io.spine.server.SubscriptionService;

import static java.util.concurrent.ForkJoinPool.commonPool;

final class TakeoffsAndLandingsService {

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
    private TakeoffsAndLandingsService() {
    }

    public static void main(String[] args) throws Exception {
        BoundedContext context = TakeoffsAndLandingsContext.build();
        QueryService queryService = queryService(context);
        GrpcContainer container = GrpcContainer
                .atPort(PORT)
                .addService(queryService)
                .addService(commandService(context))
                .addService(subscriptionService(context))
                .build();
        container.start();
        WeatherUpdateClient weatherClient = connectToWeather();
        SuppliesEventConsumer suppliesEventConsumer = connectToSupplies();
        PassengerClient passengerClient = connectToSecurity();
        Tower tower = connectToTower(context, queryService);

        container.awaitTermination();
        weatherClient.close();
        suppliesEventConsumer.close();
        passengerClient.close();
        tower.close();
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
        start(weatherClient);
        return weatherClient;
    }

    private static PassengerClient connectToSecurity() {
        PassengerClient passengerClient = new PassengerClient(SECURITY_SERVICE);
        start(passengerClient);
        return passengerClient;
    }

    private static Tower connectToTower(BoundedContext context,
                                        QueryService queryService) {
        AirportCode code = AirportCode
                .newBuilder()
                .setCode("HRK")
                .build();
        Tower tower = new Tower(context.importBus(), queryService, code);
        start(tower);
        return tower;
    }

    private static void start(PollingClient passengerClient) {
        commonPool().execute(passengerClient::start);
    }

    private static SubscriptionService subscriptionService(BoundedContext context) {
        return SubscriptionService
                .newBuilder()
                .add(context)
                .build();
    }

    private static CommandService commandService(BoundedContext context) {
        return CommandService
                .newBuilder()
                .add(context)
                .build();
    }

    private static QueryService queryService(BoundedContext context) {
        return QueryService
                .newBuilder()
                .add(context)
                .build();
    }
}

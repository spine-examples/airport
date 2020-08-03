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

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import io.spine.example.airport.supplies.SuppliesEventProducerGrpc.SuppliesEventProducerImplBase;
import io.spine.protobuf.AnyPacker;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.protobuf.util.Timestamps.compare;
import static com.google.protobuf.util.Timestamps.fromMillis;
import static io.spine.example.airport.supplies.SuppliesEvents.matches;
import static java.time.Duration.ofMinutes;
import static java.util.Collections.synchronizedList;

/**
 * Produces events to be consumed by the {@code Takeoffs and Landings} Context.
 */
// #docfragment "SuppliesEventProducer"
public final class SuppliesEventProducer extends SuppliesEventProducerImplBase {
// #enddocfragment "SuppliesEventProducer"

    private static final Random rand = new SecureRandom();
    private static final List<SuppliesEvent> historicalEvents = synchronizedList(new ArrayList<>());

    // #docfragment "SuppliesEventProducer"
    @Override
    public void subscribe(Subscription request, StreamObserver<SuppliesEvent> responseObserver) {
        // #enddocfragment "SuppliesEventProducer"
        produceRandom();
        // #docfragment "SuppliesEventProducer"
        Timestamp timestamp = request.getStartingFrom();
        historicalEvents
                .stream()
                .filter(event -> compare(event.getWhenOccurred(), timestamp) >= 0)
                .filter(event -> matches(event, request.getEventType()))
                .map(event -> event.toBuilder()
                                   .setSubscription(request)
                                   .build())
                .onClose(responseObserver::onCompleted)
                .forEach(responseObserver::onNext);
    }
    // #enddocfragment "SuppliesEventProducer"

    private static void produceRandom() {
        AirplaneId id = AirplaneId.newId();
        Instant preFlightCheckComplete = randomTimeInPast();
        Instant defrostingComplete = randomTimeInPast();
        Instant fueled = randomTimeInPast();
        PlaneSupplies supplies = PlaneSupplies
                .newBuilder()
                .setId(id)
                .setWhenPreFlightCheckComplete(preFlightCheckComplete)
                .setWhenFrostChecked(defrostingComplete)
                .setWhenFueled(fueled)
                .setFuelMass(rand.nextInt(1000))
                .build();
        putFueled(supplies);
        putFrostingChecked(supplies);
        putPreFlightCheckComplete(supplies);
    }

    private static void putFueled(PlaneSupplies supplies) {
        PlaneId id = PlaneId
                .newBuilder()
                .setValue(supplies.id().uuid())
                .build();
        PlaneFueled event = PlaneFueled
                .newBuilder()
                .setPlaneId(id)
                .setFuelMass(supplies.fuelMass())
                .build();
        Timestamp when = fromMillis(supplies.whenFueled().toEpochMilli());
        SuppliesEvent suppliesEvent = SuppliesEvent
                .newBuilder()
                .setPayload(AnyPacker.pack(event))
                .setWhenOccurred(when)
                .build();
        historicalEvents.add(suppliesEvent);
    }

    private static void putFrostingChecked(PlaneSupplies supplies) {
        PlaneId id = PlaneId
                .newBuilder()
                .setValue(supplies.id().uuid())
                .build();
        AntiFrostingCheckComplete event = AntiFrostingCheckComplete
                .newBuilder()
                .setPlaneId(id)
                .build();
        Timestamp when = fromMillis(supplies.whenFrostChecked().toEpochMilli());
        SuppliesEvent suppliesEvent = SuppliesEvent
                .newBuilder()
                .setPayload(AnyPacker.pack(event))
                .setWhenOccurred(when)
                .build();
        historicalEvents.add(suppliesEvent);
    }

    private static void putPreFlightCheckComplete(PlaneSupplies supplies) {
        boolean checkSuccessful = supplies.whenPreFlightCheckComplete() != null;
        if (checkSuccessful) {
            PlaneId id = PlaneId
                    .newBuilder()
                    .setValue(supplies.id().uuid())
                    .build();
            PreflightCheckComplete event = PreflightCheckComplete
                    .newBuilder()
                    .setPlaneId(id)
                    .build();
            Timestamp when = fromMillis(supplies.whenPreFlightCheckComplete().toEpochMilli());
            SuppliesEvent suppliesEvent = SuppliesEvent
                    .newBuilder()
                    .setPayload(AnyPacker.pack(event))
                    .setWhenOccurred(when)
                    .build();
            historicalEvents.add(suppliesEvent);
        }
    }

    private static Instant randomTimeInPast() {
        return Instant.now().minus(ofMinutes(rand.nextInt(1000)));
    }
// #docfragment "SuppliesEventProducer"
}
// #enddocfragment "SuppliesEventProducer"

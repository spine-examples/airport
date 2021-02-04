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

package io.spine.example.airport.tl;

import io.grpc.stub.StreamObserver;
import io.spine.base.EventMessage;
import io.spine.base.Identifier;
import io.spine.client.ActorRequestFactory;
import io.spine.client.Query;
import io.spine.client.QueryResponse;
import io.spine.core.Event;
import io.spine.core.UserId;
import io.spine.server.QueryService;
import io.spine.server.aggregate.ImportBus;
import io.spine.server.event.EventFactory;
import io.spine.time.Now;
import io.spine.time.ZoneIds;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static io.spine.base.Time.currentTimeZone;
import static io.spine.grpc.StreamObservers.noOpObserver;
import static io.spine.util.Exceptions.illegalStateWithCauseOf;
import static java.time.Duration.ofMinutes;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toSet;

/**
 * The dispatchers' tower.
 *
 * <p>Imports events about flights in the air via the {@link ImportBus}.
 */
public final class Tower implements PollingClient {

    private final ImportBus importBus;
    private final QueryService queryService;
    private final ActorRequestFactory requests;
    private final EventFactory events;

    private volatile boolean active;

    public Tower(ImportBus importBus, QueryService queryService, AirportCode code) {
        this.importBus = checkNotNull(importBus);
        this.queryService = checkNotNull(queryService);
        checkNotNull(code);
        this.requests = ActorRequestFactory
                .newBuilder()
                .setActor(UserId.newBuilder().setValue(code.getCode()).build())
                .setZoneId(ZoneIds.of(currentTimeZone()))
                .build();
        this.events = EventFactory.forImport(requests.newActorContext(), Identifier.pack(code));
        this.active = true;
    }

    @Override
    public void start() {
        while (active) {
            sleepUninterruptibly(ofMinutes(1));
            Query query = requests.query().all(Flight.class);
            queryService.read(query, new StreamObserver<QueryResponse>() {
                @Override
                public void onNext(QueryResponse value) {
                    @SuppressWarnings("unchecked")
                    Collection<Flight> flights = (Collection<Flight>) value.states();
                    monitorFlights(flights);
                }

                @Override
                public void onError(Throwable t) {
                    throw illegalStateWithCauseOf(t);
                }

                @Override
                public void onCompleted() {
                }
            });
        }
    }

    private void monitorFlights(Collection<Flight> flights) {
        Map<Boolean, Set<Flight>> partition = flights
                .stream()
                .unordered()
                .filter(flight -> !flight.hasActualArrival())
                .collect(partitioningBy(Flight::hasActualDeparture, toSet()));
        Set<Flight> midAirFlights = partition.get(true);
        Set<Flight> seatedFlights = partition.get(false);
        if (!midAirFlights.isEmpty()) {
            Flight flight = getFirst(midAirFlights, Flight.getDefaultInstance());
            checkNotNull(flight);
            recordArrival(flight);
        }
        if (!seatedFlights.isEmpty()) {
            Flight flight = getFirst(seatedFlights, Flight.getDefaultInstance());
            checkNotNull(flight);
            recordDeparture(flight);
        }
    }

    private void recordDeparture(Flight flight) {
        FlightDeparted event = FlightDeparted
                .newBuilder()
                .setId(flight.getId())
                .setWhen(Now.get().asOffsetDateTime())
                .vBuild();
        importEvent(event);
    }

    private void recordArrival(Flight flight) {
        FlightArrived event = FlightArrived
                .newBuilder()
                .setId(flight.getId())
                .setWhen(Now.get().asOffsetDateTime())
                .vBuild();
        importEvent(event);
    }

    private void importEvent(EventMessage message) {
        Event event = events.createEvent(message, null);
        importBus.post(event, noOpObserver());
    }

    @Override
    public void close() {
        active = false;
    }
}

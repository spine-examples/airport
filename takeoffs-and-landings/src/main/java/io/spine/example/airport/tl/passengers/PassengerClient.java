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

package io.spine.example.airport.tl.passengers;

import com.google.gson.Gson;
import io.spine.core.UserId;
import io.spine.example.airport.security.PassengerBoarded;
import io.spine.example.airport.security.PassengerDeniedBoarding;
import io.spine.example.airport.tl.PollingClient;
import io.spine.example.airport.tl.FlightId;
import io.spine.logging.Logging;
import io.spine.net.Url;
import io.spine.server.integration.ThirdPartyContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static io.spine.example.airport.tl.passengers.BoardingStatus.BOARDED;
import static io.spine.example.airport.tl.passengers.BoardingStatus.WILL_NOT_BE_BOARDED;
import static io.spine.server.integration.ThirdPartyContext.singleTenant;
import static java.lang.String.format;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofSeconds;

/**
 * A client of the {@code Security Checks} system.
 *
 * <p>Pulls the data about passengers who have passed the security check.
 */
public final class PassengerClient implements PollingClient, Logging {

    private static final Duration HALF_A_MINUTE = ofSeconds(30);
    private static final UserId ACTOR = UserId
            .newBuilder()
            .setValue("TSA")
            .build();

    private final Url securityService;
    private final OkHttpClient client;
    private final Gson parser;
    private final ThirdPartyContext securityContext;
    private volatile boolean active;

    public PassengerClient(Url service) {
        this.securityService = checkNotNull(service);
        this.securityContext = singleTenant("Security");
        this.client = new OkHttpClient();
        this.parser = new Gson();
        this.active = true;
    }

    @Override
    public void start() {
        while (active) {
            Instant now = Instant.now();
            Instant anHourAgo = now.minus(ofHours(1));
            String url = format("%s/passenger?since=%s&upto=%s",
                                securityService.getSpec(),
                                anHourAgo.getEpochSecond(),
                                now.getEpochSecond());
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            try {
                List<TsaPassenger> passengers = fetchPassengers(request);
                passengers.forEach(this::emitIfStatusKnown);
            } catch (IOException e) {
                _warn().withCause(e)
                       .log();
            }
            sleepUninterruptibly(HALF_A_MINUTE);
        }
    }

    private void emitIfStatusKnown(TsaPassenger tsaPassenger) {
        BoardingStatus status = tsaPassenger.boardingStatus();
        if (status == BOARDED) {
            emitBoarded(tsaPassenger);
        } else if (status == WILL_NOT_BE_BOARDED) {
            emitDenied(tsaPassenger);
        }
    }

    private void emitDenied(TsaPassenger tsaPassenger) {
        PassengerId id = PassengerId
                .newBuilder()
                .setValue(tsaPassenger.getId())
                .build();
        FlightId flight = FlightId
                .newBuilder()
                .setUuid(tsaPassenger.getFlightNumber())
                .build();
        PassengerDeniedBoarding event = PassengerDeniedBoarding
                .newBuilder()
                .setId(id)
                .setFlight(flight)
                .vBuild();
        securityContext.emittedEvent(event, ACTOR);
    }

    private void emitBoarded(TsaPassenger tsaPassenger) {
        PassengerId id = PassengerId
                .newBuilder()
                .setValue(tsaPassenger.getId())
                .build();
        FlightId flight = FlightId
                .newBuilder()
                .setUuid(tsaPassenger.getFlightNumber())
                .build();
        PassengerBoarded event = PassengerBoarded
                .newBuilder()
                .setId(id)
                .setFlight(flight)
                .vBuild();
        securityContext.emittedEvent(event, ACTOR);
    }

    private List<TsaPassenger> fetchPassengers(Request request) throws IOException {
        ResponseBody body = client.newCall(request)
                                  .execute()
                                  .body();
        checkNotNull(body);
        String jsonResponse = body.string();
        TsaPassengers passengers = parser.fromJson(jsonResponse, TsaPassengers.class);
        return passengers.getPassengerList();
    }

    @Override
    public void close() throws Exception {
        active = false;
        securityContext.close();
    }
}

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
import io.spine.example.airport.tl.ApiClient;
import io.spine.example.airport.tl.FlightId;
import io.spine.logging.Logging;
import io.spine.net.Url;
import io.spine.server.integration.ThirdPartyContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.example.airport.tl.passengers.BoardingStatus.BOARDED;
import static io.spine.example.airport.tl.passengers.BoardingStatus.WILL_NOT_BE_BOARDED;
import static io.spine.server.integration.ThirdPartyContext.singleTenant;
import static java.lang.String.format;

public final class PassengerClient implements ApiClient, Logging {

    private static final UserId ACTOR = UserId
            .newBuilder()
            .setValue("TSA")
            .build();

    private final Url securityService;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson parser = new Gson();
    private final ThirdPartyContext securityContext;

    public PassengerClient(Url service) {
        this.securityService = checkNotNull(service);
        this.securityContext = singleTenant("Security");
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void start() {
        while (true) {
            Request request = new Request.Builder()
                    .get()
                    .url(format("%s?since=%s&upto=%s", securityService.getSpec(), "", ""))
                    .build();
            try {
                List<TsaPassenger> passengers = fetchPassengers(request);
                passengers.forEach(this::emitIfStatusKnown);
            } catch (IOException e) {
                _severe().withCause(e).log();
            }
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
        String jsonResponse = body
                .string();
        return parser.fromJson(jsonResponse, TsaPassengers.class)
                                              .getPassengerList();
    }

    @Override
    public void close() throws Exception {
        securityContext.close();
    }
}

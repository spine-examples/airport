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
import io.spine.example.airport.security.TsaPassengers;
import io.spine.example.airport.tl.ApiClient;
import io.spine.logging.Logging;
import io.spine.net.Url;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public final class PassengerClient implements ApiClient, Logging {

    private final Url securityService;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson parser = new Gson();

    public PassengerClient(Url service) {
        this.securityService = checkNotNull(service);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void start() {
        while (true) {
            Request request = new Request.Builder()
                    .get()
                    .url(format("%s?since=%s&upto=%s", securityService.getSpec(), "", ""))
                    .build();
            ResponseBody body;
            try {
                body = client.newCall(request)
                             .execute()
                             .body();
                checkNotNull(body);
                String jsonResponse = body
                        .string();
                parser.fromJson(jsonResponse, TsaPassengers.class);
            } catch (IOException e) {
                _severe().withCause(e).log();
            }
        }
    }

    @Override
    public void close() throws Exception {

    }
}

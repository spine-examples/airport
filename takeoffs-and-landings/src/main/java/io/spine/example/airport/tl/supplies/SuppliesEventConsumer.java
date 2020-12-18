/*
 * Copyright 2020, TeamDev. All rights reserved.
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

package io.spine.example.airport.tl.supplies;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import io.spine.base.EventMessage;
import io.spine.core.ActorContext;
import io.spine.core.UserId;
import io.spine.example.airport.supplies.Subscription;
import io.spine.example.airport.supplies.SuppliesEvent;
import io.spine.example.airport.supplies.SuppliesEventProducerGrpc;
import io.spine.example.airport.supplies.SuppliesEventProducerGrpc.SuppliesEventProducerStub;
import io.spine.logging.Logging;
import io.spine.server.integration.ThirdPartyContext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.protobuf.AnyPacker.unpack;
import static java.util.concurrent.ForkJoinPool.commonPool;

/**
 * A client of the {@code Airplane Supplies} context.
 *
 * <p>Receives the events published by the {@code Airplane Supplies} and broadcasts them into
 * the {@code Takeoffs and Landings} context.
 */
public final class SuppliesEventConsumer implements AutoCloseable {

    private static final String CONTEXT_NAME = "Airplane Supplies";
    private static final UserId ACTOR = UserId
            .newBuilder()
            .setValue(CONTEXT_NAME)
            .build();

    private final SuppliesEventProducerStub producer;
    private final ThirdPartyContext context;

    public SuppliesEventConsumer(Channel channel) {
        checkNotNull(channel);
        this.producer = SuppliesEventProducerGrpc.newStub(channel)
                                                 .withExecutor(commonPool());
        this.context = ThirdPartyContext.singleTenant(CONTEXT_NAME);
    }

    public void subscribeToEvents() {
        Subscription subscription = Subscription
                .newBuilder()
                .build();
        producer.subscribe(subscription, new Observer(subscription));
    }

    @Override
    public void close() throws Exception {
        context.close();
    }

    /**
     * Observer of a {@link SuppliesEvent}s stream.
     *
     * <p>When the stream ends either via being completed or with an error, restarts
     * the subscription.
     */
    private final class Observer implements StreamObserver<SuppliesEvent>, Logging {

        private final Subscription subscription;

        private Observer(Subscription subscription) {
            this.subscription = checkNotNull(subscription);
        }

        // #docfragment "onNext"
        @Override
        public void onNext(SuppliesEvent event) {
            Subscription eventSubscription = event.getSubscription();
            checkArgument(subscription.equals(eventSubscription));

            _fine().log("Received event `%s`.", eventSubscription.getEventType());

            ActorContext actorContext = ActorContext
                    .newBuilder()
                    .setActor(ACTOR)
                    .setTimestamp(event.getWhenOccurred())
                    .vBuild();
            EventMessage eventMessage = (EventMessage) unpack(event.getPayload());
            context.emittedEvent(eventMessage, actorContext);
        }
        // #enddocfragment "onNext"

        @Override
        public void onError(Throwable t) {
            _severe().withCause(t).log();
            restart();
        }

        @Override
        public void onCompleted() {
            _fine().log("Subscription completed.");
            restart();
        }

        private void restart() {
            producer.subscribe(subscription, this);
        }
    }
}

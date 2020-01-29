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

import com.google.protobuf.Timestamp;
import io.spine.core.EventContext;
import io.spine.example.airport.security.BoardingComplete;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;
import io.spine.server.event.React;
import io.spine.server.model.Nothing;
import io.spine.server.tuple.EitherOf2;
import io.spine.time.LocalDateTime;
import io.spine.time.LocalDateTimes;
import io.spine.time.OffsetDateTime;
import io.spine.time.OffsetDateTimes;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static io.spine.server.tuple.EitherOf2.withA;
import static io.spine.server.tuple.EitherOf2.withB;
import static io.spine.time.OffsetDateTimes.toJavaTime;
import static java.lang.Math.abs;

final class FlightAggregate extends Aggregate<FlightId, Flight, Flight.Builder> {

    private static final int WIND_DIRECTION_CHANGE_THRESHOLD = 30;
    private static final int TEMPERATURE_CHANGE_THRESHOLD = 30;
    private static final int WIND_SPEED_THRESHOLD = 150;
    private static final Duration QUARTER_OF_AN_HOUR = Duration.ofMinutes(15);
    private static final Duration HALF_AN_HOUR = Duration.ofMinutes(30);

    @Assign
    FlightScheduled handle(ScheduleFlight command) {
        return FlightScheduled
                .newBuilder()
                .setId(command.getId())
                .setFrom(command.getFrom())
                .setTo(command.getTo())
                .setScheduledDeparture(command.getScheduledDeparture())
                .setScheduledArrival(command.getScheduledArrival())
                .vBuild();
    }

    @Assign
    FlightRescheduled handle(RescheduleFlight command) {
        return FlightRescheduled
                .newBuilder()
                .setId(command.getId())
                .setScheduledDeparture(command.getScheduledDeparture())
                .setScheduledArrival(command.getScheduledArrival())
                .vBuild();
    }

    @React(external = true)
    EitherOf2<FlightRescheduled, Nothing> on(WindSpeedChanged event) {
        double newDirection = event.getNewSpeed().getAzimuth();
        double previousDirection = event.getPreviousSpeed().getAzimuth();
        if (abs(previousDirection - newDirection) > WIND_DIRECTION_CHANGE_THRESHOLD) {
            return withA(postpone(QUARTER_OF_AN_HOUR));
        }
        double windSpeed = event.getNewSpeed().getValue();
        if (windSpeed > WIND_SPEED_THRESHOLD) {
            return withA(postpone(HALF_AN_HOUR));
        }
        return withB(nothing());
    }

    @React(external = true)
    EitherOf2<FlightRescheduled, Nothing> on(TemperatureChanged event) {
        float newTemperature = event.getNewTemperature().getDegreesCelsius();
        float previousTemperature = event.getPreviousTemperature().getDegreesCelsius();
        if (abs(previousTemperature - newTemperature) > TEMPERATURE_CHANGE_THRESHOLD) {
            return withA(postpone(QUARTER_OF_AN_HOUR));
        } else {
            return withB(nothing());
        }
    }

    @React
    FlightBoarded on(BoardingComplete event, EventContext context) {
        return FlightBoarded
                .newBuilder()
                .setId(event.getFlight())
                .setWhenBoarded(toLocalDateTime(context.getTimestamp()))
                .vBuild();
    }

    @Apply
    private void on(FlightScheduled event) {
        builder().setFrom(event.getFrom())
                 .setTo(event.getTo())
                 .setScheduledDeparture(event.getScheduledDeparture())
                 .setScheduledArrival(event.getScheduledArrival());
    }

    @Apply
    private void on(FlightRescheduled event) {
        builder().setScheduledDeparture(event.getScheduledDeparture())
                 .setScheduledArrival(event.getScheduledArrival());
    }

    @Apply
    private void on(FlightBoarded event) {
        builder().setWhenBoarded(event.getWhenBoarded());
    }

    private FlightRescheduled postpone(Duration forHowLong) {
        OffsetDateTime scheduledDeparture = state().getScheduledDeparture();
        OffsetDateTime newDeparture = OffsetDateTimes.of(
                toJavaTime(scheduledDeparture).plus(forHowLong)
        );

        OffsetDateTime scheduledArrival = state().getScheduledArrival();
        OffsetDateTime newArrival = OffsetDateTimes.of(
                toJavaTime(scheduledArrival).plus(forHowLong)
        );

        return FlightRescheduled
                .newBuilder()
                .setId(id())
                .setScheduledDeparture(newDeparture)
                .setScheduledArrival(newArrival)
                .vBuild();
    }

    private static LocalDateTime toLocalDateTime(Timestamp when) {
        java.time.LocalDateTime time = java.time.LocalDateTime.ofInstant(
                Instant.ofEpochSecond(when.getSeconds(), when.getNanos()),
                ZoneId.systemDefault()
        );
        return LocalDateTimes.of(time);
    }
}

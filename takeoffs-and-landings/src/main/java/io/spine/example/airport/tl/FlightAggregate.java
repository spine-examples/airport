package io.spine.example.airport.tl;

import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;

class FlightAggregate extends Aggregate<FlightId, Flight, Flight.Builder> {

    @Assign
    FlightScheduled handle(ScheduleFlight command) {
        return FlightScheduled
                .newBuilder()
                .setId(command.getId())
                .setAircraft(command.getAircraft())
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

    @Apply
    private void on(FlightScheduled event) {
        builder().setAircraft(event.getAircraft())
                 .setFrom(event.getFrom())
                 .setTo(event.getTo())
                 .setScheduledDeparture(event.getScheduledDeparture())
                 .setScheduledArrival(event.getScheduledArrival());
    }

    @Apply
    private void on(FlightRescheduled event) {
        builder().setScheduledDeparture(event.getScheduledDeparture())
                 .setScheduledArrival(event.getScheduledArrival());
    }
}

package io.spine.example.airport.airtraffic;

import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;

final class AircraftAggregate extends Aggregate<AircraftId, Aircraft, Aircraft.Builder> {

    @Assign
    AircraftRegistered handle(RegisterAircraft command) {
        return AircraftRegistered
                .newBuilder()
                .setId(command.getId())
                .vBuild();
    }

    @Apply
    private void on(AircraftRegistered event) {
        builder().setId(event.getId());
    }
}

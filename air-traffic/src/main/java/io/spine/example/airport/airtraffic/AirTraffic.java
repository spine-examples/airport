package io.spine.example.airport.airtraffic;

import com.google.common.annotations.VisibleForTesting;
import io.spine.server.BoundedContext;
import io.spine.server.BoundedContextBuilder;
import io.spine.server.Server;

import java.io.IOException;

import static io.spine.util.Exceptions.illegalStateWithCauseOf;

final class AirTraffic {

    static final String CONTEXT_NAME = "Air Traffic";

    /**
     * Prevents the utility class instantiation.
     */
    private AirTraffic() {
    }

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        Server server = Server.atPort(4242)
                .add(context())
                .build();
        try {
            server.start();
        } catch (IOException e) {
            throw illegalStateWithCauseOf(e);
        }
        server.awaitTermination();
    }

    @VisibleForTesting
    static BoundedContextBuilder context() {
        return BoundedContext
                .singleTenant(CONTEXT_NAME)
                .add(AircraftAggregate.class);
    }
}

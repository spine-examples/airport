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

package io.spine.example.airport.supplies;

import io.spine.type.TypeUrl;

import static com.google.common.base.Preconditions.checkNotNull;

final class SuppliesEvents {

    private static final TypeUrl PLANE_FUELED_TYPE =
            TypeUrl.of(PlaneFueled.class);
    private static final TypeUrl ANTI_FROSTING_CHECK_COMPLETE_TYPE =
            TypeUrl.of(AntiFrostingCheckComplete.class);
    private static final TypeUrl PREFLIGHT_CHECK_COMPLETE_TYPE =
            TypeUrl.of(PreflightCheckComplete.class);

    /**
     * Prevents the utility class instantiation.
     */
    private SuppliesEvents() {}

    static boolean matches(SuppliesEvent event, EventType type) {
        checkNotNull(event);
        checkNotNull(type);

        TypeUrl url = TypeUrl.ofEnclosed(event.getPayload());
        switch (type) {
            case PLANE_FUELED:
                return url.equals(PLANE_FUELED_TYPE);
            case ANTI_FROSTING_CHECK_COMPLETE:
                return url.equals(ANTI_FROSTING_CHECK_COMPLETE_TYPE);
            case PREFLIGHT_CHECK_COMPLETE:
                return url.equals(PREFLIGHT_CHECK_COMPLETE_TYPE);
            case UNRECOGNIZED:
                return false;
            case ALL:
            default:
                return true;
        }
    }
}

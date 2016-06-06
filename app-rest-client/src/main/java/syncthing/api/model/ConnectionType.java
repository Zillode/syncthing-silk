/*
 * Copyright (c) 2015 OpenSilk Productions LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package syncthing.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by drew on 11/6/15.
 */
public enum ConnectionType {
    @SerializedName("TCP (Server)") TCP_SERVER,
    @SerializedName("TCP (Client)") TCP_CLIENT,
    @SerializedName("Relay (Server)") RELAY_SERVER,
    @SerializedName("Relay (Client)") RELAY_CLIENT,
    UNKNOWN
}

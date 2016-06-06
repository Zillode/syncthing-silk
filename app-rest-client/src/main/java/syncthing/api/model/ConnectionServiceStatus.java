/*
 * Copyright (c) 2016 OpenSilk Productions LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package syncthing.api.model;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Map;

import syncthing.api.ApiUtils;

/**
 * Created by Zillode on 6/6/16.
 */
public class ConnectionServiceStatus {
    public String error;
    public String[] lanAddresses;
    public String[] wanAddresses;

    @Override
    public String toString() {
        return ApiUtils.reflectionToString(this);
    }
}

/*
 * Copyright (c) 2015 OpenSilk Productions LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package syncthing.api.model.event;

import org.joda.time.DateTime;

import syncthing.api.model.Model;

/**
 * Created by drew on 10/11/15.
 */
public class FolderSummary extends Event<FolderSummary.Data> {
    public FolderSummary(long id, DateTime time, EventType type, Data data) {
        super(id, time, type, data);
    }

    public static class Data {
        public String folder;
        public Model summary;

        public Data() {
        }

        public Data(String folder, Model summary) {
            this.folder = folder;
            this.summary = summary;
        }
    }
}

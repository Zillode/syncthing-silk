/*
 * Copyright (c) 2015 OpenSilk Productions LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package syncthing.api.model;

import java.io.Serializable;

/**
 * Created by drew on 11/10/15.
 */
public class VersioningSimple extends Versioning<VersioningSimple.Params> {
    private static final long serialVersionUID = -664223108287443392L;

    public VersioningSimple(VersioningType type, Params params) {
        super(type, params);
    }

    public static class Params implements Serializable, Cloneable {
        private static final long serialVersionUID = -569749202964804986L;
        public String keep = "5";
        @Override
        protected Params clone() throws CloneNotSupportedException {
            return (Params) super.clone();
        }
    }

    @Override
    public VersioningSimple clone() {
        try {
            VersioningSimple n = (VersioningSimple) super.clone();
            n.params = params.clone();
            return n;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

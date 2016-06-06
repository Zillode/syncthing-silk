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
import java.util.Arrays;

/**
 * Created by drew on 3/1/15.
 */
public class OptionsConfig implements Serializable, Cloneable {
    private static final long serialVersionUID = -6584204552575433781L;
    public String[] listenAddress;
    public String[] globalAnnounceServers;
    public boolean globalAnnounceEnabled;
    public boolean localAnnounceEnabled;
    public int localAnnouncePort;
    public String localAnnounceMCAddr;
    public int maxSendKbps;
    public int maxRecvKbps;
    public int reconnectionIntervalS;
    public boolean relaysEnabled;
    public int relayReconnectIntervalM;
    public boolean startBrowser;
    public boolean natEnabled;
    public int natLeaseMinutes;
    public int natRenewalMinutes;
    public int natTimeoutSeconds;
    public int urAccepted;
    public String urUniqueId;
    public boolean urPostInsecurely;
    public int urInitialDelayS;
    public String urURL;
    public boolean restartOnWakeup;
    public int autoUpgradeIntervalH;
    public int keepTemporariesH;
    public boolean cacheIgnoredFiles;
    public int progressUpdateIntervalS;
    public boolean symlinksEnabled;
    public boolean limitBandwidthInLan;
    public String releasesURL;
    public String[] alwaysLocalNets;
    public int minHomeDiskFreePct;
    public boolean overwriteRemoteDeviceNamesOnConnect;
    public int tempIndexMinBlocks;

    public static OptionsConfig withDefaults() {
        OptionsConfig o = new OptionsConfig();
        o.listenAddress = new String[]{"default"};
        o.globalAnnounceServers = new String[]{"default"};
        o.globalAnnounceEnabled = true;
        o.localAnnounceEnabled = true;
        o.localAnnouncePort = 21027;
        o.localAnnounceMCAddr = "[ff12::8384]:21027";
        o.maxSendKbps = 0;
        o.maxRecvKbps = 0;
        o.reconnectionIntervalS = 60;
        o.relaysEnabled = true;
        o.relayReconnectIntervalM = 10;
        o.startBrowser = false;
        o.natEnabled = true;
        o.natLeaseMinutes = 60;
        o.natRenewalMinutes = 30;
        o.natTimeoutSeconds = 10;
        o.urAccepted = -1; //0 off, -1 permanent
        o.urPostInsecurely = false;
        o.urInitialDelayS = 1800;
        o.urURL = "https://data.syncthing.net/newdata";
        o.restartOnWakeup = true;
        o.autoUpgradeIntervalH = 12;
        o.keepTemporariesH = 24;
        o.cacheIgnoredFiles = true;
        o.progressUpdateIntervalS = 5;
        o.symlinksEnabled = true;
        o.limitBandwidthInLan = false;
        o.releasesURL = "https://api.github.com/repos/syncthing/syncthing/releases?per_page=30";
        o.alwaysLocalNets = new String[]{};
        o.minHomeDiskFreePct = 1;
        o.overwriteRemoteDeviceNamesOnConnect = false;
        o.tempIndexMinBlocks = 10;
        return o;
    }

    @Override
    public OptionsConfig clone() {
        try {
            OptionsConfig n = (OptionsConfig) super.clone();
            if (listenAddress != null && listenAddress.length > 0) {
                n.listenAddress = Arrays.copyOf(listenAddress, listenAddress.length);
            }
            if (globalAnnounceServers != null && globalAnnounceServers.length > 0) {
                n.globalAnnounceServers = Arrays.copyOf(globalAnnounceServers, globalAnnounceServers.length);
            }
            if (alwaysLocalNets != null && alwaysLocalNets.length > 0) {
                n.alwaysLocalNets = Arrays.copyOf(alwaysLocalNets, alwaysLocalNets.length);
            }
            return n;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

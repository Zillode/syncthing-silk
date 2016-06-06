/*
 * Copyright (c) 2015 OpenSilk Productions LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package syncthing.android;

import android.text.TextUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensilk.common.core.app.BaseApp;
import org.opensilk.common.core.mortar.DaggerService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import syncthing.android.service.ServiceComponent;
import syncthing.android.service.ServiceSettings;
import timber.log.Timber;

/**
 * Created by drew on 3/4/15.
 */
public class App extends BaseApp {

    private String mProcess;

    @Override
    public void onCreate() {
        Timber.d("onCreate()");
        super.onCreate();
        setupTimber(true, null);
        enableStrictMode();
    }

    @Override
    protected Object getRootComponent() {
        if (isServiceProcess()) {
            return ServiceComponent.FACTORY.call(this);
        } else if (isEmulator()) {
            return EmulatorComponent.FACTORY.call(this);
        } else {
            setupReporting();
            return AppComponent.FACTORY.call(this);
        }
    }

    protected void setupReporting() {
    }

    boolean isEmulator() {
        readProcess();
        return StringUtils.isEmpty(mProcess);
    }

    boolean isServiceProcess() {
        readProcess();
        return StringUtils.endsWith(mProcess, ":service");
    }

    void readProcess() {
        if (mProcess == null) {
            try {
                final File comm = new File("/proc/self/comm");
                if (comm.exists() && comm.canRead()) {
                    final List<String> commLines = FileUtils.readLines(comm);
                    if (commLines.size() > 0) {
                        final String procName = commLines.get(0).trim();
                        Timber.i("%s >> %s ", comm.getAbsolutePath(), procName);
                        mProcess = procName;
                    }
                }
            } catch (IOException ignored) {
                mProcess = "";
            }
        }
    }
}

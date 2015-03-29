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

package syncthing.android.service;

import android.app.AlarmManager;
import android.app.NotificationManager;

import javax.inject.Singleton;

import dagger.Component;
import syncthing.android.AppComponent;
import syncthing.android.AppModule;
import syncthing.api.GsonModule;
import syncthing.api.RetrofitModule;
import syncthing.api.SyncthingApi;
import syncthing.api.SyncthingApiModule;

/**
 * Created by drew on 3/21/15.
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                GsonModule.class,
                RetrofitModule.class,
        }
)
public interface ServiceComponent {
    NotificationManager notificationManager();
    AlarmManager alarmManager();
    ServiceSettings settings();
}
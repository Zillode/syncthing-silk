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

import android.content.Context;

import org.opensilk.common.core.dagger2.AppContextComponent;
import org.opensilk.common.core.dagger2.AppContextModule;
import org.opensilk.common.core.dagger2.SystemServicesComponent;

import javax.inject.Singleton;

import dagger.Component;
import rx.functions.Func1;
import syncthing.android.identicon.IdenticonComponent;
import syncthing.android.service.ServiceSettings;
import syncthing.android.settings.AppSettings;
import syncthing.api.SessionManagerComponent;

/**
 * Created by drew on 3/4/15.
 */
@Singleton
@Component (
        modules = {
                AppContextModule.class,
                AppModule.class
        }
)
public interface AppComponent extends SessionManagerComponent, IdenticonComponent, AppContextComponent, SystemServicesComponent {
    Func1<Context, AppComponent> FACTORY = new Func1<Context, AppComponent>() {
        @Override
        public AppComponent call(Context context) {
            return DaggerAppComponent.builder()
                    .appContextModule(new AppContextModule(context))
                    .build();
        }
    };
    String NAME = AppComponent.class.getName();
    AppSettings appSettings();
    ServiceSettings serviceSettings();
}

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

package syncthing.android.ui.login;

import org.opensilk.common.core.dagger2.ScreenScope;

import dagger.Component;
import rx.functions.Func1;
import syncthing.android.ui.ManageActivityComponent;

/**
 * Created by drew on 3/15/15.
 */
@ScreenScope
@Component(
        dependencies = ManageActivityComponent.class
)
public interface ManageComponent {
    Func1<ManageActivityComponent, ManageComponent> FACTORY =
            new Func1<ManageActivityComponent, ManageComponent>() {
                @Override
                public ManageComponent call(ManageActivityComponent loginActivityComponent) {
                    return DaggerManageComponent.builder()
                            .manageActivityComponent(loginActivityComponent)
                            .build();
                }
            };
    ManagePresenter presenter();
    void inject(ManageScreenView view);
}

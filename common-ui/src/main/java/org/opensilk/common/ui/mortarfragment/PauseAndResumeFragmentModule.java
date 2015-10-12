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

package org.opensilk.common.ui.mortarfragment;

import org.opensilk.common.core.dagger2.ScreenScope;
import org.opensilk.common.ui.mortar.PauseAndResumePresenter;
import org.opensilk.common.ui.mortar.PauseAndResumeRegistrar;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by drew on 9/28/15.
 */
@Module
public class PauseAndResumeFragmentModule {
    @Provides @ScreenScope @Named("fragment")
    PauseAndResumePresenter providePauseAndResumePresenter() { return new PauseAndResumePresenter(); }
    @Provides @ScreenScope @Named("fragment")
    PauseAndResumeRegistrar providePauseAndResumeRegistar(PauseAndResumePresenter presenter) { return presenter; }
}
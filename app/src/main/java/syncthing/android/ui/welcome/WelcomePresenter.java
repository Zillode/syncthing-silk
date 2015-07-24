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

package syncthing.android.ui.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.opensilk.common.core.dagger2.ForApplication;
import org.opensilk.common.ui.mortar.ActivityResultsController;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import mortar.ViewPresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import syncthing.android.AppSettings;
import syncthing.android.model.Credentials;
import syncthing.android.service.PRNGFixes;
import syncthing.android.service.SyncthingUtils;
import syncthing.android.ui.login.LoginActivity;
import syncthing.android.ui.login.LoginUtils;
import syncthing.api.SyncthingApi;
import syncthing.api.model.Config;
import syncthing.api.model.DeviceConfig;
import syncthing.api.model.Ok;
import timber.log.Timber;

@WelcomeScreenScope
public class WelcomePresenter extends ViewPresenter<WelcomeScreenView>{

    final Context context;
    final AppSettings appSettings;
    final ActivityResultsController activityResultsController;
    final FragmentManager fragmentManager;
    final SyncthingApi syncthingApi;
    final MovingEndpoint endpoint;
    final MovingRequestInterceptor interceptor;

    int page;

    Subscription subscription;
    Subscription splashSubscription;
    boolean skipTutorial;
    boolean generating;
    Credentials newCredentials;
    String error;
    TempCredStorage tmpCreds = new TempCredStorage();

    static class TempCredStorage implements Serializable {
        private static final long serialVersionUID = 0L;
        String alias;
        String deviceId;
        String url;
        String key;
    }

    @Inject
    public WelcomePresenter(
            @ForApplication Context context,
            AppSettings appSettings,
            ActivityResultsController activityResultsController,
            SyncthingApi syncthingApi,
            MovingEndpoint endpoint,
            MovingRequestInterceptor interceptor,
            FragmentManager fragmentManager
    ) {
        this.context = context;
        this.appSettings = appSettings;
        this.activityResultsController = activityResultsController;
        this.syncthingApi = syncthingApi;
        this.endpoint = endpoint;
        this.interceptor = interceptor;
        this.fragmentManager = fragmentManager;
        this.page = 0;
        this.skipTutorial = appSettings.getSavedCredentials().size() > 0;
        if (!this.skipTutorial) {
            this.generating = true;
            fetchApiKey(SyncthingUtils.generateDeviceName(false), "127.0.0.1", "8385", true);
        }
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        reload();
        delayHideSplash();
    }

    void reload() {
        if (!hasView())
            return;
        getView().setPage(page);
        if (!generating) {
            getView().setReady(error == null);
        }
    }

    void updatePage(int page) {
        this.page = page;
        reload();
    }

    void delayHideSplash() {
        int splash;
        if (this.generating)
            splash = 3;
        else
            splash = 1;
        splashSubscription = Observable.timer(splash, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber -> {
                    hideSplash();
                });
    }

    void hideSplash() {
        if (skipTutorial) {
            exitSuccess();
            return;
        }
        if (page != 0)
            return;
        if (!hasView())
            return;
        getView().hideSplash();
    }

    void fetchApiKey(String alias, String url, String port, boolean tls) {
        String uri = LoginUtils.buildUri(url, port, tls);
        tmpCreds.alias = alias;
        tmpCreds.url = uri;
        //sneakily set the required stuffs before invoking the retrofit client
        endpoint.setUrl(uri);
        subscription = syncthingApi.config().zipWith(syncthingApi.system(),
                (config, system) -> {
                    TempCredStorage tmp = new TempCredStorage();
                    tmp.key = config.gui.apiKey;
                    tmp.deviceId = system.myID;
                    for (DeviceConfig d : config.devices) {
                        if (StringUtils.equals(d.deviceID, system.myID)) {
                            tmp.alias = SyncthingUtils.getDisplayName(d);
                        }
                    }
                    Timber.d(ReflectionToStringBuilder.reflectionToString(tmp));
                    return new Pair<>(config, tmp);})
                .retryWhen(
                        attempts -> {
                            final int retries = 60; // Wait up to 10 minutes
                            return attempts
                                    .zipWith(Observable.range(1, retries + 1),
                                            (n, i) -> i)
                                    .flatMap(
                                            times -> {
                                                if (times > retries)
                                                    return Observable.just(null);
                                                return Observable.timer((long) 10, TimeUnit.SECONDS);
                                            });})
                .filter(pair -> pair != null)
                .flatMap(
                        (Pair<Config, TempCredStorage> pair) -> {
                            String username = SyncthingUtils.generateUsername();
                            String password = SyncthingUtils.generatePassword();
                            Timber.i("Generated GUI username and password (" + username + ", " + password + ")");
                            Config config = pair.first;
                            config.gui.user = username;
                            config.gui.password = password;
                            //sneakily set the api key
                            interceptor.setApiKey(config.gui.apiKey);
                            return syncthingApi.updateConfig(config)
                                    .zipWith(Observable.just(pair.second),
                                            (Config c, TempCredStorage t) -> t);
                        })
                .flatMap(
                        (TempCredStorage pass) -> {
                            Timber.i("Restarting Syncthing");
                            return syncthingApi.restart()
                                    .zipWith(Observable.just(pass),
                                            (Ok ok, TempCredStorage t) -> t);
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tmp -> {
                            tmpCreds.deviceId = tmp.deviceId;
                            tmpCreds.key = tmp.key;
                            if (StringUtils.isEmpty(tmpCreds.alias)) {
                                tmpCreds.alias = tmp.alias;
                            }
                            saveKeyAndFinish();
                        },
                        this::notifyError
                );
    }

            void cancelGeneration() {
                if (subscription != null) {
                    subscription.unsubscribe();
                }
                exitCanceled();
            }

            void saveKeyAndFinish() {
                generating = false;
                newCredentials = new Credentials(true,
                        tmpCreds.alias, tmpCreds.deviceId,
                        tmpCreds.url, tmpCreds.key, SyncthingUtils.getSyncthingCACert(context));
                Timber.d(ReflectionToStringBuilder.reflectionToString(newCredentials));
                appSettings.saveCredentials(newCredentials);
                if (hasView()) {
                    getView().setReady(true);
                }
            }

            void notifyError(Throwable t) {
                generating = false;
                error = t.getMessage();
                Timber.d(error);
                if (hasView()) {
                    getView().showError(error);
                    getView().setReady(false);
                }
            }

            void exitSuccess() {
                Intent intent = new Intent()
                        .putExtra(LoginActivity.EXTRA_CREDENTIALS, (Parcelable) newCredentials)
                        .putExtra(LoginActivity.EXTRA_FROM, LoginActivity.ACTION_WELCOME);
                activityResultsController.setResultAndFinish(Activity.RESULT_OK, intent);
            }

            void exitCanceled() {
                Intent intent = new Intent().putExtra(LoginActivity.EXTRA_FROM, LoginActivity.ACTION_WELCOME);
                activityResultsController.setResultAndFinish(Activity.RESULT_CANCELED, intent);
            }
        }

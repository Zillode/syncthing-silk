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

package syncthing.android.ui.session.edit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryCancelEvent;
import com.turhanoz.android.reactivedirectorychooser.event.OnDirectoryChosenEvent;
import com.turhanoz.android.reactivedirectorychooser.ui.OnDirectoryChooserFragmentInteraction;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensilk.common.ui.mortar.ActivityResultsController;
import org.opensilk.common.ui.mortar.ActivityResultsListener;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import mortar.MortarScope;
import rx.Subscription;
import syncthing.android.service.DocumentHelper;
import syncthing.android.ui.common.ActivityRequestCodes;
import syncthing.android.ui.session.SessionPresenter;
import syncthing.api.SessionController;
import syncthing.api.model.FolderConfig;
import syncthing.api.model.FolderDeviceConfig;
import timber.log.Timber;

import static syncthing.android.ui.session.edit.EditModule.INVALID_ID;

/**
 * Created by drew on 3/16/15.
 */
@EditScope
public class EditFolderPresenter extends EditPresenter<EditFolderScreenView> implements ActivityResultsListener {

    final ActivityResultsController activityResultsController;

    FolderConfig origFolder;

    Subscription deleteSubscription;

    @Inject
    public EditFolderPresenter(
            SessionController controller,
            ActivityResultsController activityResultsController,
            EditFragmentPresenter editFragmentPresenter,
            SessionPresenter sessionPresenter,
            @Named("folderid") String folderId,
            @Named("isadd") boolean isAdd,
            @Named("deviceid") String deviceId
    ) {
        super(controller, editFragmentPresenter, sessionPresenter, folderId, deviceId, isAdd);
        this.activityResultsController = activityResultsController;
    }

    @Override
    protected void onEnterScope(MortarScope scope) {
        activityResultsController.register(scope, this);
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();
        if (deleteSubscription != null) {
            deleteSubscription.unsubscribe();
        }
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        if (savedInstanceState == null) {
            if (isAdd) {
                origFolder = FolderConfig.withDefaults();
                if (!INVALID_ID.equals(folderId)) {
                    origFolder.id = folderId;
                }
                if (!INVALID_ID.equals(deviceId)) {
                    origFolder.devices = Collections.singletonList(new FolderDeviceConfig(deviceId));
                }
            } else {
                origFolder = SerializationUtils.clone(controller.getFolder(folderId));
            }
        } else {
            origFolder = (FolderConfig) savedInstanceState.getSerializable("folder");
        }
        getView().initialize(isAdd, controller.isLocal(), origFolder, controller.getRemoteDevices(), controller.getSystemInfo(), savedInstanceState != null);
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putSerializable("folder", origFolder);
    }

    boolean validateFolderId(CharSequence text) {
        if (StringUtils.isEmpty(text)) {
            getView().notifyEmptyFolderId(false);
            return false;
        } else if (!text.toString().matches("^[-.\\w]{1,64}$")) {
            getView().notifyInvalidFolderId(false);
            return false;
        } else if (!isFolderIdUnique(text, controller.getFolders())) {
            getView().notifyNotUniqueFolderId(false);
            return false;
        } else {
            getView().notifyEmptyFolderId(true);
            getView().notifyInvalidFolderId(true);
            getView().notifyNotUniqueFolderId(true);
            return true;
        }
    }

    static boolean isFolderIdUnique(CharSequence text, Collection<FolderConfig> folders) {
        for (FolderConfig f : folders) if (StringUtils.equals(f.id, text)) return false;
        return true;
    }

    boolean validateFolderPath(CharSequence text) {
        boolean valid;
        if (StringUtils.isEmpty(text)) {
            valid = false;
        } else {
            valid = true;
        }
        getView().notifyEmptyFolderPath(valid);
        return valid;
    }

    boolean validateRescanInterval(CharSequence text) {
        boolean valid;
        if (!StringUtils.isNumeric(text) || Integer.decode(text.toString()) < 0) {
            valid = false;
        } else {
            valid = true;
        }
        getView().notifyInvalidRescanInterval(valid);
        return valid;
    }

    boolean validateSimpleVersioningKeep(CharSequence text) {
        if (StringUtils.isEmpty(text) || !StringUtils.isNumeric(text)) {
            getView().notifySimpleVersioningKeepEmpty(false);
            return false;
        } else if (!StringUtils.isNumeric(text) || Integer.decode(text.toString()) < 1) {
            getView().notifySimpleVersioningKeepInvalid(false);
            return false;
        } else {
            getView().notifySimpleVersioningKeepEmpty(true);
            getView().notifySimpleVersioningKeepInvalid(true);
            return true;
        }
    }

    boolean validateStaggeredMaxAge(CharSequence text) {
        boolean valid;
        if (StringUtils.isEmpty(text)
                || !StringUtils.isNumeric(text)
                || Integer.decode(text.toString()) < 0) {
            valid = false;
        } else {
            valid = true;
        }
        getView().notifyStaggeredMaxAgeInvalid(valid);
        return valid;
    }

    boolean validateExternalVersioningCmd(CharSequence text) {
        boolean valid;
        if (StringUtils.isEmpty(text)) {
            valid = false;
        } else {
            valid = true;
        }
        getView().notifyExternalVersioningCmdInvalid(valid);
        return valid;
    }

    void saveFolder() {
        if (saveSubscription != null) {
            saveSubscription.unsubscribe();
        }
        onSaveStart();
        saveSubscription = controller.editFolder(origFolder,
                this::onSavefailed,
                this::onSaveSuccessfull
        );
    }

    void deleteFolder() {
        if (deleteSubscription != null) {
            deleteSubscription.unsubscribe();
        }
        onSaveStart();
        deleteSubscription = controller.deleteFolder(origFolder,
                this::onSavefailed,
                this::onSaveSuccessfull
        );
    }

    void openIgnoresEditor() {
        sessionPresenter.openEditIgnoresScreen(folderId);
    }

    void openPathBrowser() {
        sessionPresenter.openPathBrowser(folderId);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityRequestCodes.DIRECTORY_PICKER) {
            if (resultCode == Activity.RESULT_OK && hasView()) {
                String path = data.getStringExtra("folder");
                getView().setFolderPath(path);
                return true;
            }
        }
        return false;
    }
}

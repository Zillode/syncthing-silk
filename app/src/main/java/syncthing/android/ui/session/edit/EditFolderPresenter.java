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

import android.os.Bundle;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import syncthing.android.ui.session.SessionPresenter;
import syncthing.api.SessionController;
import syncthing.api.model.FolderConfig;
import syncthing.api.model.FolderDeviceConfig;

import static syncthing.android.ui.session.edit.EditModule.INVALID_ID;

/**
 * Created by drew on 3/16/15.
 */
@EditScope
public class EditFolderPresenter extends EditPresenter<EditFolderScreenView> {

    FolderConfig origFolder;

    Subscription deleteSubscription;

    @Inject
    public EditFolderPresenter(
            SessionController controller,
            EditFragmentPresenter editFragmentPresenter,
            SessionPresenter sessionPresenter,
            @Named("folderid") String folderId,
            @Named("isadd") boolean isAdd,
            @Named("deviceid") String deviceId
    ) {
        super(controller, editFragmentPresenter, sessionPresenter, folderId, deviceId, isAdd);
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
        getView().initialize(isAdd, origFolder, controller.getRemoteDevices(), controller.getSystemInfo(), savedInstanceState != null);
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putSerializable("folder", origFolder);
    }

    boolean validateFolderId(CharSequence text) {
        if (StringUtils.isEmpty(text)) {
            getView().notifyEmptyFolderId();
            return false;
        } else if (!text.toString().matches("^[-.\\w]{1,64}$")) {
            getView().notifyInvalidFolderId();
            return false;
        } else if (!isFolderIdUnique(text, controller.getFolders())) {
            getView().notifyNotUniqueFolderId();
            return false;
        } else {
            return true;
        }
    }

    static boolean isFolderIdUnique(CharSequence text, Collection<FolderConfig> folders) {
        for (FolderConfig f : folders) if (StringUtils.equals(f.id, text)) return false;
        return true;
    }

    boolean validateFolderPath(CharSequence text) {
        if (StringUtils.isEmpty(text)) {
            getView().notifyEmptyFolderPath();
            return false;
        } else {
            return true;
        }
    }

    boolean validateRescanInterval(CharSequence text) {
        if (!StringUtils.isNumeric(text) || Integer.decode(text.toString()) < 0) {
            getView().notifyInvalidRescanInterval();
            return false;
        } else {
            return true;
        }
    }

    boolean validateSimpleVersioningKeep(CharSequence text) {
        if (StringUtils.isEmpty(text) || !StringUtils.isNumeric(text)) {
            getView().notifySimpleVersioningKeepEmpty();
            return false;
        } else if (!StringUtils.isNumeric(text) || Integer.decode(text.toString()) < 1) {
            getView().notifySimpleVersioningKeepInvalid();
            return false;
        } else {
            return true;
        }
    }

    boolean validateStaggeredMaxAge(CharSequence text) {
        if (StringUtils.isEmpty(text)
                || !StringUtils.isNumeric(text)
                || Integer.decode(text.toString()) < 0) {
            getView().notifyStaggeredMaxAgeInvalid();
            return false;
        } else {
            return true;
        }
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

}

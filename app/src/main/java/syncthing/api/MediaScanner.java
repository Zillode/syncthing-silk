package syncthing.api;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;

import org.opensilk.common.rx.RxBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import syncthing.android.ui.session.Update;
import timber.log.Timber;

public class MediaScanner {

    Context appContext;
    LinkedBlockingDeque<String> newFiles;
    Observable<SessionController.ChangeEvent> input;
    Observable<SessionController.ChangeEvent> debounced;
    Observable<List<SessionController.ChangeEvent>> buffer;
    Subscription rescanSubscription;

    public MediaScanner(Context appContext) {
        Timber.d("newMediaScanner");
        this.appContext = appContext;
        this.newFiles = new LinkedBlockingDeque<>();
    }

    public void subscribeUpdates(Observable<SessionController.ChangeEvent> observable) {
        Timber.d("subscribe MediaScanner");
        input = observable.filter(e -> e.change == SessionController.Change.FILE_DOWNLOADED);
        debounced = input.debounce(2, TimeUnit.SECONDS);
        buffer = input.buffer(debounced);
        rescanSubscription = buffer.subscribe(events -> {
            Timber.d("informing media scanner about new files");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String filenames[] = new String[events.size()];
                int idx = 0;
                for (SessionController.ChangeEvent e : events) {
                    filenames[idx++] = e.id + File.separatorChar + e.id2;
                }
                MediaScannerConnection.scanFile(appContext, filenames, null, null);
            } else {
                Map<String, Boolean> folders = new HashMap<String, Boolean>();
                for (SessionController.ChangeEvent e : events) {
                    if (!folders.get(e.id)) {
                        folders.put(e.id, true);
                        appContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + e.id)));
                    }
                }
            }
        });

    }

    public void unsubscribeUpdates() {
        Timber.d("unsubscribe MediaScanner");
        rescanSubscription.unsubscribe();
        buffer = null;
        debounced = null;
    }

}

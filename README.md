##Syncthing Silk for Android

This app aims to be a full featured [Syncthing](https://syncthing.net/) client comparable to the web ui.
It is based on [SyncthingAndroid from OpenSilk](https://github.com/OpenSilk/SyncthingAndroid) but with a focus on user friendliness.
This includes:
  - A welcome dialog explaining the basics of Syncthing Silk for Android
  - Support for file watchers (based on [Syncthing-inotify](https://github.com/syncthing/syncthing-inotify))
  - A directory picker
  - In-app TLS to preserve Androids sandboxing
  - Asynchrounous key generation in the background
  - Cancelling login processes
  - Support for intel/i386 cpus
  - Meaningful default device name
  - A default local Syncthing instance

###Building

#####Syncthing binary

```bash
# Build Syncthing
./build-syncthing-go.sh
# Build Syncthing-inotify
./build-syncthing-inotify.sh
```

#####App

```bash
# Build apk
./gradlew app:assembleDebug
```

###Contributing

Pull requests are welcome via github. However, be aware that we frequently rebase this codebase on top of [SyncthingAndroid from OpenSilk](https://github.com/OpenSilk/SyncthingAndroid). This means we will break your git tree.


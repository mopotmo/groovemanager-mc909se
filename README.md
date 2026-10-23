# GrooveManager MC-909 Sample Editor

A sample editor for the Roland MC-909 groovebox, written in Java (SWT/JFace, Java Sound) in 2004 as the practical part of my diploma thesis [*"Java Sound als Plattform für die Entwicklung studiotauglicher Audioapplikationen"*](docs/thesis/diplomarbeit.pdf) ("Java Sound as a platform for developing studio-grade audio applications", Hochschule der Medien Stuttgart, August 2004). This repository holds the original sources of release 1.0 (2004-08-08).

> **Status: archive.** The code is published as it was in 2004. It does not build or run out of the box on a current system (see [Building](#building)). A revived version that runs on today's Java and macOS/Windows/Linux is planned.

![Simple Sample Editor](docs/screenshots/simple-sample-editor.png)

## Features (2004)

There are two applications:

**Simple Sample Editor** (`app/sse`), the basic version:
- Opens any number of files in tabs, in every format a Java Sound plugin can read. Files can also be opened by drag and drop. Saving is WAV only.
- **Non-destructive editing** with cut lists: Copy, Cut, Paste, Delete, Trim, and unlimited Undo/Redo.
- **Effects** through a plugin interface (`META-INF/services/com.groovemanager.sampled.fx.Effect`): Normalize, Pseudo Echo, Chorus/Flanger (experimental). Each effect is applied non-destructively through a temporary file.
- **Waveform display** with cached peak files, selection, markers (add with Ctrl+click, move with Shift and the arrow keys), zoom, and an overview of the whole file.
- **Streaming playback and recording.** The Settings dialog has driver selection, buffer size and player thread priority. Info panels show the file, the device (including buffer fill level) and JVM memory.

**MC-909 Sample Editor** (`app/mc909se`) extends the basic version for the Roland MC-909:
- **Per-sample MC-909 properties:** name, loop mode, time-stretch type, bars/time signature/BPM, with BPM calculated from the selection.
- **Sample import/export** to and from the MC-909 in USB storage mode (internal flash or SmartMedia card). The WAV files carry the chunks the MC-909 expects. Patches can be created automatically over MIDI SysEx. They then have to be saved on the device, because saving remotely isn't possible.
- **Rhythm set export** from a sample split up with markers, one slice per pad. Optional: the slice sequence as a Standard MIDI File, or played to the MC-909 over MIDI (with its own MIDI Clock implementation, because Java Sound's sequencer lacked it) for realtime recording.
- **Preview** through on-screen MC-909 pads or an external MIDI keyboard. This was a hard realtime requirement.

| | |
|---|---|
| ![Export samples](docs/screenshots/mc909-export-samples.png) | ![Export rhythm set](docs/screenshots/mc909-export-rhythm-set.png) |

![MC-909 pads](docs/screenshots/mc909-pads.png)

Also part of the thesis:
- **Java Sound plugins:** [jsASIO](https://github.com/mopotmo/jsasio) (ASIO drivers as Java Sound mixers), [jsREX](https://github.com/mopotmo/jsrex) (ReCycle file reader), and [floatconversion](https://github.com/mopotmo/floatconversion) (a `PCM_FLOAT` encoding plus conversions).
- **A separate plugin interface** (`com.groovemanager.sampled.providers`, managed by `AudioManager`) for reading file formats and writing audio files. It works around weaknesses of Java Sound's `AudioFileReader`/`AudioFileWriter` SPIs. Providers declare their supported types, formats and property keys, and `AudioManager` chooses the best match.

Both editors were tested on Windows and Linux in 2004.

## Repository layout

| Path | Contents |
|---|---|
| `app/src/com/groovemanager/core`, `sampled`, `gui`, `actions`, `thread`, `midi`, `pref`, … | The GrooveManager framework, released as `gm.core.jar` |
| `app/src/com/groovemanager/app/sse` | Simple Sample Editor. It was released separately with its own `start.jar` (main class `com.groovemanager.app.sse.Starter`) and is also contained in the MC-909 `start.jar` |
| `app/src/com/groovemanager/app/mc909se`, `gui/custom/mc909` | MC-909 Sample Editor, released as `start.jar` (main class `com.groovemanager.app.mc909se.Starter`) |
| `app/resources/META-INF` | Manifest and service registrations of the released `start.jar` |
| `app/root` | Icons and MC-909 screen images. The app loads them from `./root` at runtime |
| `docs/install` | Original installation notes (German) |
| `docs/thesis/diplomarbeit.pdf` | The diploma thesis (German, 106 pages). Chapter VI describes the editors and plugins in detail |
| `docs/screenshots` | Screenshots from the thesis |
| `samples` | Example sounds from the thesis CD-ROM (16/24/32-bit WAV). A longer recording, "Elektronische Kerzenlichtmusik.wav" (82 MB), is attached to the v1.0 release |

Related repositories:
- [jsASIO](https://github.com/mopotmo/jsasio): ASIO `MixerProvider` for Java Sound (Windows)
- [jsREX](https://github.com/mopotmo/jsrex): ReCycle file reader for Java Sound (Windows)
- [floatconversion](https://github.com/mopotmo/floatconversion): `PCM_FLOAT` encoding and conversions for Java Sound. It was part of this repository up to v1.0

## Building

The 2004 build was done in Eclipse. There is no build script. To compile the original code you need:

- **JDK 5 to 8.** `AudioManager` uses `sun.misc.Service`, which was removed in Java 9. The code itself is Java 1.4 style.
- **SWT 3.0.x and JFace 3.0** (`swt.jar`, `jface.jar`, `runtime.jar`, `boot.jar` from Eclipse 3.0) plus the matching SWT native libraries.
- `-encoding UTF-8` (the sources were converted from CP1252 for this repository).

The app expects to be started from its install directory, with the following layout:

```
start.jar  gm.core.jar  root/  ext/*.jar  <native libraries>
java -Djava.library.path=. -jar start.jar
```

The manifest's `Class-Path` lists the jars expected in `ext/`.

## License

MIT, see [LICENSE](LICENSE).

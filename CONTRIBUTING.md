# Contributing

Feedback, issues, forks and pull requests are welcome.

## Feedback and issues

[Open an issue](../../issues) for bugs, questions and ideas. It helps to include:
- your operating system and version, and how you run the editor (installer or `./gradlew run`)
- what you did, what you expected and what happened instead
- the log, if there is an error: `~/.groovemanager/error.log` for the installed app, `app/root/error.log` when run from source
- for problems with the MC-909: the export mode (USB storage or MIDI) and the MIDI interface you use

The editor is mainly tested on macOS, so reports from Windows and Linux are especially useful, as are reports of using it with a real MC-909.

## Forks and pull requests

Fork the repository and build it as described in [Running it today](README.md#running-it-today). For a pull request:
- Keep a pull request to one topic and describe what it changes and how you tested it.
- Follow the style of the surrounding code: it is 2004-era Java (raw collections, anonymous classes, Javadoc on every class and method, tabs for indentation). Don't modernize code that you don't otherwise change.
- If you change the look, check it in all four themes (*Settings → Appearance*).
- The state of the 2004 release is kept at the tag [`v1.0`](../../tree/v1.0) and isn't changed.

By contributing, you agree that your contributions are licensed under the [MIT License](LICENSE) of this project.

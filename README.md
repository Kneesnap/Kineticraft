# Kineticraft v4 - The Lost City

A custom implementation of many Essentials features and staff utilities, built with expandability in mind.

## Instructions:
1. Clone the repository locally.
    - For Github Desktop, simple press the green `Clone or download` button and press `Open in Desktop`.
2. Open the downloaded repository in an IDE. We recommend IntelliJ IDEA (Community or Professional), and this tutorial currently only supports it.
3. Configure Project Structure:
    1. Project Settings > Project:
        1. Set `Project SDK` to `1.8` if it's not already set.
        2. Set `Project language level` to `8 - Lambdas, type annotations etc.`.
    2. Project Settings > Modules > Kineticraft:
        1. Sources:
            1. Mark the `src` directory as a `Source`.
        2. Paths:
            1. Press the `Use module compile output path` radio button.
            2. Set `Output path` to a new directory named `build` in the repository directory.
            3. Set `Test output path` to a new directory named `test` in the repository directory.
    3. Project Settings > Libraries:
        - For each `.jar` file or directory (if applicable) in the `/libs/` directory, press the `+` button to add a new project library from java correlating to said file.
    4. Project Settings > Artifacts:
        1. Press the `+` button, hover over `JAR` and select `Empty`.
        2. Name the new entry in the sidebar `Kineticraft`.
        3. Toggle the `Include in project build` checkmark.
        4. In the `Available Elements` pane, double click `'Kineticraft' compile output` to move it over to the left pane.
        5. Press the `Use Exisiting Manifest` button, and select `META-INF > MANIFEST.MF` from the dropdown file browser.
4. Preferences > Build, Execution, Deployment > Compiler > Annotation Processors:
    - Click the `Enable annotation processing` checkbox.
5. Preferences > Plugins > Browse repositories:
    - Search and install `Lombok Plugin`.
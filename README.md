# Kineticraft v4 - The Lost City
A custom implementation of many Essentials features and staff utilities, built with expandability in mind.

## Instructions:
- This tutorial only supports using IntelliJ IDEA (either Community or Professional), which we strongly recommend. You may configure it using your IDE of choice, but if you do, you'll be on your own.
1. Clone the repository locally.
    - For Github Desktop, simply press the green `Clone or download` button and press `Open in Desktop`.
2. Open the downloaded repository in your IDE.
3. Configure Project Structure:
    1. Project Settings > Project:
        1. Set `Project SDK` to `1.8` if it's not already set.
        2. Set `Project language level` to `8 - Lambdas, type annotations etc`.
    2. Project Settings > Modules > Kineticraft:
        1. Sources:
            1. Mark the `src` directory as a `Source`.
        2. Paths:
            1. Press the `Use module compile output path` radio button.
            2. Set `Output path` to a new directory named `build` in the repository directory.
            3. Set `Test output path` to a new directory named `test` in the repository directory.
    3. Project Settings > Libraries:
        1. Press the `+` button in the top of the left panel to toggle the `New Project Library` popup and select `Java`.
        2. From the dropdown file browser, choose the `/libs/` directory.
        3. In the `Choose Modules` window, select the `Kineticraft` module and press `OK`.
        4. If there are any subdirectories in the `/libs/` directory (eg. Kotlin dependencies grouped into a directory), you need to manually add them using the `+` button in the bottom of the right panel.
    4. Project Settings > Artifacts:
        1. Press the `+` button, hover over `JAR` and select `Empty`.
        2. Name the new entry in the sidebar `Kineticraft`.
        3. Toggle the `Include in project build` checkmark.
        4. In the `Available Elements` panel, double click `'Kineticraft' compile output` to move it over to the left panel.
        5. Press the `Use Exisiting Manifest` button, and select `META-INF > MANIFEST.MF` from the dropdown file browser.
4. Preferences > Build, Execution, Deployment > Compiler > Annotation Processors:
    - Click the `Enable annotation processing` checkbox.
5. Preferences > Plugins > Browse repositories:
    - Search and install `Lombok Plugin`.
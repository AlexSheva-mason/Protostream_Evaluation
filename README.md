
Technical Test for Protostream

This is a primitive app that displays a list of items in grid view.

Created using: Android Studio Bumblebee | 2021.1.1
               AVD Emulator: Pixel 5 API 30
               
It was not tested on any other IDE build / avd emulator / device.

- 1st screen - Login page.

Provide any email (accepts string with length > 5, must include at least one of '@' and '.' characters).
Password - any non-empty string

- 2nd screen - List of items.

Items are displayed in grid view with 2 columns.
Long tap on the item will open expanded item card with bigger image and description.
Short tap on the item will navigate to screen #3

- 3rd screen - Item ID

This will simply display an id of item selected on previous screen

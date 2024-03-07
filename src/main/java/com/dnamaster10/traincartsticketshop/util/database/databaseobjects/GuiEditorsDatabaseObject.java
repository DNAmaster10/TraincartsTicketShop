package com.dnamaster10.traincartsticketshop.util.database.databaseobjects;

import java.util.List;

public record GuiEditorsDatabaseObject(
        int guiId,
        List<String> editorUuids
) {}

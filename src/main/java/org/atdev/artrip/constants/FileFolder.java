package org.atdev.artrip.constants;

public enum FileFolder {

    POSTERS("posters"),
    REVIEWS("reviews"),
    PROFILES("profiles");

    private final String folderName;

    FileFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getPath() {
        return folderName;
    }

}

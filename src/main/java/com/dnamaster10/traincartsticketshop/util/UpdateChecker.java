package com.dnamaster10.traincartsticketshop.util;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    public final String gitRepo;
    private final String currentVersion;
    private final Plugin plugin;

    public UpdateChecker(String gitRepo, Plugin plugin) {
        this.gitRepo = gitRepo;
        this.currentVersion = plugin.getDescription().getVersion();
        this.plugin = plugin;
    }

    // Run async pls
    public boolean checkIfUpdateAvailable() {
        try {
            boolean snapshot = currentVersion.contains("SNAPSHOT");
            InputStream inputStream = new URL("https://api.github.com/repos/" + this.gitRepo + "/releases?per_page=" + (snapshot ? 100 : 1)).openStream();
            Scanner scanner = new Scanner(inputStream);
            StringBuffer buffer = new StringBuffer();
            while (scanner.hasNext()) {
                buffer.append(scanner.next());
            }

            // For snapshots, check if the version has been properly released yet
            System.out.println("\"tag_name\":\"v" + this.currentVersion.replace("-SNAPSHOT", "") + "\"");
            System.out.println(snapshot);
            System.out.println(buffer.toString());
            if (snapshot) return buffer.toString().contains("\"tag_name\":\"v" + this.currentVersion.replace("-SNAPSHOT", "") + "\"");
                // For ordinary releases, check if the latest version string = this version
            else return !buffer.toString().contains("\"tag_name\":\"v" + currentVersion + "\"");
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to check for updates: " + e.getMessage());
            return false;
        }
    }
}

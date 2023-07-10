package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;

public class FileMonitor {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Specify the file path to monitor
        String filePath = "provide-path-here/newFile.txt";

        // Obtain the file's path
        Path fileToMonitor = Paths.get(filePath);

        // Create a watch service and register
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path parentDirectory = fileToMonitor.getParent();
        parentDirectory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("File monitor is now ready. Waiting for modifications...");

        while (true) {
            // Take the watch key
            WatchKey key = watchService.take();

            // Process all events
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // If the modified event is detected for the specified file
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path modifiedFilePath = (Path) event.context();

                    // Check if the modified file is the file we are monitoring
                    if (modifiedFilePath.equals(fileToMonitor.getFileName())) {
                        System.out.println("File modified: " + fileToMonitor);
                        BufferedReader reader = new BufferedReader(new FileReader(fileToMonitor.toFile()));
                        String lastline, line = null;
                        while((line = reader.readLine())!=null) {
                            lastline = line;
                            System.out.println("The last line is ::" + lastline);
                        }
                    }
                }
            }

            // Reset the key to receive further watch events
            boolean valid = key.reset();

            // If the key is no longer valid, exit the loop
            if (!valid) {
                break;
            }
        }

        // Close the watch service
        watchService.close();
    }
}

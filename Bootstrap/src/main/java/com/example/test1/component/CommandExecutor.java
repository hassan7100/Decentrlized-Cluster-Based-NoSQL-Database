package com.example.test1.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CommandExecutor {
    public void exec(String... args) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        List<String> commands;
        if (isWindows) {
            commands = Stream.of("cmd.exe", "/c").collect(Collectors.toList());
        }
        else
            commands = Stream.of("sh", "-c").collect(Collectors.toList());
        for (String cmd : args)
            commands.add(cmd);
        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process =builder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }

        // Capture and display the error stream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.error(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("Exited with error code : " + exitCode);
        }

    }
}

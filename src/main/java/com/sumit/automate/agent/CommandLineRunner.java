package com.sumit.automate.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandLineRunner {

    public static void main(String[] args) {
        try {
            // Example command: lists current directory contents
            // You can replace this with any command you want
            String command = "main -m C:\\projects\\models\\ggml-base.en.bin -np -nt  -f C:\\projects\\voice\\recording_20250330_115101.wav"; // Use "ls" for Unix-based systems
           // String command = "main -m C:\\projects\\models\\ggml-model-whisper-tiny.en-q8_0.bin -f C:\\projects\\whispercpp\\whisper.cpp\\samples\\   "; // Use "ls" for Unix-based systems

//
            Process process = Runtime.getRuntime().exec(command);

            // Capture standard output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            System.out.println("Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Capture error output, if any
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            boolean hasErrors = false;
            while ((line = errorReader.readLine()) != null) {
                if (!hasErrors) {
                    System.out.println("\nErrors:");
                    hasErrors = true;
                }
                System.err.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

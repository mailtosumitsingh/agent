package com.sumit.automate.agent;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SoundRecorder {

    private TargetDataLine line;
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private File outputFile;

    public void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                return;
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            String directoryPath = "C:\\projects\\voice";
            Files.createDirectories(Paths.get(directoryPath));

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            outputFile = new File(directoryPath + "\\recording_" + timeStamp + ".wav");

            Thread recordingThread = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    AudioSystem.write(ais, fileType, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            recordingThread.start();
            System.out.println("Recording started...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String stop() {
        if (line != null) {
            line.stop();
            line.close();
            System.out.println("Recording stopped. File saved to: " + outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();
        }
        return "";

    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1; // mono
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public static void main(String[] args) throws Exception {
        SoundRecorder recorder = new SoundRecorder();

        System.out.println("Press Enter to start recording...");
        System.in.read();
        recorder.start();

        System.out.println("Press Enter to stop recording...");
        System.in.read();
        recorder.stop();
    }
}

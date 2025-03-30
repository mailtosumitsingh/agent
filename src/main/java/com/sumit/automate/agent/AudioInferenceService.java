package com.sumit.automate.agent;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class AudioInferenceService {
//server --port 9099 -m C:\projects\models\ggml-base.en.bin
    public String sendAudioFile(String filePath) throws Exception {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        HttpPost post = new HttpPost("http://127.0.0.1:9099/inference");
        post.setHeader("User-Agent", "insomnia/10.1.1");

        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addPart("file", new FileBody(audioFile, ContentType.create("audio/wav")))
                .addPart("temperature", new StringBody("0.0", ContentType.TEXT_PLAIN))
                .addPart("temperature_inc", new StringBody(".2", ContentType.TEXT_PLAIN))
                .addPart("response_format", new StringBody("json", ContentType.TEXT_PLAIN))
                .build();

        post.setEntity(entity);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            InputStream responseStream = response.getEntity().getContent();
            String responseText = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

            int statusCode = response.getStatusLine().getStatusCode();
            return  extractCleanedText(responseText);
        }
    }
    public String extractCleanedText(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        String text = root.path("text").asText();
        return text.replace("[ Silence ]", "").trim();
    }
    public static void main(String[] args) throws Exception{
        AudioInferenceService service  = new AudioInferenceService();
       String text = service.sendAudioFile("C:\\projects\\voice\\recording_20250329_091357.wav");
        System.out.println("%%%%%%%%%%%%%%%%%%");
        System.out.println(text);
    }
}


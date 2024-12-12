package jp.kurashina.commons.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class FileUtils {

    private static final ResourceLoader resourceLoader;

    static {
        resourceLoader = null;
    }

    public static String getStringByFilename(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static InputStream getInputStreamByFilename(String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        return resource.getInputStream();
    }
}
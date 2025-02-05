package me.quickscythe.api.v1.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.quickscythe.blockbridge.core.server.BridgeServer;
import me.quickscythe.blockbridge.core.server.BridgeServlet;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class HealthReportHandler extends BridgeServlet {

    private final File folder;

    public HealthReportHandler(BridgeServer server) {
        super(server);
        folder = new File(server.integration().dataFolder(), "web");
        if (!folder.exists()) {
            Logger logger = server.integration().logger();
            logger.info("Web folder does not exist, creating: {}", folder.mkdirs() ? "Success" : "Failed");
            logger.info("Copying web files");

            try {
                copyResources("web", folder, true);
            } catch (IOException e) {
                server.integration().logger().error("Failed to copy web files", e);
            }
        }


    }

    public void copyResources(String resourcePath, File targetDirectory, boolean isRoot) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File resourceDirectory = new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile());

        if (resourceDirectory.isDirectory()) {
            for (String child : Objects.requireNonNull(resourceDirectory.list())) {
                copyResources(resourcePath + "/" + child, isRoot ? targetDirectory : new File(targetDirectory, resourceDirectory.getName()), false);
            }
        } else {
            if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                throw new IOException("Failed to create target directory: " + targetDirectory);
            }

            try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
                if (resourceStream == null) {
                    throw new IOException("Resource not found: " + resourcePath);
                }

                File targetFile = new File(targetDirectory, resourceDirectory.getName());
                Files.copy(resourceStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String finalPath = req.getRequestURI().replaceFirst("/healthreport", "");

        if (!finalPath.endsWith(".html") && !finalPath.endsWith(".css") && !finalPath.endsWith(".js") && !finalPath.endsWith(".png") && !finalPath.endsWith("jpg") && !finalPath.endsWith(".jpng"))
            finalPath += (finalPath.endsWith("/") ? "" : "/") + "index.html";

        String fileExtension = finalPath.substring(finalPath.lastIndexOf(".") + 1);

        System.out.println(finalPath + "{" + fileExtension + "}");

        resp.setContentType("text/" + fileExtension);

        File htmlFile = new File(folder, finalPath.substring(1));

        if (htmlFile.exists()) {
            try (InputStream inputStream = new FileInputStream(htmlFile); ServletOutputStream outputStream = resp.getOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "HTML file not found");
        }


    }
}

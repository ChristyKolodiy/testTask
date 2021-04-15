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

    private static final String fromFileFolder = "/Users/consolkaaa/files/";
    private static final String toSortedFolder = "/Users/consolkaaa/sorted_files";
    private static final String toDocuments = toSortedFolder + "/Documents/";
    private static final String toMusic = toSortedFolder + "/Music/";
    private static final String toPhotos = toSortedFolder + "/Photos/";
    private static final String toOther = toSortedFolder + "/Other/";
    private static final String zippedDocuments = toSortedFolder + "/Documents.zip";
    private static final String zippedMusic = toSortedFolder + "/Music.zip";
    private static final String zippedPhotos = toSortedFolder + "/Photos.zip";
    private static final String zippedOther = toSortedFolder + "/Other.zip";

    private static final List<String> directories = Arrays.asList(toDocuments, toMusic, toPhotos, toOther, toSortedFolder);

    private static void createDirectories(List<String> directories){
        directories.stream().forEach(dir -> {
            try {
                Files.createDirectories(Paths.get(dir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void moveTo(List<File> filesList, String sourcePath, String destinationPath){
        filesList.stream().forEach(file -> {
            Path source = Paths.get(sourcePath + file.getName());
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

        File folder = new File(fromFileFolder);
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
                moveTo(files, fromFileFolder, toDocuments);
            } else if (predicate == belongsToPhotos){
                moveTo(files, fromFileFolder, toPhotos);
            } else if (predicate == belongsToMusic){
                moveTo(files, fromFileFolder, toMusic);
            }
        });

        List<File> otherFiles = Arrays.asList(folder.listFiles());
        moveTo(otherFiles, fromFileFolder, toOther);

        zipFolder(Paths.get(toDocuments), Paths.get(zippedDocuments));
        zipFolder(Paths.get(toPhotos), Paths.get(zippedPhotos));
        zipFolder(Paths.get(toMusic), Paths.get(zippedMusic));
        zipFolder(Paths.get(toOther), Paths.get(zippedOther));

    }
}

package file.task;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileOperation {

    private static final String fromFileFolder = "/Users/consolkaaa/files/";
    private static final String toSortedFolder = "/Users/consolkaaa/sorted_files";
    private static final String toDocuments = toSortedFolder + "/Documents";
    private static final String toMusic = toSortedFolder + "/Music";
    private static final String toPhotos = toSortedFolder + "/Photos";
    private static final String toOther = toSortedFolder + "/Other";
    private static final String SLASH = "/";
    private static final String ZIP = ".zip";

    private static File folder = new File(fromFileFolder);
    private static Logger logger = Logger.getLogger(FileOperation.class.getName());

    private static Predicate<File> belongsToDocuments = belongsToCategory(Multimedia.TXT, Multimedia.DOC, Multimedia.DOCX);
    private static Predicate<File> belongsToPhotos = belongsToCategory(Multimedia.JPG, Multimedia.JPEG, Multimedia.PNG);
    private static Predicate<File> belongsToMusic = belongsToCategory(Multimedia.MP3, Multimedia.WAV, Multimedia.MP4);

    private static void createDirectories(List<String> directories) throws IOException{
        Files.createDirectories(Paths.get(toSortedFolder));
        directories.stream().forEach(dir -> {
            try {
                Files.createDirectories(Paths.get(dir));
            } catch (IOException e) {
                logger.info("Directory " + dir + "couldn't be created.");
            }
        });
    }

    private static void moveTo(List<File> filesList, String sourcePath, String destinationPath){
        filesList.stream().forEach(file -> {
            Path source = Paths.get(sourcePath + file.getName());
            Path target = Paths.get(destinationPath + SLASH + file.getName());
            try {
                Files.move(source, target);
            } catch (IOException e) {
                logger.info("Moving files to " + destinationPath + "couldn't be done. File"
                        + file.getName() + "already exists.");
            }
        });
    }

    private static void sortFilesIntoFolders(List<Predicate<File>> categoryList, List<File> files){
        categoryList.stream().forEach(predicate -> {
            List<File> sortedFiles = files.stream().filter(predicate)
                    .collect(Collectors.toList());

            if(predicate == belongsToDocuments) {
                moveTo(sortedFiles, fromFileFolder, toDocuments);
            } else if (predicate == belongsToPhotos){
                moveTo(sortedFiles, fromFileFolder, toPhotos);
            } else if (predicate == belongsToMusic){
                moveTo(sortedFiles, fromFileFolder, toMusic);
            }
        });

        List<File> otherFiles = Arrays.asList(folder.listFiles());
        moveTo(otherFiles, fromFileFolder, toOther);
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

    private static void zipDirectoriesList(List<String> directories){
        directories.stream().forEach(dir -> {
            try {
                zipFolder(Paths.get(dir), Paths.get(dir + ZIP));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static Predicate<File> belongsToCategory(Multimedia media1, Multimedia media2, Multimedia media3) {
        return file -> file.getName().endsWith(media1.getValue())
                || file.getName().endsWith(media2.getValue())
                || file.getName().endsWith(media3.getValue());
    }

    public static void main(String[] args) throws Exception {

        List<String> directories = Arrays.asList(toDocuments, toMusic, toPhotos, toOther);
        createDirectories(directories);

        List<File> fileList = Arrays.asList(folder.listFiles());
        fileList.stream().forEach(file -> file.getName());

        List<Predicate<File>> categoryList = Arrays.asList(belongsToDocuments, belongsToPhotos, belongsToMusic);
        sortFilesIntoFolders(categoryList, fileList);

        zipDirectoriesList(directories);

    }
}

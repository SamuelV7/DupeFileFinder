package com.company;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWorker {
    private byte[] fileBytes;
    private Optional<Path> thePath;
    public enum FileOrDirectory{
        FILE,
        DIRECTORY,
    }
    public FileWorker(String path) {
        this.thePath = Optional.of(Paths.get(path));
    }
    //Checks if a path is file or directory, returns an optional
    //Returns empty optional if there is an issue.
    public static Optional<FileOrDirectory> checkFileOrDirectory(Optional<Path> thePath){
        final var isDirectory = Files.isDirectory(thePath.get());
        final var isFile = Files.isRegularFile(thePath.get());
        Optional<FileOrDirectory> fileOrDirectoryOptional = Optional.empty();
        if (isDirectory) {
            fileOrDirectoryOptional = Optional.of(FileOrDirectory.DIRECTORY);
            return fileOrDirectoryOptional;
        }
        if (isFile) return Optional.of(FileOrDirectory.FILE);
        return fileOrDirectoryOptional;
    }
    //need to handle exception in the case it's not directory?
    public static ArrayList<Path> listItemsInDirectory(Optional<Path> thePath) throws IOException {
        final Stream<Path> listOfFiles = Files.list(thePath.get());
        final var fileList = listOfFiles.toList();
        final var fileArrayList = new ArrayList<Path>(fileList);
        return fileArrayList;
    }
    public static ArrayList<Path> recursiveWalkOfDirectory(Optional<Path> thePath, ArrayList<Path> fileList) throws IOException {
        //get files in directory
        final var listOfFiles = listItemsInDirectory(thePath);
        listOfFiles.forEach((Path filePath)->{
            //check if file path is directory or file
            if (checkFileOrDirectory(Optional.of(filePath)).get() == FileOrDirectory.DIRECTORY){
                //if it is a directory then recurse until you have a file
                try {
                    recursiveWalkOfDirectory(Optional.of(filePath), fileList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(checkFileOrDirectory(Optional.of(filePath)).get() == FileOrDirectory.FILE){
                fileList.add(filePath);
            }
        });
        return fileList;
    }
    //need a function to recursively walk through the directory
    public static ArrayList<Path> walkOfDirectoryRecursive(String thePath) throws IOException {
        final Optional<Path> optionalPath = Optional.of(Paths.get(thePath));
        ArrayList fillWithPaths = new ArrayList<Path>();
        final ArrayList<Path> listOfFilesInDirectory = recursiveWalkOfDirectory(optionalPath, fillWithPaths);
        return listOfFilesInDirectory;
    }

    public static String toHex(byte[] byteArray){
        StringBuilder theHex = new StringBuilder();
        for (byte theByte: byteArray) {
            theHex.append(String.format("%02x", theByte));
        }
        return theHex.toString();
    }

    public static String getLargeFileHash(String path) throws IOException, NoSuchAlgorithmException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
        return hashStreamUpdate(bufferedInputStream);
    }

    public static String hashStreamUpdate(BufferedInputStream bis) throws IOException, NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        var byteBuffer = new byte[1024000];
        int count = 0;
        while ((count = bis.read(byteBuffer)) > 0){
            digest.update(byteBuffer, 0, count);
        }
        bis.close();
        var hashByteArray = digest.digest();
        return toHex(hashByteArray);
    }
}
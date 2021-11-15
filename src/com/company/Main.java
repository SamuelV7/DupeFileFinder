package com.company;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static HashMap<String, ArrayList<Path>> hashAndDupeFileLocation(ArrayList<Path> thePaths){
        HashMap<String, ArrayList<Path>> fileLocationsHashMap = new HashMap<>();
        thePaths.forEach((Path thePath)->{
            try{
                System.out.println("Reading File: "+ thePath);
                var hashOfFile = FileWorker.getLargeFileHash(String.valueOf(thePath));
                if (fileLocationsHashMap.containsKey(hashOfFile)){
                    var fileList = fileLocationsHashMap.get(hashOfFile);
                    fileList.add(thePath);
                    fileLocationsHashMap.put(hashOfFile, fileList);
                }
                else {
                    ArrayList<Path> filePathList = new ArrayList<>();
                    filePathList.add(thePath);
                    fileLocationsHashMap.put(hashOfFile, filePathList);
                }
            } catch (IOException | NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        });
        return fileLocationsHashMap;
    }
    public static void showFilesWithSameHash(HashMap<String, ArrayList<Path>> hashMap){
        System.out.println("Files Scanned: "+hashMap.size());
        for (Map.Entry<String, ArrayList<Path>> entry: hashMap.entrySet()){
            var dupeArrayLen = entry.getValue().size();
            if (dupeArrayLen>1){
                System.out.print("Hash: "+ entry.getKey()+" ");
                System.out.print("MatchedWith: "+entry.getValue().size()+" "+ entry.getValue()+" ");
                System.out.println(" ");
            }
        }
    }

    public static void main(String[] args) throws IOException{
	// write your code here
        //make it so that the file with higher filesize and its duplicates show up on the top of
        //the printed list
        final var path = "F:/Downloads";
        //create new file object
        HashMap<String, ArrayList<Path>> fileDupeLocation = new HashMap<String, ArrayList<Path>>();
        final var filePathList = FileWorker.walkOfDirectoryRecursive(path);
        final var theMap = hashAndDupeFileLocation(filePathList);
        showFilesWithSameHash(theMap);
    }
}
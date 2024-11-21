package com.aegis.project.dto;

import com.aegis.project.model.FileModel;

public class FileDTO {

    private int fileID;
    private String fileName;
    private String fileType;
    private String fileData;
    public int fileSize;
    private int taskID;
    private int uploaderID;

    public FileDTO(FileModel file) {
        this.fileID = file.getFileID();
        this.fileName = file.getFileName();
        this.fileType = file.getFileType();
        this.fileData = file.getFileData();
        this.fileSize = file.getFileSize();
        this.taskID = file.getTaskID();
        this.uploaderID = file.getUploaderID();
    }

    public FileDTO(int fileID, String fileName, String fileType, /*byte[] fileData,*/ String fileData, int fileSize, int taskID, int uploaderID) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.fileSize = fileSize;
        this.taskID = taskID;
        this.uploaderID = uploaderID;
    }

    public FileDTO() {

    }

    public int getFileID() {
        return this.fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /*public byte[] getFileData() {
        return this.fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }*/
    public String getFileData() {
        return this.fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getUploaderID() {
        return this.uploaderID;
    }

    public void getUploaderID(int uploaderID) {
        this.uploaderID = uploaderID;
    }

}

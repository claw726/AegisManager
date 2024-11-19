package com.aegis.project.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "file")
public class FileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileID;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Lob
    @Column(name = "file_data", columnDefinition = "TEXT")
    private String fileData;

    @Column(name = "task_ID")
    private int taskID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task") // Foreign key column in ProjectModel table
    private TaskModel task;

    @Column(name = "uploader_ID")
    private int uploaderID;

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

    public String getAbbreviatedFileData() {
        if (this.fileData.length() > 100) {
            return this.fileData.substring(0, 100) + "...";
        }
        return this.fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public TaskModel getTask() {
        return this.task;
    }

    public void setTask(TaskModel task) {
        this.task = task;
    }

    public int getUploaderID() {
        return this.uploaderID;
    }

    public void setUploaderID(int uploaderID) {
        this.uploaderID = uploaderID;
    }

    @Override
    public String toString() {
        String ret = "{"
                + "\"fileName\": "
                + this.getFileName()
                + ","
                + "\"fileType\": "
                + this.getFileType()
                + ","
                + "\"fileData\": "
                //+ Arrays.toString(this.getFileData())
                + this.getAbbreviatedFileData()
                + ","
                + "\"taskID\": "
                + this.getTaskID()
                + "}";
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileModel fileModel = (FileModel) o;
        return Objects.equals(fileID, fileModel.getFileID()); // Compare using unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileID);
    }

}

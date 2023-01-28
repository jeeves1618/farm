package org.farmfresh.RESTEndPoints.Service;

import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Entity.UploadInfo;
import org.farmfresh.RESTEndPoints.Repo.UploadServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class UploadService {

    @Autowired
    UploadInfo uploadInfo;

    @Autowired
    UploadServiceRepo uploadServiceRepo;

    DecimalFormat ft = new DecimalFormat("####,##0 KB");

    public void saveUploadInfo(UploadInfo uploadInfo){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter tmf = DateTimeFormatter.ofPattern("HH:MM:SS");
        LocalDateTime today = LocalDateTime.now();
        uploadInfo.setFileSizeFmtd(ft.format(uploadInfo.getFileSize()));
        uploadInfo.setUploadDate(dtf.format(today));
        uploadInfo.setUploadTime(tmf.format(today));
        uploadInfo.setUploadStatus("Uploaded");
        switch (uploadInfo.getFileType()){
            case "M":
            case "Menu":
                uploadInfo.setFileType("Menu");
                break;
            case "I":
            case "Inventory":
                uploadInfo.setFileType("Inventory");
                break;
            default:
                uploadInfo.setFileType("Unknown File Format");
        }
        uploadServiceRepo.save(uploadInfo);
    }

    public List<UploadInfo> findall(){
        return uploadServiceRepo.findAll();
    }
}

package org.farmfresh.RESTEndPoints.Controller;

import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Domain.UIMetaData;
import org.farmfresh.RESTEndPoints.Entity.Menu;
import org.farmfresh.RESTEndPoints.Entity.UploadInfo;
import org.farmfresh.RESTEndPoints.Service.MenuService;
import org.farmfresh.RESTEndPoints.Service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/admin")
public class AdminController {

    @Autowired
    UIMetaData uiMetaData;

    @Autowired
    UploadService uploadService;

    @Autowired
    UploadInfo uploadInfo;

    @Autowired
    MenuService menuService;

    private final String UPLOAD_FILE_PATH = "C:\\Dev\\Sweets\\data\\";
    private final String USER_NAME = System.getProperty("user.name").substring(0,1).toUpperCase()+ System.getProperty("user.name").substring(1);

    @GetMapping(path = "/menumanager")
    public String getHomePage(Model model){
        List<UploadInfo> uploadInfoList = uploadService.findall();
        model.addAttribute("uploadEntries", uploadInfoList);
        model.addAttribute("messagetext", "Please select the type of statement and upload it here");
        model.addAttribute("UIMetaData",uiMetaData);
        return "menumanager";
    }

    @GetMapping(path = "/quotegenerator")
    public String getMenuPage(Model model){
        return "quotegenerator";
    }

    @PostMapping("/menu/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @ModelAttribute("UIMetaData") UIMetaData uiMetaData,
                             RedirectAttributes attributes, Model model) {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "menumanager";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("The File name is : " + UPLOAD_FILE_PATH + fileName + " and the user is " + USER_NAME);
        uploadInfo.setFileUploadKey(null);
        uploadInfo.setFileName(fileName);
        uploadInfo.setContainingFolder(UPLOAD_FILE_PATH);
        uploadInfo.setUploadedBy(USER_NAME);
        uploadInfo.setFileType(uiMetaData.getTypeOfStatement());
        uploadInfo.setFileSize(file.getSize());

        uploadService.saveUploadInfo(uploadInfo);

        //Call getAccountEntries of Statement Service here. The excel to Account Statement Mapping may have to change a bit.

        // save the file on the local file system
        /*
        try {
            Path path = Paths.get(UPLOAD_FILE_PATH + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        // return success response
        //attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        List<Menu> menuServiceEntries = menuService.getEntries(UPLOAD_FILE_PATH + fileName, uiMetaData);
        menuService.saveAccountEntries(menuServiceEntries);

        List<UploadInfo> uploadInfoList = uploadService.findall();
        model.addAttribute("uploadEntries", uploadInfoList);
        model.addAttribute("messagetext", "You successfully uploaded " + fileName + '!');
        return "menumanager";
    }
}

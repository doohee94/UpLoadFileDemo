package com.example.demo.upload.controller;

import com.example.demo.upload.entity.SaveStatus;
import com.example.demo.upload.service.FileUploadService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

import com.example.demo.upload.entity.FileEntity;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UploadFileController {

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/")
    public String openIndex() throws Exception {
        return "redirect:main";
    }

    @RequestMapping(value = "/main")
    public String mainPage() throws Exception {
        return "mainPage";
    }

    //업로드된 파일 저장
    @RequestMapping(value = "saveFile", method = RequestMethod.POST)
    public String saveFile(@RequestPart(required = false) MultipartFile file) throws Exception {

        String sourceFileName = file.getOriginalFilename(); //파일 이름
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase(); //확장자

        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
        File destinationFile = new File("C:/uploadedFile/" + destinationFileName);

        file.transferTo(destinationFile); //파일 저장

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(sourceFileName);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileContentType(file.getContentType());
        fileEntity.setFileFakeName(destinationFileName);
        fileEntity.setAttachmentUrl("C:/uploadedFile/" + destinationFileName);
        fileEntity.setSaveStatus(SaveStatus.UPLOADED);
        fileUploadService.saveFile(fileEntity);

        return "redirect:main";
    }

    //다중 업로드 파일 저장
    @RequestMapping(value = "saveMultiFile", method = RequestMethod.POST)
    public String saveMultiFile(@RequestPart(required = false) List<MultipartFile> files) throws Exception {

        for(MultipartFile file : files){
            String sourceFileName = file.getOriginalFilename(); //파일 이름
            String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase(); //확장자

            String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
            File destinationFile = new File("C:/uploadedFile/" + destinationFileName);

            file.transferTo(destinationFile); //파일 저장

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(sourceFileName);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileContentType(file.getContentType());
            fileEntity.setFileFakeName(destinationFileName);
            fileEntity.setAttachmentUrl("C:/uploadedFile/" + destinationFileName);
            fileEntity.setSaveStatus(SaveStatus.UPLOADED);
            fileUploadService.saveFile(fileEntity);
        }

        return "redirect:main";
    }


    //특정 확장자 파일 저장
    @RequestMapping(value = "uploadSpecificForm", method = RequestMethod.POST)
    public String saveSpecificFile(@RequestPart(required = false) MultipartFile file) throws Exception {

        String sourceFileName = file.getOriginalFilename(); //파일 이름
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase(); //확장자

        if (!sourceFileNameExtension.equals("vcf")) {
            throw new Exception("vcf파일 아님");
        }

        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
        File destinationFile = new File("C:/uploadedFile/" + destinationFileName);

        file.transferTo(destinationFile); //파일 저장

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(sourceFileName);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileContentType(file.getContentType());
        fileEntity.setFileFakeName(destinationFileName);
        fileEntity.setAttachmentUrl("C:/uploadedFile/" + destinationFileName);

        fileUploadService.saveFile(fileEntity);

        return "redirect:main";
    }


    //업로드된 파일 화면에 출력
    @RequestMapping(value = "readFile", method = RequestMethod.POST)
    public ModelAndView readFile(@RequestPart(required = false) MultipartFile file) throws IOException {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("mainPage");

        InputStream inputStream = file.getInputStream();

        BufferedReader  br = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
        String sCurrentLine = null;
        StringBuffer fileContent = new StringBuffer();

        while ((sCurrentLine = br.readLine()) != null) {
            fileContent.append(sCurrentLine + "\n");
        }

        mv.addObject("fileContent", fileContent);
        return mv;
    }

    @RequestMapping(value = "/tolist")
    public ModelAndView getFileList() throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("listPage");

        List<FileEntity> list = fileUploadService.getFileList();

        mv.addObject("list", list);

        return mv;
    }

    //파일 다운로드
    @RequestMapping(value = "/download{fileIdx}")
    public String getFile(@PathVariable("fileIdx") int fileIdx, HttpServletResponse response) throws Exception {

        //db에있는 경로 + 파일 이름 가져오기
        FileEntity fileEntity = fileUploadService.getFile(fileIdx);

        String file = new String(fileEntity.getFileName().getBytes("EUC-KR"), "iso-8859-1");//한글파일명 인코딩

        response.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Type", fileEntity.getFileContentType());
        response.setHeader("Content-Length", "" + fileEntity.getFileSize());
        response.setHeader("Pragma", "no-cache;");
        response.setHeader("Expires", "-1;");

        File readFile = new File(fileEntity.getAttachmentUrl());
        if (!readFile.exists()) {
            throw new RuntimeException("file not found");
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(readFile);
            FileCopyUtils.copy(fis, response.getOutputStream());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {

            }
        }

        return "redirect:tolist";
    }


    //DB에 저장된 파일의 내용 읽기
    @RequestMapping(value = "/read{fileIdx}")
    public ModelAndView getFileContent(@PathVariable("fileIdx") int fileIdx) throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("readPage");
        //db에있는 경로 + 파일 이름 가져오기
        FileEntity fileEntity = fileUploadService.getFile(fileIdx);

        File file = new File(fileEntity.getAttachmentUrl());

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "euc-kr"));
        StringBuffer fileContent = new StringBuffer();
        String sCurrentLine = null;

        while ((sCurrentLine = br.readLine()) != null) {
            fileContent.append(sCurrentLine + "\n");
        }

        mv.addObject("fileContent", fileContent);

        return mv;
    }

    //파일+DB 삭제하기
    @RequestMapping(value = "/delete{fileIdx}")
    public String deleteFile(@PathVariable("fileIdx") int fileIdx) throws Exception {

        FileEntity fileEntity = fileUploadService.getFile(fileIdx);

        File file = new File(fileEntity.getAttachmentUrl());

        if (file.exists()) {
            if (file.delete()) {
                fileUploadService.deleteFile(fileIdx);
            } else {
                throw new Exception("파일 삭제 실패");
            }
        } else {
            fileUploadService.deleteFile(fileIdx);
            throw new Exception("저장된 파일 없음");
        }

        return "redirect:tolist";
    }

    //파일 저장 경로 바꾸기 + DB AttachmentURL도 바꾸기
    @RequestMapping(value = "/changeStatus{fileIdx}")
    public String changeStatusFile(@PathVariable("fileIdx") int fileIdx) throws Exception {

        FileEntity fileEntity = fileUploadService.getFile(fileIdx);

        File file = new File(fileEntity.getAttachmentUrl());
        String deletedPath = "C:/deletedFile/"+fileEntity.getFileFakeName();

        if (file.exists()) {

            if(file.renameTo(new File(deletedPath))){ //폴더 이동
                fileEntity.setSaveStatus(SaveStatus.DELETED);
                fileEntity.setAttachmentUrl(deletedPath);
                fileUploadService.changeStatus(fileEntity);
            }else{
                throw new Exception("파일 이동 실패");
            }

        } else {
            throw new Exception("저장된 파일 없음");
        }



        return "redirect:tolist";
    }

}

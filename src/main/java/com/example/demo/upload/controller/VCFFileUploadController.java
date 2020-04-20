package com.example.demo.upload.controller;
import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PresetEntity;
import com.example.demo.upload.entity.dto.*;
import com.example.demo.upload.service.VCFFileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/onco/repository")
@RequiredArgsConstructor
public class VCFFileUploadController {
    private final VCFFileUploadService vcfFileUploadService;

    @PostMapping(value = "/fileUpload") //VCF파일 업로드 및 DB저장
    public void fileUpload(@RequestPart(required = false) MultipartFile file, HttpServletResponse response) throws Exception {
        vcfFileUploadService.fileUpload(file);
        response.sendRedirect("/main");
    }//end AllView

    @GetMapping(value = "/VCFFileList")//유저아이디에 해당하는 VCF파일 목록 불러오기
    public ResponseEntity vcfFileList(int personId, Pageable pageable) throws Exception {
        Page<FileEntity> fileEntityList = vcfFileUploadService.vcfFileList(personId, pageable);
        return new ResponseEntity(fileEntityList, HttpStatus.OK);
    }

    @GetMapping(value = "/presetList")
    public PresetDto.ListResponse savePresetDB(int personId){

        List<PresetEntity> presetList = vcfFileUploadService.getPresetList(personId);

        return new PresetDto.ListResponse(presetList);
    }

    @GetMapping(value = "/analyze") //VCF파일 ID에 해당하는 파일 찾아서 DB 선택 후 ANNOVAR 파일 만들기
    public void analyze(int vcfFileIdx, String[] dbList ) throws Exception {
        //dbList View Name으로 가져와야됨 ex) hg19_@@@
        vcfFileUploadService.analyze(Arrays.asList(dbList), vcfFileIdx);
    }

    @GetMapping(value = "/analyzedList")//ANNOVAR파일 필터해서 가져오기
    public ResponseEntity getAnalyzedList(int vcfFileIdx,FilterList filterList, String[] selectedHeaders, Pageable pageable) throws Exception{

        Page vcfLines = vcfFileUploadService.getAnalyzedList(filterList,Arrays.asList(selectedHeaders),vcfFileIdx,pageable);
        return new ResponseEntity(vcfLines,HttpStatus.OK);
    }

    @GetMapping(value="/annovarInformation")
    public AnnovarDBInformationDto.ListResponse getAnnovarInformation(int vcfFileIdx, String[] selectedHeaders,Pageable pageable) throws Exception{
        return new AnnovarDBInformationDto.ListResponse(vcfFileUploadService.getAnnovarInformation(vcfFileIdx,Arrays.asList(selectedHeaders),pageable));
    }
}

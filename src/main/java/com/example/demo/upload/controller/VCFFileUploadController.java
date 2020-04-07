package com.example.demo.upload.controller;

import com.example.demo.upload.common.PaginationUtil;
import com.example.demo.upload.entity.ConvertFileEntity;
import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.dto.Filter;
import com.example.demo.upload.entity.dto.Header;
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
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VCFFileUploadController {
    private final VCFFileUploadService vcfFileUploadService;

    @PostMapping(value = "/fileUpload") //VCF파일 업로드 및 DB저장
    public void fileUpload(@RequestPart(required = false) MultipartFile file, HttpServletResponse response) throws Exception {
        vcfFileUploadService.fileUpload(file);
        response.sendRedirect("/main");
    }//end AllView

    @GetMapping(value = "/VCFFileList")//유저아이디에 해당하는 VCF파일 목록 불러오기
    public ResponseEntity VCFfileList(Pageable pageable) throws Exception {
        //유저 아이디 가져오기, 지금은 일단 2번
        int personId = 2;
        Page<FileEntity> fileEntityList = vcfFileUploadService.VCFfileList(personId, pageable);
        return new ResponseEntity(fileEntityList, HttpStatus.OK);
    }

    @GetMapping(value = "/VCFFileConvert") //VCF파일 ID에 해당하는 파일 찾아서 ANNOVAR 파일 만들기
    public void VCFFileConvert() throws Exception {
        //파일 아이디 가져오기, 지금은 일단 1번
        int VCFFileIdx = 1;
        //사용자로부터 받을 파일 이름
        String inputFileName = "TEST";
        vcfFileUploadService.VCFFileConvert(VCFFileIdx, inputFileName);
    }

    @GetMapping(value = "/VCFFileConvertSelectDB") //VCF파일 ID에 해당하는 파일 찾아서 DB 선택 후 ANNOVAR 파일 만들기
    public void VCFFileConvertSelectDB() throws Exception {
        //파일 아이디 가져오기, 지금은 일단 6번
        int VCFFileIdx = 6;
        //사용자로부터 받을 파일 이름
        String inputFileName = "SelectDBTEST";
        //사용자가 선택한 DB목록
        List<String> dbList = new ArrayList<>();
        dbList.add("refGene");

        vcfFileUploadService.VCFFileConvertSelectDB(dbList,VCFFileIdx, inputFileName);
    }

    @GetMapping(value = "/annovarFileList") //VCF파일ID에 해당하는 ANNOVAR파일 목록 불러오기
    public ResponseEntity annovarFileList(Pageable pageable) throws Exception{
        //파일 아이디 가져오기, 지금은 일단 1번
        int VCFFileIdx = 1;
        Page<ConvertFileEntity> list = vcfFileUploadService.convertFileList(VCFFileIdx,pageable);
        return new ResponseEntity(list,HttpStatus.OK);
    }

    @GetMapping(value = "/convertFile") //ANNOVAR파일 필터링 없이 가져오기
    public ResponseEntity getConvertFile(Pageable pageable) throws Exception{
        int convertFileIdx = 16;
        //처음 파일 가져올때 보이는 기본 값들
        List<String> selectedHeaders = new ArrayList<>();
        selectedHeaders.add("Chr");
        selectedHeaders.add("Start");
        selectedHeaders.add("End");
        selectedHeaders.add("Ref");
        selectedHeaders.add("Alt");

        Page vcfLines = vcfFileUploadService.getConvertFile(convertFileIdx,selectedHeaders,pageable);
        return new ResponseEntity(vcfLines,HttpStatus.OK);
    }

    @GetMapping(value = "/filteredList")//ANNOVAR파일 필터해서 가져오기
    public ResponseEntity getFilteredList(Pageable pageable) throws Exception{
        int convertFileIdx = 16;
        //사용자가 선택한 필터
        List<Filter> filters = new ArrayList<>();
//        filters.add( new Filter("ljb23_sift","<","0.05"));
//        filters.add( new Filter("Ref","=","G"));
        filters.add( new Filter("Chr","=","1"));
        filters.add( new Filter("Start","=","977390"));

        //사용자가 선택한 헤더리스트로 가져와야함
        List<String> selectedHeaders = new ArrayList<>();
        selectedHeaders.add("Chr");
        selectedHeaders.add("Start");
        selectedHeaders.add("End");
        selectedHeaders.add("Ref");
        selectedHeaders.add("Alt");

        Page vcfLines = vcfFileUploadService.getFilteredList(filters,selectedHeaders,convertFileIdx,pageable);
        return new ResponseEntity(vcfLines,HttpStatus.OK);
    }


    @GetMapping(value ="/basicInfo")
    public ResponseEntity getBasicInfo(Pageable pageable) throws Exception{
        int VCFFileIdx = 2;
        List<String> selectedHeaders = new ArrayList<>();
        selectedHeaders.add("Chr");
        selectedHeaders.add("Start");
        selectedHeaders.add("End");
        selectedHeaders.add("Ref");
        selectedHeaders.add("Alt");
        Page vcfLines = vcfFileUploadService.getBasicInfo(VCFFileIdx, selectedHeaders,pageable);

        return new ResponseEntity(vcfLines, HttpStatus.OK);
    }

    @GetMapping(value ="/selectByHeader")
    public ResponseEntity getSelectByHeader(Pageable pageable) throws Exception{
        int convertFileIdx = 16;

        List<String> selectedHeaders = new ArrayList<>();
        selectedHeaders.add("Chr");
        selectedHeaders.add("Start");
        selectedHeaders.add("End");
        selectedHeaders.add("TAL");
        selectedHeaders.add("ljb23_sift");

        Page vcfLines = vcfFileUploadService.getSelectByHeader(convertFileIdx, selectedHeaders,pageable);

        return new ResponseEntity(vcfLines, HttpStatus.OK);
    }

    @GetMapping(value ="/basicInfoHeader")
    public ResponseEntity getBasicInfoHeader(Pageable pageable) throws Exception {
        List<String> selectedHeaders = new ArrayList<>();
        selectedHeaders.add("Chr");
        selectedHeaders.add("Start");
        selectedHeaders.add("End");
        selectedHeaders.add("Ref");
        selectedHeaders.add("Alt");

        List<Header> list = vcfFileUploadService.getBasicInfoHeader(selectedHeaders, pageable);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping(value = "/headers")
    public ResponseEntity getHeaders(Pageable pageable) throws Exception {
        int convertFileIdx = 16;
        //처음 파일 가져올때 보이는 기본 값들
        List<String> selectedHeaders = new ArrayList<>();
        selectedHeaders.add("Chr");
        selectedHeaders.add("Start");
        selectedHeaders.add("End");
        selectedHeaders.add("Ref");
        selectedHeaders.add("Alt");
        selectedHeaders.add("TAL");
        selectedHeaders.add("TCGA_CODE");

        List<Header> list = vcfFileUploadService.getHeaders(convertFileIdx,selectedHeaders,pageable);

        return new ResponseEntity(list, HttpStatus.OK);
    }

}

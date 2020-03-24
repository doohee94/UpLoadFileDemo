package com.example.demo.upload.controller;

import com.example.demo.upload.common.PaginationUtil;
import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PersonEntity;
import com.example.demo.upload.entity.SaveStatus;
import com.example.demo.upload.entity.dto.Cols;
import com.example.demo.upload.entity.dto.Filter;
import com.example.demo.upload.entity.dto.TableAnnotation;
import com.example.demo.upload.service.APITestService;
import com.example.demo.upload.service.FileUploadService;
import htsjdk.samtools.util.BufferedLineReader;
import htsjdk.tribble.readers.AsciiLineReader;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APITestController {

    @Autowired
    APITestService apiTestService;

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public void fileUpload(@RequestPart(required = false) MultipartFile file, HttpServletResponse response) throws Exception {
        //1. 업로드된 VCF파일 저장
        String sourceFileName = file.getOriginalFilename(); //파일 이름
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase(); //확장자

        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
        File destinationFile = new File("C:/uploadedFile/" + destinationFileName);
        file.transferTo(destinationFile); //파일 저장

        if (!sourceFileNameExtension.equals("vcf")) {
            throw new Exception("vcf파일 아님");
        }

        FileEntity.FileEntityBuilder builder = FileEntity.builder();
        builder
                .fileName(sourceFileName)
                .fileSize(file.getSize())
                .fileContentType(file.getContentType())
                .fileFakeName(destinationFileName)
                .attachmentUrl("C:/uploadedFile/" + destinationFileName)
                .saveStatus(SaveStatus.UPLOADED)
                .personId(((int) (Math.random() * 10) + 1));
        FileEntity fileEntity = builder.build();

        apiTestService.saveOriginFile(fileEntity);

        response.sendRedirect("/api/VCFFileList");

    }//end AllView

    @RequestMapping(value = "/VCFFileList")
    public ResponseEntity VCFfileList(Pageable pageable) throws Exception {
        //유저 아이디 가져오기, 지금은 일단 2번
        int personId = 2;
        Page<FileEntity> fileEntityList = apiTestService.getFileListByPersonId(personId, pageable);
        return new ResponseEntity(fileEntityList, HttpStatus.OK);
    }

    @RequestMapping(value = "/VCFFileConvert")
    public ResponseEntity VCFFileConvert(Pageable pageable) throws Exception {
        //파일 아이디 가져오기, 지금은 일단 1번
        int fileIdx = 1;

        //파일 ID에 따라 convert된 파일들 만들기 만들어진 파일이 5개 이상이면 막기
        Long convertFileCount = apiTestService.getConvertFileCount(fileIdx);
        if(convertFileCount >=5) throw new Exception("파일이 5개 이상");

        //사용자로부터 받을 파일 ID
        String inputFileName = "TEST";

        FileEntity fileEntity = apiTestService.getFileByFileIdx(fileIdx);
        tableAnnovar(fileEntity.getFileFakeName());

        Path path = Paths.get("C:/convertFile/" + fileEntity.getFileFakeName() + ".hg19_multianno.txt");
        List<String> fileContentList = Files.readAllLines(path);

        deleteFiles(fileEntity.getFileFakeName());


        return new ResponseEntity("", HttpStatus.OK);
    }

    /**
     * methods
     * */
    //table_annvar실행
    private void tableAnnovar(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl ");
        list.add("C:/annovar/table_annovar.pl ");
        list.add("C:/uploadedFile/" + vcfName + " ");
        list.add("C:/annovar/humandb/ ");
        list.add("-buildver ");
        list.add("hg19 ");
        list.add("-outfile ");
        list.add("C:/convertFile/" + vcfName + " ");
        list.add("-remove ");
        list.add("-protocol ");
        list.add("refGene,");
        list.add("ljb23_sift,");
        list.add("ljb23_pp2hvar,");
        list.add("clinvar_20190305,");
        list.add("1000g2015aug_all,");
        list.add("exac03,");
        list.add("esp6500si_all ");
        list.add("-operation ");
        list.add("g,f,f,f,f,f,f ");
        list.add("-nastring ");
        list.add(". ");
        list.add("-vcfinput ");
        list.add("-polish ");
        list.add("-xref ");
        list.add("C:/annovar/example/gene_xref.txt");

        String line = "";

        for (int i = 0; i < list.size(); i++) {
            line += list.get(i);
        }
        System.out.println(">>" + line);
        CommandLine commandLine = CommandLine.parse(line);
        Executor executor = new DefaultExecutor();
        try {
            executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }

    //생성된 파일 삭제
    private void deleteFiles(String fakeName) throws Exception {

        File deleteVcf = new File("C:/convertFile/" + fakeName + ".hg19_multianno.vcf");
        File deleteAvinput = new File("C:/convertFile/" + fakeName + ".avinput");

        if (deleteVcf.exists() && deleteAvinput.exists()) {

            if (deleteVcf.delete()) System.out.println("VCF파일삭제 성공");
            else throw new Exception("VCF파일 삭제 실패");

            if (deleteAvinput.delete()) System.out.println("avinput파일삭제 성공");
            else throw new Exception("avinput파일 삭제 실패");


        } else {
            throw new Exception("저장된 파일 없음");
        }
    }

}

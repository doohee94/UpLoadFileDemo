package com.example.demo.upload.controller;

import com.example.demo.upload.entity.PersonEntity;
import com.example.demo.upload.entity.SaveStatus;
import com.example.demo.upload.entity.dto.Clinvar;
import com.example.demo.upload.entity.dto.SIFT;
import com.example.demo.upload.entity.dto.TotalAnnotation;
import com.example.demo.upload.service.FileUploadService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import htsjdk.samtools.util.BufferedLineReader;
import htsjdk.samtools.util.LineReader;
import htsjdk.tribble.readers.AsciiLineReader;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;
import org.apache.commons.exec.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.jni.Proc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

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
        fileEntity.setPersonEntity(new PersonEntity((int) (Math.random() * 10) + 1));
        fileUploadService.saveFile(fileEntity);

        return "redirect:main";
    }

    //다중 업로드 파일 저장
    @RequestMapping(value = "saveMultiFile", method = RequestMethod.POST)
    public String saveMultiFile(@RequestPart(required = false) List<MultipartFile> files) throws Exception {

        for (MultipartFile file : files) {
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
            fileEntity.setPersonEntity(new PersonEntity((int) (Math.random() * 10) + 1));
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

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));
        String sCurrentLine = null;
        StringBuffer fileContent = new StringBuffer();

        while ((sCurrentLine = br.readLine()) != null) {
            fileContent.append(sCurrentLine + "\n");
        }

        mv.addObject("fileContent", fileContent);
        return mv;
    }

    //VcfFileReader 사용해서 화면에 보여주기
    @RequestMapping(value = "vcfReadFile", method = RequestMethod.POST)
    public ModelAndView readVcfFile(@RequestPart(required = false) MultipartFile file) throws IOException {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("mainPage");

        InputStream inputStream = file.getInputStream();

        BufferedLineReader br = new BufferedLineReader(inputStream);
        AsciiLineReader ar = new AsciiLineReader(inputStream);
        htsjdk.tribble.readers.LineReader lr2 = ar;
        LineIteratorImpl t = new LineIteratorImpl(lr2);

        VCFCodec codec = new VCFCodec();
        codec.readActualHeader(t);
        VariantContext ctx = null;
        int i = 0;
        while (t.hasNext()) {
            i++;
            if (i < 350) continue;
            ctx = codec.decode(t.next());

            System.out.println("-----------------------------------------------");
            System.out.println(i + "toStringDecodeGenotypes>>" + ctx.toStringDecodeGenotypes());
            System.out.println(i + "toStringWithoutGenotypes>>" + ctx.toStringWithoutGenotypes());

            if (i == 450) break;
        }
        br.close();


        String fileContent = "";
        mv.addObject("fileContent", fileContent);
        return mv;
    }


    //vcf파일을 받아서 avinput으로 바꿔서 annotate_variation 돌려서 파일 만들기
    @RequestMapping(value = "ANNOVAR", method = RequestMethod.POST)
    public ModelAndView annovar(@RequestPart(required = false) MultipartFile file) throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("annovarExecPage");

        //1. 업로드된 VCF파일 저장
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
        fileEntity.setPersonEntity(new PersonEntity((int) (Math.random() * 10) + 1));

        fileUploadService.saveFile(fileEntity);
        int fileIdx = fileUploadService.getFileByFileFakeName(fileEntity.getFileFakeName()).getFileIdx();

        //2. 저장된 VCF파일의 경로와 파일 이름을 보내서 avinput 파일 생성
        convert2annovar(fileEntity.getAttachmentUrl(), fileEntity.getFileFakeName());

        //3. avinput파일을 이용해서 SIFT 파일 생성
        SIFTannotation(fileEntity.getFileFakeName());

        File droppedFile = new File("C:/convertFile/" + fileEntity.getFileFakeName() + ".hg19_ljb23_sift_dropped");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(droppedFile), "euc-kr"));
        String sCurrentLine = null;
        List<TotalAnnotation> list = new ArrayList<>();
        List<String> cols = getCols(0);
        while ((sCurrentLine = br.readLine()) != null) {
            String[] temp = sCurrentLine.split("\t");
            TotalAnnotation tempSIFT = new TotalAnnotation(temp[0], new BigDecimal(temp[1]), temp[2],
                    Long.parseLong(temp[3]), Long.parseLong(temp[4]),
                    temp[5], temp[6], temp[7], temp[8], temp[9]);

            list.add(tempSIFT);
        }

        br.close();
        //생성된 파일 삭제
        File filteredFile = new File("C:/convertFile/" + fileEntity.getFileFakeName() + ".hg19_ljb23_sift_filtered");
        File logFile = new File("C:/convertFile/" + fileEntity.getFileFakeName() + ".log");
        if (droppedFile.exists() && filteredFile.exists() && logFile.exists()) {

            if (droppedFile.delete()) System.out.println("dropped파일삭제 성공");
            else throw new Exception("dropped파일 삭제 실패");

            if (filteredFile.delete()) System.out.println("filtered파일삭제 성공");
            else throw new Exception("filtered파일 삭제 실패");

            if (logFile.delete()) System.out.println("log파일삭제 성공");
            else throw new Exception("filtered파일 삭제 실패");

        } else {
            throw new Exception("저장된 파일 없음");
        }

        mv.addObject("id", 0);
        mv.addObject("cols", cols);
        mv.addObject("fileIdx", fileIdx);
        mv.addObject("list", list);
        return mv;
    }

    @RequestMapping(value = "/annoSelect")
    public ModelAndView annoSelect(int id, int fileIdx) throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("annovarExecPage");
        FileEntity fileEntity = fileUploadService.getFile(fileIdx);
        String extension = "";
        String otherExtension = "";
        String logExtension = ".log";

        switch (id) {
            case 0: //SIFT
                SIFTannotation(fileEntity.getFileFakeName());
                extension = ".hg19_ljb23_sift_dropped";
                otherExtension = ".hg19_ljb23_sift_filtered";
                break;
            case 1: //PolyPhen
                PolyPhen2Annotation(fileEntity.getFileFakeName());
                extension = ".hg19_ljb23_pp2hvar_dropped";
                otherExtension = ".hg19_ljb23_pp2hvar_filtered";
                break;
            case 2: //refGene All
                refGeneAllAnnotation(fileEntity.getFileFakeName());
                extension = ".avinput.variant_function";
                otherExtension = ".avinput.exonic_variant_function";
                logExtension = ".avinput.log";
                break;
            case 3: //refGene Exonic
                refGeneExonicAnnotation(fileEntity.getFileFakeName());
                extension = ".avinput.exonic_variant_function";
                otherExtension = ".avinput.variant_function";
                logExtension = ".avinput.log";
                break;
            case 4: //ClinVar
                ClinVarAnnotation(fileEntity.getFileFakeName());
                extension = ".hg19_clinvar_20150629_dropped";
                otherExtension = ".hg19_clinvar_20150629_filtered";
                break;
            case 5: //1000Genome
                Genenome1000Annotation(fileEntity.getFileFakeName());
                extension = ".hg19_all.sites.2015_08_dropped";
                otherExtension = ".hg19_all.sites.2015_08_filtered";
                break;
            case 6: //ExAC
                ExACAnnotation(fileEntity.getFileFakeName());
                extension = ".hg19_exac03_dropped";
                otherExtension = ".hg19_exac03_filtered";
                break;
            case 7: //ESP6500
                Esp6500Annotation(fileEntity.getFileFakeName());
                extension = ".hg19_esp6500si_all_dropped";
                otherExtension = ".hg19_esp6500si_all_filtered";
                break;

        }//end switch


        File droppedFile = new File("C:/convertFile/" + fileEntity.getFileFakeName() + extension);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(droppedFile), "euc-kr"));
        StringBuffer fileContent = new StringBuffer();
        String sCurrentLine = null;
        List<String> cols = getCols(id);
        List<TotalAnnotation> list = new ArrayList<>();

        while ((sCurrentLine = br.readLine()) != null) {
            TotalAnnotation tempAnno = null;
            String[] temp = sCurrentLine.split("\t");

            if (id == 0 || id == 1 || id == 5 || id == 6 || id == 7) {
                tempAnno = new TotalAnnotation(temp[0], new BigDecimal(temp[1]), temp[2],
                        Long.parseLong(temp[3]), Long.parseLong(temp[4]),
                        temp[5], temp[6], temp[7], temp[8], temp[9]);

            } else if (id == 3) { //refgene exonic
                tempAnno = new TotalAnnotation(temp[0], temp[1], temp[2], temp[3], Long.parseLong(temp[4]),
                        Long.parseLong(temp[5]), temp[6], temp[7], temp[8],
                        temp[9], temp[10]);
                tempAnno.setGeneList(Arrays.asList(temp[2].split(",")));
            } else if (id == 2 || id == 4) { //clinvar / refGene_All
                tempAnno = new TotalAnnotation(temp[0], temp[1], temp[2],
                        Long.parseLong(temp[3]), Long.parseLong(temp[4]),
                        temp[5], temp[6], temp[7], temp[8], temp[9]);
                if (id == 4) {
                    List<String> templsit = Arrays.asList(temp[1].split(";"));
                    Map<String, String> map = new HashMap<>();
                    for (int i = 0; i < templsit.size(); i++) {
                        map.put(templsit.get(i).split("=")[0], templsit.get(i).split("=")[1]);
                    }
                    tempAnno.setClinvarDescriptionMap(map);
                }// end clinvar

                if(id == 2){
                    tempAnno.setGeneList(Arrays.asList(temp[1].split(",")));
                }
            }
            list.add(tempAnno);
        }//end while
        br.close();

        //생성된 파일 삭제
        File filteredFile = new File("C:/convertFile/" + fileEntity.getFileFakeName() + otherExtension);
        File logFile = new File("C:/convertFile/" + fileEntity.getFileFakeName() + logExtension);
        if (droppedFile.exists() && filteredFile.exists()) {

            if (droppedFile.delete()) System.out.println("파일삭제 성공");
            else throw new Exception("dropped파일 삭제 실패");

            if (filteredFile.delete()) System.out.println("파일삭제 성공");
            else throw new Exception("filtered파일 삭제 실패");

            if (logFile.delete()) System.out.println("log파일삭제 성공");
            else throw new Exception("filtered파일 삭제 실패");

        } else {

            throw new Exception("저장된 파일 없음");
        }

        mv.addObject("id", id);
        mv.addObject("fileIdx", fileIdx);
        mv.addObject("fileContent", fileContent);
        mv.addObject("cols", cols);
        mv.addObject("list", list);
        return mv;
    }


    //vcfanno 파일 실행하기
    @RequestMapping(value = "/annovarExec")
    public String execANNFile() throws Exception {

        vcfanno("", "");


        return "annovarExecPage";
    }


    @RequestMapping(value = "/tolist")
    public ModelAndView getFileList(@PageableDefault(sort = {"fileIdx"}, direction = Sort.Direction.ASC, size = 3) Pageable pageable) throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("listPage");

        Page<FileEntity> page = fileUploadService.getFileList(pageable);
        mv.addObject("page", page);

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
        String deletedPath = "C:/deletedFile/" + fileEntity.getFileFakeName();

        if (file.exists()) {

            if (file.renameTo(new File(deletedPath))) { //폴더 이동
                fileEntity.setSaveStatus(SaveStatus.DELETED);
                fileEntity.setAttachmentUrl(deletedPath);
                fileUploadService.changeStatus(fileEntity);
            } else {
                throw new Exception("파일 이동 실패");
            }

        } else {
            throw new Exception("저장된 파일 없음");
        }

        return "redirect:tolist";
    }


    //한사람당 가지고 있는 파일 목록 가져오기
    @RequestMapping(value = "/personFileList{personId}")
    public ModelAndView getPersonFileList(@PathVariable int personId) throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("personPage");

        List<FileEntity> fileList = null;
        if (personId == 0) {
            fileList = fileUploadService.getAllFileList();
        } else {
            fileList = fileUploadService.getFileListByPersonId(personId);
        }

        List<PersonEntity> personList = fileUploadService.getPersonList();

        mv.addObject("selectedPerson", personId);
        mv.addObject("fileList", fileList);
        mv.addObject("personList", personList);

        return mv;
    }


    /**
     * methods
     */
    private void refGeneExonicAnnotation(String vcfName) {
        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-geneanno");
        list.add("-dbtype");
        list.add("refGene");
        list.add("-buildver");
        list.add("hg19");
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");

        String line = "";

        for (int i = 0; i < list.size(); i++) {
            line += list.get(i) + " ";
        }

        CommandLine commandLine = CommandLine.parse(line);
        Executor executor = new DefaultExecutor();

        System.out.println("line : " + line);

        try {
            int exitValue = executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    private void refGeneAllAnnotation(String vcfName) {
        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-geneanno");
        list.add("-dbtype");
        list.add("refGene");
        list.add("-buildver");
        list.add("hg19");
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");

        String line = "";

        for (int i = 0; i < list.size(); i++) {
            line += list.get(i) + " ";
        }

        CommandLine commandLine = CommandLine.parse(line);
        Executor executor = new DefaultExecutor();

        System.out.println("line : " + line);

        try {
            int exitValue = executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    private void convert2annovar(String vcfPath, String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/convert2annovar.pl");
        list.add("-format");
        list.add("vcf4");
        list.add(vcfPath);
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName + ".avinput");

        String line = "";

        for (int i = 0; i < list.size(); i++) {
            line += list.get(i) + " ";
        }

        CommandLine commandLine = CommandLine.parse(line);
        Executor executor = new DefaultExecutor();

        System.out.println("line : " + line);

        try {
            int exitValue = executor.execute(commandLine);

        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }

    private void vcfanno(String vcfPath, String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("cmd.exe");
        list.add("cd");
        list.add("C:/vcfanno");
        list.add("&&");
        list.add("vcfanno");
        list.add("-p");
        list.add("4");
        list.add("-lua");
        list.add("example/custom.lua");
        list.add("example/conf.toml");
        list.add("C:/annovar/tumor_sample.BRCA.vcf");
        list.add(">");
        list.add("C:/annovar/vcfannoTest.vcf");


        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }

    private void SIFTannotation(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-filter");
        list.add("-dbtype");
        list.add("ljb23_sift");
        list.add("-buildver");
        list.add("hg19");
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName);
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");

        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }//end method

    private void PolyPhen2Annotation(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-filter");
        list.add("-dbtype");
        list.add("ljb23_pp2hvar");
        list.add("-buildver");
        list.add("hg19");
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName);
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");

        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }//end method

    private void ClinVarAnnotation(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");
        list.add("-filter");
        list.add("-dbtype");
        list.add("clinvar_20150629");
        list.add("-buildver");
        list.add("hg19");
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName);


        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }


    }//end method

    private void Genenome1000Annotation(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-filter");
        list.add("-dbtype");
        list.add("1000g2015aug_all");
        list.add("-buildver");
        list.add("hg19");
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName);
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");

        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }//end method

    private void ExACAnnotation(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-filter");
        list.add("-build");
        list.add("hg19");
        list.add("-dbtype");
        list.add("exac03");
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName);

        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }//end method

    private void Esp6500Annotation(String vcfName) {

        List<String> list = new ArrayList<>();
        list.add("perl");
        list.add("C:/annovar/annotate_variation.pl");
        list.add("-filter");
        list.add("-dbtype");
        list.add("esp6500si_all");
        list.add("-build");
        list.add("hg19");
        list.add("-outfile");
        list.add("C:/convertFile/" + vcfName);
        list.add("C:/convertFile/" + vcfName + ".avinput");
        list.add("C:/annovar/humandb/");

        try {
            Process process = new ProcessBuilder(list).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "euc-kr"));
            String sCurrentLine = null;
            StringBuffer fileContent = new StringBuffer();

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent.append(sCurrentLine + "\n");
            }


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }//end method

    List<String> getCols(int id) {
        List<String> cols = new ArrayList<>();
        ;

        if (id == 0 || id == 1) { //SIFT, PolyPhen
            cols.add("annotaionType");
            cols.add("score");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (id == 5 || id == 6 || id == 7) { //Genome1000, Exac, ESP
            cols.add("annotationType");
            cols.add("alleleFrequency");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (id == 2) { //RefGene_All
            cols.add("All");
            cols.add("geneName");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (id == 3) { //RefGene_Exonic
            cols.add("line");
            cols.add("geneType");
            cols.add("geneNames");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (id == 4) { //Clinvar
            cols.add("annotationType");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
            cols.add("type");
            cols.add("clinvarDescription");
        }

//        cols.add("t1");
//        cols.add("t2");
//        cols.add("t3");

        return cols;
    }


}//end class

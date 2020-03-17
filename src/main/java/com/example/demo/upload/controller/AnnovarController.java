package com.example.demo.upload.controller;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PersonEntity;
import com.example.demo.upload.entity.SaveStatus;
import com.example.demo.upload.entity.dto.*;
import com.example.demo.upload.service.FileUploadService;
import javafx.util.Builder;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sun.security.x509.AttributeNameEnumeration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/annovar")
public class AnnovarController {

    @Autowired
    FileUploadService fileUploadService;

    //vcf파일을 받아서 avinput으로 바꿔서 annotate_variation 돌려서 파일 만들기
    @RequestMapping(value = "ANNOVAR", method = RequestMethod.POST)
    public String annovar(@RequestPart(required = false) MultipartFile file) throws Exception {
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

        return "redirect:/annovar/annoSelect?dbType=ljb23_sift&fileIdx=" + fileIdx;
    }

    @RequestMapping(value = "/annoSelect")
    public ModelAndView annoSelect(String dbType, int fileIdx) throws Exception {

        FileEntity fileEntity = fileUploadService.getFile(fileIdx);

        String extension = getExtension(dbType);
        String otherExtension = getOtherExtension(dbType);
        String logExtension = getLogExtension(dbType);

        annotation(fileEntity.getFileFakeName(), dbType);

        List<String> cols = getCols(dbType);

        List<TotalAnnotation> list = new ArrayList<>();
        Path path = Paths.get("C:/convertFile/" + fileEntity.getFileFakeName() + extension);
        List<String> fileContentList = Files.readAllLines(path);

        for (String content : fileContentList) {

            TotalAnnotation.TotalAnnotationBuilder tempAnnoBuilder = TotalAnnotation.builder();

            String[] temp = content.split("\t");

            if (dbType.equals("ljb23_sift") || dbType.equals("ljb23_pp2hvar") || dbType.equals("1000g2015aug_all")
                    || dbType.equals("exac03") || dbType.equals("esp6500si_all")) {

                tempAnnoBuilder
                        .dbType(dbType)
                        .annotationType(temp[0])
                        .score(new BigDecimal(temp[1]))
                        .chromNum(temp[2])
                        .start(Long.parseLong(temp[3]))
                        .end(Long.parseLong(temp[4]))
                        .ref(temp[5])
                        .alt(temp[6]);

            } else if (dbType.equals("refGeneExonic")) { //refgene exonic

                tempAnnoBuilder
                        .dbType(dbType)
                        .line(temp[0])
                        .geneType(temp[1])
                        .geneNames(temp[2])
                        .geneList(temp[2])
                        .chromNum(temp[3])
                        .start(Long.parseLong(temp[4]))
                        .end(Long.parseLong(temp[5]))
                        .ref(temp[6])
                        .alt(temp[7]);


            } else if (dbType.equals("refGeneAll") || dbType.equals("clinvar_20150629") || dbType.equals("snp138")) {

                if (dbType.equals("clinvar_20150629")) {
                    tempAnnoBuilder.clinvarDescriptionMap(temp[1]);
                }
                if (dbType.equals("refGeneAll") || dbType.equals("snp138")) {
                    tempAnnoBuilder.geneList(temp[1]);
                }

                tempAnnoBuilder
                        .dbType(dbType)
                        .annotationType(temp[0])
                        .clinvarDescription(temp[1])
                        .chromNum(temp[2])
                        .start(Long.parseLong(temp[3]))
                        .end(Long.parseLong(temp[4]))
                        .ref(temp[5])
                        .alt(temp[6]);
            }

            TotalAnnotation tempAnno = tempAnnoBuilder.build();
            list.add(tempAnno);
        }//end for

        deleteFiles(extension, otherExtension, logExtension, fileEntity.getFileFakeName());

        ModelAndView mv = new ModelAndView();
        mv.setViewName("annovarExecPage");
        mv.addObject("dbType", dbType);
        mv.addObject("fileIdx", fileIdx);
        mv.addObject("cols", cols);
        mv.addObject("list", list);
        return mv;
    }

    @RequestMapping(value = "/AllannoSelect")
    public ModelAndView AllannoSelect(String dbType, int fileIdx) throws Exception {

        FileEntity fileEntity = fileUploadService.getFile(fileIdx);

        //DB List
        List<String> dbList = new ArrayList<>();
        dbList.add("refGeneAll");
        dbList.add("refGeneExonic");
        dbList.add("ljb23_sift");
        dbList.add("ljb23_pp2hvar");
        dbList.add("clinvar_20150629");
        dbList.add("1000g2015aug_all");
        dbList.add("exac03");
        dbList.add("esp6500si_all");

        Map<Long, AllAnnotation> map = new HashMap<>();
        String extension = "";
        String otherExtension = "";
        String logExtension = "";

        List<String> cols = getCols(dbType);

        for (String DBs : dbList) {
            System.out.println("DBs>>"+DBs);
            annotation(fileEntity.getFileFakeName(), DBs);
            extension = getExtension(DBs);
            otherExtension = getOtherExtension(DBs);
            logExtension = getLogExtension(DBs);
            map = getAllMap(map, fileEntity.getFileFakeName(), extension, DBs);

        }//end

        List<AllAnnotation> list = new ArrayList<>(map.values()); //원래꺼
//
//        List<AllAnnotation> list = new ArrayList<>(map.values())
//                .stream()
//                .filter(all -> all.getGenome1000() != null)
//                .filter(all -> all.getExac() != null)
//                .filter(all -> all.getEsp6500() != null)
//                .filter(all -> all.getGenome1000().getAlleleFrequency().compareTo(BigDecimal.valueOf(0.01)) == -1) //기준보다 작은경우 -1/ 큰경우 1/ 같은경우 0
//                .filter(all -> all.getExac().getAlleleFrequency().compareTo(BigDecimal.valueOf(0.01)) == -1)
//                .filter(all -> all.getEsp6500().getAlleleFrequency().compareTo(BigDecimal.valueOf(0.01)) == -1)
//                .collect(Collectors.toList());
//
//        Stream<AllAnnotation> t1 = list2.stream()
//                .filter(all -> all.getGenome1000() != null)
//                .filter(all -> all.getGenome1000().getAlleleFrequency().compareTo(BigDecimal.valueOf(0.01)) == -1);
//
//        Stream<AllAnnotation> t2 = t1
//                .filter(all -> all.getExac() != null)
//                .filter(all -> all.getExac().getAlleleFrequency().compareTo(BigDecimal.valueOf(0.01)) == -1);
//
//        Stream<AllAnnotation> t3 = t2
//                .filter(all -> all.getEsp6500() != null)
//                .filter(all ->  all.getEsp6500() != null && all.getEsp6500().getAlleleFrequency().compareTo(BigDecimal.valueOf(0.01)) == -1);


        deleteFiles(extension, otherExtension, logExtension, fileEntity.getFileFakeName());

        ModelAndView mv = new ModelAndView();
        mv.setViewName("annovarExecPage");
        mv.addObject("dbType", dbType);
        mv.addObject("fileIdx", fileIdx);
        mv.addObject("cols", cols);
        mv.addObject("list", list);
        return mv;
    }

    /**
     * methods
     */

    private Map<Long, AllAnnotation> getAllMap(Map<Long, AllAnnotation> map, String fileName, String extension, String DBs) throws IOException {

        Path path = Paths.get("C:/convertFile/" + fileName + extension);
        List<String> fileContentList = Files.readAllLines(path);

        switch (DBs) {
            case "refGeneAll":
                RefGeneAll.RefGeneAllBuilder refGeneAllBuilder = RefGeneAll.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    refGeneAllBuilder
                            .geneType(temp[0])
                            .geneNames(temp[1])
                            .geneList(temp[1])
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    RefGeneAll refGeneAll = refGeneAllBuilder.build();
                    AllAnnotation.AllAnnotationBuilder allAnnotationBuilder = AllAnnotation.builder();
                    map.put(Long.parseLong(temp[4]), allAnnotationBuilder.refGeneAll(refGeneAll).build());
                }

                break;
            case "refGeneExonic":
                RefGene.RefGeneBuilder refGeneBuilder = RefGene.builder();

                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    refGeneBuilder
                            .line(temp[0])
                            .geneType(temp[1])
                            .geneNames(temp[2])
                            .geneList(temp[2])
                            .chromNum(temp[3])
                            .start(Long.parseLong(temp[4]))
                            .end(Long.parseLong(temp[5]))
                            .ref(temp[6])
                            .alt(temp[7]);
                    RefGene refGene = refGeneBuilder.build();
                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[4]));

                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setRefGene(refGene);
                        map.put(Long.parseLong(temp[4]), allAnnotation);
                    }
                }

                break;
            case "ljb23_sift":
                SIFT.SIFTBuilder siftBuilder = SIFT.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    siftBuilder
                            .annotationType(temp[0])
                            .siftScore(new BigDecimal(temp[1]))
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    SIFT sift = siftBuilder.build();

                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[3]));
                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setSift(sift);
                        map.put(Long.parseLong(temp[3]), allAnnotation);
                    }

                }
                break;
            case "ljb23_pp2hvar":
                PolyPhen2hvar.PolyPhen2hvarBuilder polyPhen2hvarBuilder = PolyPhen2hvar.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    polyPhen2hvarBuilder
                            .annotationType(temp[0])
                            .hvarScore(new BigDecimal(temp[1]))
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    PolyPhen2hvar polyPhen2hvar = polyPhen2hvarBuilder.build();

                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[3]));
                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setPolyPhen2hvar(polyPhen2hvar);
                        map.put(Long.parseLong(temp[3]), allAnnotation);
                    }
                }
                break;
            case "clinvar_20150629":
                Clinvar.ClinvarBuilder clinvarBuilder = Clinvar.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    clinvarBuilder
                            .annotationType(temp[0])
                            .clinvarDescription(temp[1])
                            .clinvarDescriptionMap(temp[1])
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    Clinvar clinvar = clinvarBuilder.build();
                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[3]));
                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setClinvar(clinvar);
                        map.put(Long.parseLong(temp[3]), allAnnotation);
                    }
                }
                break;
            case "1000g2015aug_all":
                Genome1000.Genome1000Builder genome1000Builder = Genome1000.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    genome1000Builder
                            .annotationType(temp[0])
                            .alleleFrequency(new BigDecimal(temp[1]))
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    Genome1000 genome1000 = genome1000Builder.build();
                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[3]));
                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setGenome1000(genome1000);
                        map.put(Long.parseLong(temp[3]), allAnnotation);
                    }
                }
                break;
            case "exac03":
                Exac.ExacBuilder exacBuilder = Exac.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    exacBuilder
                            .annotationType(temp[0])
                            .alleleFrequency(new BigDecimal(temp[1]))
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    Exac exac = exacBuilder.build();
                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[3]));
                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setExac(exac);
                        map.put(Long.parseLong(temp[3]), allAnnotation);
                    }
                }
                break;
            case "esp6500si_all":
                ESP6500.ESP6500Builder esp6500Builder = ESP6500.builder();
                for (String content : fileContentList) {
                    String[] temp = content.split("\t");
                    esp6500Builder
                            .annotationType(temp[0])
                            .alleleFrequency(new BigDecimal(temp[1]))
                            .chromNum(temp[2])
                            .start(Long.parseLong(temp[3]))
                            .end(Long.parseLong(temp[4]))
                            .ref(temp[5])
                            .alt(temp[6]);
                    ESP6500 esp6500 = esp6500Builder.build();
                    AllAnnotation allAnnotation = map.get(Long.parseLong(temp[3]));
                    if(allAnnotation == null){

                    }else{
                        allAnnotation.setEsp6500(esp6500);
                        map.put(Long.parseLong(temp[3]), allAnnotation);
                    }
                }
                break;
        }//end switch

        return map;
    }


    //VCF 파일을 avinput 파일로 변환
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
        try {
            executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }

    //각 annovarDB에 맞는 파일 생성
    private void annotation(String vcfName, String dbType) {

        List<String> list = new ArrayList<>();

        if (dbType.equals("refGeneAll") || dbType.equals("refGeneExonic")) {
            list.add("perl");
            list.add("C:/annovar/annotate_variation.pl");
            list.add("-geneanno");
            list.add("-dbtype");
            list.add("refGene");
            list.add("-buildver");
            list.add("hg19");
            list.add("C:/convertFile/" + vcfName + ".avinput");
            list.add("C:/annovar/humandb/");
        } else {
            list.add("perl");
            list.add("C:/annovar/annotate_variation.pl");
            list.add("-filter");
            list.add("-dbtype");
            list.add(dbType);
            list.add("-buildver");
            list.add("hg19");
            list.add("-outfile");
            list.add("C:/convertFile/" + vcfName);
            list.add("C:/convertFile/" + vcfName + ".avinput");
            list.add("C:/annovar/humandb/");
        }

        try {
            Process process = new ProcessBuilder(list).start();

            //에러 보는 buffer
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

    //생성된 파일 삭제
    private void deleteFiles(String extension, String otherExtension, String logExtension, String fakeName) throws Exception {

        File droppedFile = new File("C:/convertFile/" + fakeName + extension);
        File filteredFile = new File("C:/convertFile/" + fakeName + otherExtension);
        File logFile = new File("C:/convertFile/" + fakeName + logExtension);
        if (droppedFile.exists() && filteredFile.exists()) {

            if (droppedFile.delete()) System.out.println("파일삭제 성공");
            else throw new Exception("dropped파일 삭제 실패");

            if (filteredFile.delete()) System.out.println("파일삭제 성공");
            else throw new Exception("filtered파일 삭제 실패");

            if (logFile.delete()) System.out.println("log파일삭제 성공");
            else throw new Exception("log파일 삭제 실패");

        } else {
            throw new Exception("저장된 파일 없음");
        }

    }

    //각 annovarDB에 맞는 열 이름 리스트
    List<String> getCols(String dbType) {
        List<String> cols = new ArrayList<>();

        if (dbType.equals("ljb23_sift") || dbType.equals("ljb23_pp2hvar")) { //SIFT, PolyPhen
            cols.add("annotaionType");
            cols.add("score");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (dbType.equals("1000g2015aug_all") || dbType.equals("exac03") || dbType.equals("esp6500si_all")) { //Genome1000, Exac, ESP
            cols.add("annotationType");
            cols.add("alleleFrequency");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (dbType.equals("refGeneAll") || dbType.equals("snp138")) { //RefGene_All
            cols.add("Names");
            cols.add("geneName");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (dbType.equals("refGeneExonic")) { //RefGene_Exonic
            cols.add("line");
            cols.add("geneType");
            cols.add("geneNames");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
        } else if (dbType.equals("clinvar_20150629")) { //Clinvar
            cols.add("annotationType");
            cols.add("chromNum");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
            cols.add("type");
            cols.add("clinvarDescription");
        }else if(dbType.equals("All")){
            cols.add("chromNum");
            cols.add("SIFT_SCORE");
            cols.add("PolyPhen_SCORE");
            cols.add("1000Genome_Allele");
            cols.add("ExAC_Allele");
            cols.add("ESP6500_Allele");
            cols.add("geneType");
            cols.add("geneName");
            cols.add("ExonicGeneType");
            cols.add("ExonicGeneNames");
            cols.add("start");
            cols.add("end");
            cols.add("ref");
            cols.add("alt");
            cols.add("type");
            cols.add("clinvarDescription");
        }

        return cols;
    }


    private String getExtension(String dbType) {

        String extension = "";

        if (dbType.equals("refGeneAll")) {
            extension = ".avinput.variant_function";
        } else if (dbType.equals("refGeneExonic")) {
            extension = ".avinput.exonic_variant_function";
        } else if (dbType.equals("1000g2015aug_all")) {
            extension = ".hg19_all.sites.2015_08_dropped";
        } else {
            extension = ".hg19_" + dbType + "_dropped";
        }


        return extension;
    }

    private String getOtherExtension(String dbType) {

        String otherExtension = "";

        if (dbType.equals("refGeneAll")) {
            otherExtension = ".avinput.exonic_variant_function";
        } else if (dbType.equals("refGeneExonic")) {
            otherExtension = ".avinput.variant_function";
        } else if (dbType.equals("1000g2015aug_all")) {
            otherExtension = ".hg19_all.sites.2015_08_filtered";
        } else {
            otherExtension = ".hg19_" + dbType + "_filtered";
        }

        return otherExtension;
    }


    private String getLogExtension(String dbType) {

        String logExtension = "";
        if (dbType.equals("refGeneAll")) {
            logExtension = ".avinput.log";
        } else if (dbType.equals("refGeneExonic")) {
            logExtension = ".avinput.log";
        } else {
            logExtension = ".log";
        }
        return logExtension;
    }

    /**
     * method
     */
    List<String> spiltList(String genes, String delimiter) {
        List<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(genes, delimiter);

        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }


}

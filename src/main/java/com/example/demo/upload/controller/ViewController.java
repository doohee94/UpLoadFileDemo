//package com.example.demo.upload.controller;
//
//import com.example.demo.upload.common.PaginationUtil;
//import com.example.demo.upload.entity.FileEntity;
//import com.example.demo.upload.entity.PersonEntity;
//import com.example.demo.upload.entity.SaveStatus;
//import com.example.demo.upload.entity.dto.*;
//import com.example.demo.upload.service.FileUploadService;
//import javafx.collections.transformation.FilteredList;
//import org.apache.commons.exec.CommandLine;
//import org.apache.commons.exec.DefaultExecutor;
//import org.apache.commons.exec.Executor;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.RandomStringUtils;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.xml.ws.Response;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@RestController
//@RequestMapping("/view")
//public class ViewController {
//
//    @Autowired
//    FileUploadService fileUploadService;
//
////    @RequestMapping(value = "/AllView", method = RequestMethod.POST)
////    public ModelAndView AllView(@RequestPart(required = false) MultipartFile file,Pageable pageable) throws Exception {
////
////        //1. 업로드된 VCF파일 저장
////        String sourceFileName = file.getOriginalFilename(); //파일 이름
////        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase(); //확장자
////
////        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
////        File destinationFile = new File("C:/uploadedFile/" + destinationFileName);
////        file.transferTo(destinationFile); //파일 저장
////
////        FileEntity.FileEntityBuilder builder = FileEntity.builder();
////        builder
////                .fileName(sourceFileName)
////                .fileSize(file.getSize())
////                .fileContentType(file.getContentType())
////                .fileFakeName(destinationFileName)
////                .attachmentUrl("C:/uploadedFile/" + destinationFileName)
////                .saveStatus(SaveStatus.UPLOADED)
////                .personId(((int) (Math.random() * 10) + 1));
////        FileEntity fileEntity = builder.build();
////
////        fileUploadService.saveFile(fileEntity);
////        int fileIdx = fileUploadService.getFileByFileFakeName(fileEntity.getFileFakeName()).getFileIdx();
////
////        //2. 저장된 VCF파일의 경로와 파일 이름을 보내서 avinput 파일 생성
////        tableAnnovar(fileEntity.getFileFakeName());
////
////        Path path = Paths.get("C:/convertFile/" + fileEntity.getFileFakeName() + ".hg19_multianno.txt");
////        List<String> fileContentList = Files.readAllLines(path);
////
////        List<TableAnnotation> list = getFileList(fileContentList);
////        List<Cols> cols = getCols(fileContentList.get(0).split("\t"));
////
////
////        deleteFiles(fileEntity.getFileFakeName());
////
////        List<Filter> filterList = new ArrayList<>();
////        Filter filter = new Filter("1", "equal", "");
////        filterList.add(filter);
////        Page<?> pageList = PaginationUtil.convertListToPage(list, pageable);
////
////        ModelAndView mv = new ModelAndView();
////        mv.setViewName("ViewPage");
////        mv.addObject("list", pageList);
////        mv.addObject("cols", cols);
////        mv.addObject("fileIdx", fileIdx);
////        mv.addObject("filterList", filterList);
////        mv.addObject("pageStatus", 1);
////        return mv;
////    }//end AllView
//
//    @RequestMapping(value = "/filters", method = RequestMethod.POST)
//    public ModelAndView filters(String dbSelect, String condition, String comp, int fileIdx, Pageable pageable) throws Exception {
//
//        FileEntity fileEntity = fileUploadService.getFile(fileIdx);
//
//        Path path = Paths.get("C:/convertFile/" + fileEntity.getFileFakeName() + ".hg19_multianno.txt");
//        List<String> fileContentList = Files.readAllLines(path);
//
//        List<Filter> filterList = new ArrayList<>();
//
//        for (int i = 0; i < dbSelect.split(",").length; i++) {
//            Filter.FilterBuilder builder = Filter.builder();
//
//            builder.dbSelect(dbSelect.split(",")[i])
//                    .condition(condition.split(",")[i])
//                    .comp(comp.split(",", -1)[i]);
//            filterList.add(builder.build());
//        }
//
//        List<TableAnnotation> list = getFilterFileListByList(fileContentList, filterList);
//        List<Cols> cols = getCols(fileContentList.get(0).split("\t"));
//
//
//        Page<?> pageList = PaginationUtil.convertListToPage(list, pageable);
//
//
//        ModelAndView mv = new ModelAndView();
//        mv.setViewName("ViewPage");
////        mv.addObject("list", list.subList(subListStart, subListEnd));
//        mv.addObject("list", pageList);
//        mv.addObject("cols", cols);
//        mv.addObject("fileIdx", fileIdx);
//        mv.addObject("filterList", filterList);
//        return mv;
//    }
//
//    @RequestMapping(value = "/read{fileIdx}")
//    public ResponseEntity getFileByPersonId(@PathVariable("fileIdx") int fileIdx, Pageable pageable) throws Exception {
//
//        FileEntity fileEntity = fileUploadService.getFile(fileIdx);
//
//        Path path = Paths.get("C:/convertFile/" + fileEntity.getFileFakeName() + ".hg19_multianno.txt");
//        List<String> fileContentList = Files.readAllLines(path);
//
//        List<TableAnnotation> list = getFileList(fileContentList);
//        List<Cols> cols = getCols(fileContentList.get(0).split("\t"));
//
//        List<Filter> filterList = new ArrayList<>();
//        Filter filter = new Filter("1", "equal", "");
//        filterList.add(filter);
//
//        Page<?> pageList = PaginationUtil.convertListToPage(list, pageable);
//
//        return new ResponseEntity(pageList, HttpStatus.OK);
//    }
//
//
//    /**
//     * methods
//     */
//    private List<TableAnnotation> getFileList(List<String> fileContentList) {
//
//        List<TableAnnotation> list = new ArrayList<>();
//
//        for (int i = 1; i < fileContentList.size(); i++) {
//
//            TableAnnotation.TableAnnotationBuilder builder = TableAnnotation.builder();
//
//            String[] temp = fileContentList.get(i).split("\t");
//
//            DecimalFormat format = new DecimalFormat("#.########");
//            format.setGroupingUsed(false);
//
//            builder
//                    .chr(temp[0])
//                    .start(Long.parseLong(temp[1]))
//                    .end(Long.parseLong(temp[2]))
//                    .ref(temp[3])
//                    .alt(temp[4])
//                    .funcRefGene(temp[5])
//                    .geneRefGene(temp[6])
//                    .geneDetail(temp[7])
//                    .geneDetailList(temp[7])
//                    .exonicRefGene(temp[8])
//                    .changRefGene(temp[9])
//                    .changeRefGeneList(temp[9])
//                    .siftScore(temp[10].split(",")[0])
//                    .ppScore(temp[11].split(",")[0])
//                    .clnalleleid(temp[12])
//                    .clndn(temp[13])
//                    .clndnList(temp[13])
//                    .clndisdb(temp[14])
//                    .clndisdbList(temp[14])
//                    .clnrevstat(temp[15])
//                    .clnrevstatList(temp[15])
//                    .clnsig(temp[16])
//                    .genome1000Alle(temp[17].equals(".") ? "-100" : format.format(Double.parseDouble(temp[17])))
//                    .exacAlle(temp[18].equals(".") ? "-100" : format.format(Double.parseDouble(temp[18])))
//                    .esp6500Alle(temp[26].equals(".") ? "-100" : format.format(Double.parseDouble(temp[26])));
//            //.snp138(temp[27]);
//
//            list.add(builder.build());
//        }//end for
//        return list;
//    }
//
//
//    private List<Cols> getCols(String[] columns) {
//        Integer[] colsList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26};
//
//        List<Cols> list = new ArrayList<>();
//        for (int i = 0; i < colsList.length; i++) {
//            Cols temp = new Cols(colsList[i], columns[colsList[i]], true);
//            list.add(temp);
//        }
//        return list;
//    }
//
//
//    private List<TableAnnotation> getFilterFileListByList(List<String> fileContentList, List<Filter> filterList) throws Exception {
//        List<TableAnnotation> list = new ArrayList<>();
//
//        iForOUT:
//        for (int i = 1; i < fileContentList.size(); i++) {
//
//            String[] temp = fileContentList.get(i).split("\t");
//            int status = 0;
//
//            for (int j = 0; j < filterList.size(); j++) {
//
//                String condition = filterList.get(j).getCondition().trim();
//                String inputComp = filterList.get(j).getComp().trim();
//                String colsNum = filterList.get(j).getDbSelect().trim();
//
//                if (inputComp.equals("")) {
//                    continue;
//                }
//
//                // '.' 으로 되어있는 애들 제거 // 필터 아무것도 입력 안된거 제거
//                if (temp[Integer.parseInt(colsNum)].equals(".")) {
//                    continue iForOUT;
//                }
//
//                String tempStr = temp[Integer.parseInt(colsNum)];
//                if (colsNum.equals("10") || colsNum.equals("11")) { //sift, pp score spilt
//                    tempStr = tempStr.split(",")[0];
//                }
//
//                switch (condition) {
//                    case "equal":
//                        if (colsNum.equals("17") || colsNum.equals("18") || colsNum.equals("26")) { //double type인경우
//                            if (Double.parseDouble(tempStr) != Double.parseDouble(inputComp)) {
//                                status++;
//                            }
//                        } else {
//                            if (colsNum.equals("9") || colsNum.equals("10") || colsNum.equals("13") || colsNum.equals("14") || colsNum.equals("15")) {
//                                if (!tempStr.contains(inputComp)) {
//                                    status++;
//                                }
//                            } else {
//                                if (!tempStr.equals(inputComp)) {
//                                    status++;
//                                }
//                            }
//                        }//end 문자열/double 비교
//
//                        break;
//                    case "notEqual":
//                        if (colsNum.equals("17") || colsNum.equals("18") || colsNum.equals("26")) {
//                            if (Double.parseDouble(tempStr) == Double.parseDouble(inputComp)) {
//                                status++;
//                            }
//                        } else {
//                            if (colsNum.equals("9") || colsNum.equals("10") || colsNum.equals("13") || colsNum.equals("14") || colsNum.equals("15")) {
//                                if (tempStr.contains(inputComp)) {
//                                    status++;
//                                }
//                            } else {
//                                if (tempStr.equals(inputComp)) {
//                                    status++;
//                                }
//                            }
//                        }
//                        break;
//                    case "less":
//                        if (Double.parseDouble(tempStr) >= Double.parseDouble(inputComp)) {
//                            status++;
//                        }
//                        break;
//                    case "lessEqual":
//                        if (Double.parseDouble(tempStr) > Double.parseDouble(inputComp)) {
//                            status++;
//                        }
//                        break;
//                    case "greater":
//                        if (Double.parseDouble(tempStr) <= Double.parseDouble(inputComp)) {
//                            status++;
//                        }
//                        break;
//                    case "greaterEqual":
//                        if (Double.parseDouble(tempStr) < Double.parseDouble(inputComp)) {
//                            status++;
//                        }
//                        break;
//                }//end switch
//
//            }//end for j
//
//            if (status > 0) {
//                continue;
//            }
//            DecimalFormat format = new DecimalFormat("#.########");
//            format.setGroupingUsed(false);
//
//
//            TableAnnotation.TableAnnotationBuilder builder = TableAnnotation.builder();
//            builder
//                    .chr(temp[0])
//                    .start(Long.parseLong(temp[1]))
//                    .end(Long.parseLong(temp[2]))
//                    .ref(temp[3])
//                    .alt(temp[4])
//                    .funcRefGene(temp[5])
//                    .geneRefGene(temp[6])
//                    .geneDetail(temp[7])
//                    .geneDetailList(temp[7])
//                    .exonicRefGene(temp[8])
//                    .changRefGene(temp[9])
//                    .changeRefGeneList(temp[9])
//                    .siftScore(temp[10].split(",")[0])
//                    .ppScore(temp[11].split(",")[0])
//                    .clnalleleid(temp[12])
//                    .clndn(temp[13])
//                    .clndnList(temp[13])
//                    .clndisdb(temp[14])
//                    .clndisdbList(temp[14])
//                    .clnrevstat(temp[15])
//                    .clnrevstatList(temp[15])
//                    .clnsig(temp[16])
//                    .genome1000Alle(temp[17].equals(".") ? "-100" : format.format(Double.parseDouble(temp[17])))
//                    .exacAlle(temp[18].equals(".") ? "-100" : format.format(Double.parseDouble(temp[18])))
//                    .esp6500Alle(temp[26].equals(".") ? "-100" : format.format(Double.parseDouble(temp[26])));
//            //.snp138(temp[27]);
//            list.add(builder.build());
//        }//end for
//
//
//        return list;
//    }
//
//    //table_annvar실행
//    private void tableAnnovar(String vcfName) {
//
//        List<String> list = new ArrayList<>();
//        list.add("perl ");
//        list.add("C:/annovar/table_annovar.pl ");
//        list.add("C:/uploadedFile/" + vcfName + " ");
//        list.add("C:/annovar/humandb/ ");
//        list.add("-buildver ");
//        list.add("hg19 ");
//        list.add("-outfile ");
//        list.add("C:/convertFile/" + vcfName + " ");
//        list.add("-remove ");
//        list.add("-protocol ");
//        list.add("refGene,");
//        list.add("ljb23_sift,");
//        list.add("ljb23_pp2hvar,");
//        list.add("clinvar_20190305,");
//        list.add("1000g2015aug_all,");
//        list.add("exac03,");
//        list.add("esp6500si_all ");
//        list.add("-operation ");
//        list.add("g,f,f,f,f,f,f ");
//        list.add("-nastring ");
//        list.add(". ");
//        list.add("-vcfinput ");
//        list.add("-polish ");
//        list.add("-xref ");
//        list.add("C:/annovar/example/gene_xref.txt");
//
//        String line = "";
//
//        for (int i = 0; i < list.size(); i++) {
//            line += list.get(i);
//        }
//        System.out.println(">>" + line);
//        CommandLine commandLine = CommandLine.parse(line);
//        Executor executor = new DefaultExecutor();
//        try {
//            executor.execute(commandLine);
//        } catch (Exception e) {
//            System.out.println("Exception : " + e.getMessage());
//        }
//
//    }
//
//    //생성된 파일 삭제
//    private void deleteFiles(String fakeName) throws Exception {
//
//        File deleteVcf = new File("C:/convertFile/" + fakeName + ".hg19_multianno.vcf");
//        File deleteAvinput = new File("C:/convertFile/" + fakeName + ".avinput");
//
//        if (deleteVcf.exists() && deleteAvinput.exists()) {
//
//            if (deleteVcf.delete()) System.out.println("VCF파일삭제 성공");
//            else throw new Exception("VCF파일 삭제 실패");
//
//            if (deleteAvinput.delete()) System.out.println("avinput파일삭제 성공");
//            else throw new Exception("avinput파일 삭제 실패");
//
//
//        } else {
//            throw new Exception("저장된 파일 없음");
//        }
//
//    }
//
//
//}

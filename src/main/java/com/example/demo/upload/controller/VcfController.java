//package com.example.demo.upload.controller;
//
//import com.example.demo.upload.common.PaginationUtil;
//import com.example.demo.upload.entity.FileEntity;
//import com.example.demo.upload.entity.SaveStatus;
//import com.example.demo.upload.entity.dto.*;
//import com.example.demo.upload.service.FileUploadService;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.exec.CommandLine;
//import org.apache.commons.exec.DefaultExecutor;
//import org.apache.commons.exec.Executor;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class VcfController {
//
//    private final FileUploadService fileUploadService;
//
//    @RequestMapping(value = "/AllView", method = RequestMethod.GET)
//    public ModelAndView AllView(Pageable pageable) throws Exception {
//
//
//        Path path = Paths.get("C:/convertFile/" + "g4uZz2XhgzBOrwx9feGp89lBsjkiIgEy.vcf.hg19_multianno.txt");
//        List<String> fileContentList = Files.readAllLines(path);
//
//        VcfLines list = getFileList(fileContentList);
//
//        List<Filter> filterList = new ArrayList<>();
//        Filter filter = new Filter("chr", "!=", "1");
//
//
//        filterList.add(filter);
//        Page<?> pageList = list.convertPagination(pageable);
//
//        List<Cols> cols = getCols(fileContentList.get(0).split("\t"));
//        ModelAndView mv = new ModelAndView();
//        mv.setViewName("ViewPage");
//        mv.addObject("list", pageList);
//        mv.addObject("cols", cols);
//        return mv;
//    }//end AllView
//
//    /**
//     * methods
//     */
//    private VcfLines getFileList(List<String> fileContentList) {
//
//        VcfLines list = new VcfLines();
//        List<String> headers = Arrays.asList(fileContentList.get(0).split("\t"));
//
//        for (int i = 1; i < fileContentList.size(); i++) {
//
//            List<String> temp = Arrays.asList(fileContentList.get(i).split("\t"));
//            VcfLine vcfLine = new VcfLine(headers, temp);
//
//            DecimalFormat format = new DecimalFormat("#.########");
//            format.setGroupingUsed(false);
//
//
//            list.add(vcfLine);
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
//}

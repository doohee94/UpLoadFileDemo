package com.example.demo.upload.controller;

import com.example.demo.upload.entity.dto.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import htsjdk.tribble.annotation.Strand;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/view")
public class ViewController {

    @RequestMapping("/AllView")
    public ModelAndView AllView() throws Exception {

        //원래는 VCF파일을 받아서 DB에 저장 후에 table_annovar.pl 돌려서 결과로 받은 파일을 파싱
        String fileName = "COAD.hg19_multianno";
        Path path = Paths.get("C:/Users/classact/Desktop/" + fileName + ".txt");
        List<String> fileContentList = Files.readAllLines(path);

        List<TableAnnotation> list = getFileList(fileContentList);

        Integer[] colsList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 22};
        List<Cols> cols = getCols(Arrays.asList(colsList), fileContentList.get(0).split("\t"));

        ModelAndView mv = new ModelAndView();
        mv.setViewName("ViewPage");
        mv.addObject("list", list);
        mv.addObject("cols", cols);
        return mv;
    }//end AllView

    @RequestMapping(value = "/filters", method = RequestMethod.POST)
    public ModelAndView filters(String dbSelect, String condition, String comp) throws Exception {

        String fileName = "COAD.hg19_multianno";
        Path path = Paths.get("C:/Users/classact/Desktop/" + fileName + ".txt");
        List<String> fileContentList = Files.readAllLines(path);

        List<Filter> filterList = new ArrayList<>();

        for (int i = 0; i < dbSelect.split(",").length; i++) {
            Filter.FilterBuilder builder = Filter.builder();
            builder.dbSelect(dbSelect.split(",")[i])
                    .condition(condition.split(",")[i])
                    .comp(comp.split(",")[i]);
            filterList.add(builder.build());
        }

        List<TableAnnotation> list = getFilterFileListByList(fileContentList, filterList);

        Integer[] colsList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 22};
        List<Cols> cols = getCols(Arrays.asList(colsList), fileContentList.get(0).split("\t"));

        ModelAndView mv = new ModelAndView();
        mv.setViewName("ViewPage");
        mv.addObject("list", list);
        mv.addObject("cols", cols);
        mv.addObject("filterList", filterList);
        return mv;
    }


    /**
     * methods
     */
    private List<TableAnnotation> getFileList(List<String> fileContentList) {

        List<TableAnnotation> list = new ArrayList<>();

        for (int i = 1; i < 10; i++) {

            TableAnnotation.TableAnnotationBuilder builder = TableAnnotation.builder();

            String[] temp = fileContentList.get(i).split("\t");

            builder
                    .chr(temp[0])
                    .start(Long.parseLong(temp[1]))
                    .end(Long.parseLong(temp[2]))
                    .ref(temp[3])
                    .alt(temp[4])
                    .funcRefGene(temp[5])
                    .geneRefGene(temp[6])
                    .geneDetail(temp[7])
                    .geneDetailList(temp[7])
                    .exonicRefGene(temp[8])
                    .changRefGene(temp[9])
                    .changeRefGeneList(temp[9])
                    .siftScore(temp[10])
                    .ppScore(temp[11])
                    .clinvar(temp[12])
                    .clinvarMap(temp[12])
                    .genome1000Alle(temp[13].equals(".") ? -100 : Double.parseDouble(temp[13]))
                    .exacAlle(temp[14].equals(".") ? -100 : Double.parseDouble(temp[14]))
                    .esp6500Alle(temp[22].equals(".") ? -100 : Double.parseDouble(temp[22]));

            list.add(builder.build());
        }//end for
        return list;
    }


    private List<Cols> getCols(List<Integer> colsList, String[] columns) {

        List<Cols> list = new ArrayList<>();

        for (int i = 0; i < colsList.size(); i++) {
            Cols temp = new Cols(colsList.get(i), columns[colsList.get(i)], true);
            list.add(temp);

            if (colsList.get(i) == 12) {
                Cols temp2 = new Cols(colsList.get(i), "clinvar_detail", true);
                list.add(temp2);
            }
        }
        return list;
    }

    private List<TableAnnotation> getFilterFileListByList(List<String> fileContentList, List<Filter> filterList) throws Exception {
        List<TableAnnotation> list = new ArrayList<>();

        iForOUT:
        for (int i = 1; i < fileContentList.size(); i++) {



            String[] temp = fileContentList.get(i).split("\t");
            int status = 0;

            for (int j = 0; j < filterList.size(); j++) {

                String condition = filterList.get(j).getCondition().trim();
                String inputComp = filterList.get(j).getComp().trim();
                String colsNum = filterList.get(j).getDbSelect().trim();

                // '.' 으로 되어있는 애들 제거
                if (temp[Integer.parseInt(colsNum)].equals(".") || condition.equals("") || inputComp.equals("") || colsNum.equals("")) {
                    continue iForOUT;
                }

                switch (condition) {
                    case "equal":
                        //문자열일경우 vs double일 경우
                        if (colsNum.equals(13) || colsNum.equals(14) || colsNum.equals(22)) {
                            if (Double.parseDouble(temp[Integer.parseInt(colsNum)]) != Double.parseDouble(inputComp)) {
                                status++;
                            }
                        } else {
                            if (!temp[Integer.parseInt(colsNum)].contains(inputComp)) {
                                status++;
                            }
                        }

                        break;
                    case "notEqual":
                        if (colsNum.equals(13) || colsNum.equals(14) || colsNum.equals(22)) {
                            if (Double.parseDouble(temp[Integer.parseInt(colsNum)]) == Double.parseDouble(inputComp)) {
                                status++;
                            }
                        } else {
                            if (temp[Integer.parseInt(colsNum)].contains(inputComp)) {
                                status++;
                            }
                        }
                        break;
                    case "less":
                        if (Double.parseDouble(temp[Integer.parseInt(colsNum)]) >= Double.parseDouble(inputComp)) {
                            status++;
                        }
                        break;
                    case "lessEqual":
                        if (Double.parseDouble(temp[Integer.parseInt(colsNum)]) > Double.parseDouble(inputComp)) {
                            status++;
                        }
                        break;
                    case "greater":
                        if (Double.parseDouble(temp[Integer.parseInt(colsNum)]) <= Double.parseDouble(inputComp)) {
                            status++;
                        }
                        break;
                    case "greaterEqual":
                        if (Double.parseDouble(temp[Integer.parseInt(colsNum)]) < Double.parseDouble(inputComp)) {
                            status++;
                        }
                        break;
                }//end switch

            }//end for j

            if (status > 0) {
                continue;
            }

            TableAnnotation.TableAnnotationBuilder builder = TableAnnotation.builder();
            builder
                    .chr(temp[0])
                    .start(Long.parseLong(temp[1]))
                    .end(Long.parseLong(temp[2]))
                    .ref(temp[3])
                    .alt(temp[4])
                    .funcRefGene(temp[5])
                    .geneRefGene(temp[6])
                    .geneDetail(temp[7])
                    .geneDetailList(temp[7])
                    .exonicRefGene(temp[8])
                    .changRefGene(temp[9])
                    .changeRefGeneList(temp[9])
                    .siftScore(temp[10])
                    .ppScore(temp[11])
                    .clinvar(temp[12])
                    .clinvarMap(temp[12])
                    .genome1000Alle(temp[13].equals(".") ? -100 : Double.parseDouble(temp[13]))
                    .exacAlle(temp[14].equals(".") ? -100 : Double.parseDouble(temp[14]))
                    .esp6500Alle(temp[22].equals(".") ? -100 : Double.parseDouble(temp[22]));

            list.add(builder.build());
        }//end for


        return list;
    }


}

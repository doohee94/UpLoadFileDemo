package com.example.demo.upload.service;


import com.example.demo.upload.PerlConfiguration;
import com.example.demo.upload.common.PaginationUtil;
import com.example.demo.upload.entity.*;
import com.example.demo.upload.entity.dto.*;
import com.example.demo.upload.repository.AnnovarDBInformationRepository;
import com.example.demo.upload.repository.ConvertFileRepository;
import com.example.demo.upload.repository.FileUploadRepository;
import com.example.demo.upload.repository.PresetRepository;
import htsjdk.samtools.util.BufferedLineReader;
import htsjdk.tribble.readers.AsciiLineReader;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class VCFFileUploadServiceImpl implements VCFFileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final ConvertFileRepository convertFileRepository;
    private final PerlConfiguration perlConfiguration;
    private final AnnovarDBInformationRepository annovarDBInformationRepository;
    private final PresetRepository presetRepository;

    @Override
    public void fileUpload(MultipartFile file) throws Exception {
        FileEntity fileEntity = FileEntity.builder().file(file).build();
        fileEntity.isValdateExtension();
        File destinationFile = new File(fileEntity.getAttachmentUrl());
        file.transferTo(destinationFile); //파일 저장
        fileUploadRepository.save(fileEntity);
    }

    @Override
    public Page<FileEntity> vcfFileList(int personId, Pageable pageable) {
        return fileUploadRepository.findByPersonId(personId, pageable);
    }

    @Override
    public void analyze(List<String> dbList, int vcfFileIdx) throws Exception {
        FileEntity fileEntity = fileUploadRepository.findByFileIdx(vcfFileIdx);

        //dbList를 가져와서 이름 바꿔서 다시 넣어주기
        Map<String, String> map = annovarDBInformationRepository.findAll().stream()
                .collect(Collectors.toMap(AnnovarDBInformation::getViewName, AnnovarDBInformation::getRealName));

        List<String> selectedDBList = new ArrayList<>();
        for (String viewName : dbList) {
            selectedDBList.add(map.get(viewName));
        }

        tableAnnovarDBSelect(selectedDBList, fileEntity.getFileFakeName());
        deleteFiles(fileEntity.getFileFakeName());

        ConvertFileEntity convertFileEntity = convertFileRepository.findByOriginFileidx(vcfFileIdx);

        if (convertFileEntity == null) {
            convertFileEntity = ConvertFileEntity.builder()
                    .fileEntity(fileEntity).build();
        } else {
            convertFileEntity.setAnnotatedDate(LocalDate.now());
        }

        convertFileRepository.save(convertFileEntity);
    }

    @Override
    public Page<?> getAnalyzedList(FilterList filterList, List<String> selectedHeaders, int vcfFileIdx, Pageable pageable) throws Exception {
        ConvertFileEntity convertFileEntity = convertFileRepository.findByOriginFileidx(vcfFileIdx);
        VcfLines list = null;

        if (convertFileEntity == null) {
            list = getBasicInfo(vcfFileIdx, selectedHeaders);
        } else {
            Path path = Paths.get(convertFileEntity.getAttachmentUrl());
            List<String> fileContentList = Files.readAllLines(path);
            list = getContent(fileContentList, selectedHeaders);
        }

        return PaginationUtil.convertListToPage(list.filter(filterList), pageable);
    }


    @Override
    public List<AnnovarDBInformation> getAnnovarInformation(int vcfFileIdx, List<String> selectedHeaders, Pageable pageable) throws Exception {
        ConvertFileEntity convertFileEntity = convertFileRepository.findByOriginFileidx(vcfFileIdx);
        List<AnnovarDBInformation> infoList = annovarDBInformationRepository.findAll();

        List<AnnovarDBInformation> list = new ArrayList<>();

        if (convertFileEntity == null) { // analyze된 파일이 없을 경우
            list = infoList.stream().filter(a -> a.getGroup().equals("BasicInfo")).collect(Collectors.toList());
        }else{ //분석 파일이 있을 경우

            //파일의 헤더를 파싱하고
            Path path = Paths.get(convertFileEntity.getAttachmentUrl());
            List<String> fileContentList = Files.readAllLines(path);
            List<String> fileHeader = Arrays.asList(fileContentList.get(0).split("\t"));

            //group filter
            for(int i=0; i<infoList.size(); i++){
                for(int j=0; j<fileHeader.size(); j++){
                    List<AnnovarDBHeader> tempList = infoList.get(i).getHeaderList();
                    int finalJ = j;
                    boolean tempFilter = tempList.stream().anyMatch(s -> s.getHeader().equals(fileHeader.get(finalJ)));
                    if(tempFilter){//한개라도 부합되는게 있다면..
                        list.add(infoList.get(i));
                        break;
                    }
                }//end j
            }//end i
        }//end else-if

        //header filter
        for(int i=0; i<list.size(); i++){
            for(int j=0; j<list.get(i).getHeaderList().size(); j++){
                AnnovarDBHeader tempHeader =  list.get(i).getHeaderList().get(j);
                for(int k=0; k<selectedHeaders.size(); k++){
                    if(tempHeader.getHeader().equals(selectedHeaders.get(k))){
                        tempHeader.setDisplayChecked(true);
                        break;
                    }
                }//end k
            }//end j
        }// end i


        return list;
    }

    @Override
    public List<PresetEntity> getPresetList(int personId) {
        return presetRepository.findByPersonId(personId);
    }


    /**
     * methods
     */

    private void tableAnnovarDBSelect(List<String> dbList, String vcfName) {

        StringBuilder protocols = new StringBuilder();
        StringBuilder operations = new StringBuilder();

        for (int i = 0; i < dbList.size(); i++) {
            protocols.append(dbList.get(i));
            operations.append(dbList.get(i).equals("refGene") ? "g" : "f");
            if (i != dbList.size() - 1) {
                protocols.append(",");
                operations.append(",");
            }
        }

        StringBuilder line = new StringBuilder();
        line.append(perlConfiguration.getCommand())
                .append(" -protocol ")
                .append(protocols)
                .append(" -operation ")
                .append(operations)
                .append(" ")
                .append(perlConfiguration.getUploadedPath())
                .append(vcfName)
                .append(" ")
                .append(perlConfiguration.getAnnovarDB())
                .append(" -outfile ")
                .append(perlConfiguration.getConvertPath())
                .append(vcfName);

        System.out.println(">>" + line.toString());
        CommandLine commandLine = CommandLine.parse(line.toString());

        Executor executor = new DefaultExecutor();
        try {
            executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }

    public VcfLines getBasicInfo(int vcfFileIdx, List<String> selectedHeaders) throws Exception {

        FileEntity fileEntity = fileUploadRepository.findByFileIdx(vcfFileIdx);

        InputStream inputStream = new FileInputStream(fileEntity.getAttachmentUrl());

        BufferedLineReader br = new BufferedLineReader(inputStream);
        AsciiLineReader ar = new AsciiLineReader(inputStream);
        LineReader lr2 = ar;
        LineIteratorImpl t = new LineIteratorImpl(lr2);

        VCFCodec codec = new VCFCodec();
        codec.readActualHeader(t);
        VariantContext ctx = null;

        VcfLines vcfLines = new VcfLines();
        String[] headers = {"Chr", "Start", "End", "Ref", "Alt", "TCGA_CODE", "TVAF", "TDP", "TAL"};
        List<String> header = Arrays.asList(headers);

        while (t.hasNext()) {
            ctx = codec.decode(t.next());

            List<String> temp = new ArrayList<>();
            temp.add(ctx.getContig());
            temp.add(String.valueOf(ctx.getStart()));
            temp.add(String.valueOf(ctx.getEnd()));
            temp.add(ctx.getGenotypes().get(0).getAllele(0).getBaseString());
            temp.add(ctx.getGenotypes().get(0).getAllele(1).getBaseString());
            temp.add(ctx.getAttributes().get("TCGA_CODE").toString());
            temp.add(ctx.getAttributes().get("TVAF").toString());
            temp.add(ctx.getAttributes().get("TDP").toString());
            temp.add(ctx.getAttributes().get("TAL").toString().replace("[", "").replace("]", "").replace(" ", ""));

            VcfLine vcfLine = new VcfLine(header, temp, selectedHeaders);
            vcfLines.add(vcfLine);
        }
        br.close();

        return vcfLines;
    }

    private VcfLines getContent(List<String> fileContentList, List<String> selectedHeaders) {

        VcfLines list = new VcfLines();
        List<String> headers = Arrays.asList(fileContentList.get(0).split("\t"));

        for (int i = 1; i < fileContentList.size(); i++) {

            List<String> temp = Arrays.asList(fileContentList.get(i).split("\t"));
            VcfLine vcfLine = new VcfLine(headers, temp, selectedHeaders);

            list.add(vcfLine);
        }//end for
        return list;
    }


    //생성된 파일 삭제
    private void deleteFiles(String fakeName) throws Exception {

        File deleteVcf = new File(perlConfiguration.getConvertPath() + fakeName + ".hg19_multianno.vcf");
        File deleteAvinput = new File(perlConfiguration.getConvertPath() + fakeName + ".avinput");

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

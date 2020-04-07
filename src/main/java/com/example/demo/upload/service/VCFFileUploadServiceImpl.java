package com.example.demo.upload.service;


import com.example.demo.upload.PerlConfiguration;
import com.example.demo.upload.common.PaginationUtil;
import com.example.demo.upload.entity.ConvertFileEntity;
import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.dto.Filter;
import com.example.demo.upload.entity.dto.Header;
import com.example.demo.upload.entity.dto.VcfLine;
import com.example.demo.upload.entity.dto.VcfLines;
import com.example.demo.upload.repository.ConvertFileRepository;
import com.example.demo.upload.repository.FileUploadRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VCFFileUploadServiceImpl implements VCFFileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final ConvertFileRepository convertFileRepository;
    private final PerlConfiguration perlConfiguration;

    @Override
    public void fileUpload(MultipartFile file) throws Exception {
        FileEntity fileEntity = FileEntity.builder().file(file).build();
        fileEntity.isValdateExtension();
        File destinationFile = new File(fileEntity.getAttachmentUrl());
        file.transferTo(destinationFile); //파일 저장
        fileUploadRepository.save(fileEntity);
    }

    @Override
    public Page<FileEntity> VCFfileList(int personId, Pageable pageable) throws Exception {
        Page<FileEntity> list = fileUploadRepository.findByPersonId(personId, pageable);

        for(FileEntity file : list){
            Long countConvertFile = convertFileRepository.countByOriginFileidx(file.getFileIdx());
            file.setAnalyzeCount(countConvertFile);
        }

        return list;
    }

    @Override
    public void VCFFileConvert(int VCFFileIdx, String inputFileName) throws Exception {
        FileEntity fileEntity = fileUploadRepository.findByFileIdx(VCFFileIdx);

        Long countConvertFile = convertFileRepository.countByOriginFileidx(VCFFileIdx);

        tableAnnovar(fileEntity.getFileFakeName(), countConvertFile);
        deleteFiles(fileEntity.getFileFakeName(), countConvertFile);

        ConvertFileEntity convertFileEntity = ConvertFileEntity.builder().
                fileEntity(fileEntity).inputFileName(inputFileName).countConvertFile(countConvertFile).build();
        convertFileRepository.save(convertFileEntity);
    }

    @Override
    public void VCFFileConvertSelectDB(List<String> dbList, int VCFFileIdx, String inputFileName) throws Exception {
        FileEntity fileEntity = fileUploadRepository.findByFileIdx(VCFFileIdx);

        Long countConvertFile = convertFileRepository.countByOriginFileidx(VCFFileIdx);

        tableAnnovarDBSelect(dbList, fileEntity.getFileFakeName(), countConvertFile);
        deleteFiles(fileEntity.getFileFakeName(), countConvertFile);

        ConvertFileEntity convertFileEntity = ConvertFileEntity.builder()
                .fileEntity(fileEntity).inputFileName(inputFileName).countConvertFile(countConvertFile).build();

        convertFileRepository.save(convertFileEntity);
    }

    @Override
    public Page<ConvertFileEntity> convertFileList(int VCFFileIdx, Pageable pageable) throws Exception {
        return convertFileRepository.findAllByOriginFileidx(VCFFileIdx, pageable);
    }

    @Override
    public Page<?> getConvertFile(int fileIdx, List<String>selectedHeaders,Pageable pageable) throws Exception {

        ConvertFileEntity convertFileEntity = convertFileRepository.findByFileIdx(fileIdx);

        Path path = Paths.get(convertFileEntity.getAttachmentUrl());
        List<String> fileContentList = Files.readAllLines(path);

        VcfLines list = getContent(fileContentList,selectedHeaders);

        return list.convertPagination(pageable);
    }

    @Override
    public Page<?> getFilteredList(List<Filter> filterList,List<String>selectedHeaders,int fileIdx, Pageable pageable) throws Exception {

        ConvertFileEntity convertFileEntity = convertFileRepository.findByFileIdx(fileIdx);

        Path path = Paths.get(convertFileEntity.getAttachmentUrl());
        List<String> fileContentList = Files.readAllLines(path);

        VcfLines list = getContent(fileContentList,selectedHeaders);

        return PaginationUtil.convertListToPage(list.filter(filterList), pageable);
    }

    @Override
    public Page<?> getBasicInfo(int vcfFileIdx, List<String> selectedHeaders,Pageable pageable) throws Exception {

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
        String []headers = {"Chr","Start","End","Ref","Alt","TCGA_CODE","TVAF","TDP","TAL"};
        List<String> header =Arrays.asList(headers);

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
            temp.add(ctx.getAttributes().get("TAL").toString().replace("[","").replace("]","").replace(" ",""));

            VcfLine vcfLine = new VcfLine(header, temp,selectedHeaders);
            vcfLines.add(vcfLine);
        }
        br.close();

        return vcfLines.convertPagination(pageable);
    }

    @Override
    public Page<?> getSelectByHeader(int convertFileIdx,List<String> selectedHeaders, Pageable pageable) throws Exception {
        ConvertFileEntity convertFileEntity = convertFileRepository.findByFileIdx(convertFileIdx);

        Path path = Paths.get(convertFileEntity.getAttachmentUrl());
        List<String> fileContentList = Files.readAllLines(path);
        VcfLines list = getFilterHeaderContent(fileContentList, selectedHeaders);

        return list.convertPagination(pageable);
    }

    @Override
    public List<Header> getBasicInfoHeader(List<String> selectedHeaders, Pageable pageable) throws Exception {
        List<Header> list = new ArrayList<>();
        for(int i=0; i<selectedHeaders.size(); i++){
            Header header = new Header(selectedHeaders.get(i));
            list.add(header);
        }
        return list;
    }

    @Override
    public List<Header> getHeaders(int convertFileIdx, List<String> selectedHeaders, Pageable pageable) throws Exception {
        ConvertFileEntity convertFileEntity = convertFileRepository.findByFileIdx(convertFileIdx);

        Path path = Paths.get(convertFileEntity.getAttachmentUrl());
        List<String> fileContentList = Files.readAllLines(path);
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(fileContentList.get(0).split("\t")));
        headers.add("TCGA_CODE");
        headers.add("TVAF");
        headers.add("TDP");
        headers.add("TAL");

        List<Header> list = new ArrayList<>();

        for(int i=0; i<headers.size(); i++){
            Header tempHeader = new Header(headers.get(i), false);
            for(int j=0; j<selectedHeaders.size(); j++){
                if(headers.get(i).equals(selectedHeaders.get(j))){
                    tempHeader.setChecked(true);
                    break;
                }
            }// end for j
            list.add(tempHeader);
        }// end for i

        return list;
    }

    /**
     * methods
     */

    private void tableAnnovarDBSelect(List<String> dbList, String vcfName, Long countConvertFile) {

        String saveName =  vcfName + "(" + (countConvertFile + 1) + ")";

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
                .append(saveName);

        System.out.println(">>" + line.toString());
        CommandLine commandLine = CommandLine.parse(line.toString());

        Executor executor = new DefaultExecutor();
        try {
            executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }


    private void tableAnnovar(String vcfName, Long countConvertFile) {

        String saveName =  vcfName + "(" + (countConvertFile + 1) + ")";

        StringBuilder protocols = new StringBuilder();
        protocols.append("refGene,").
                append("ljb23_sift,").
                append("ljb23_pp2hvar,").
                append("clinvar_20190305,").
                append("1000g2015aug_all,").
                append("exac03,").
                append("esp6500si_all ");

        StringBuilder line = new StringBuilder();
        line.append(perlConfiguration.getCommand())
                .append(" -protocol ")
                .append(protocols)
                .append(" -operation ")
                .append("g,f,f,f,f,f,f ")
                .append(" ")
                .append(perlConfiguration.getUploadedPath())
                .append(vcfName)
                .append(" ")
                .append(perlConfiguration.getAnnovarDB())
                .append(" -outfile ")
                .append(perlConfiguration.getConvertPath())
                .append(saveName);


        System.out.println(">>" + line);
        CommandLine commandLine = CommandLine.parse(line.toString());

        Executor executor = new DefaultExecutor();
        try {
            executor.execute(commandLine);
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }

    }


    private VcfLines getContent(List<String> fileContentList,List<String>selectedHeaders) {

        VcfLines list = new VcfLines();
        List<String> headers = Arrays.asList(fileContentList.get(0).split("\t"));

        for (int i = 1; i < fileContentList.size(); i++) {

            List<String> temp = Arrays.asList(fileContentList.get(i).split("\t"));
            VcfLine vcfLine = new VcfLine(headers, temp, selectedHeaders);

            list.add(vcfLine);
        }//end for
        return list;
    }

    private VcfLines getFilterHeaderContent(List<String> fileContentList, List<String> selectedHeaders){
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
    private void deleteFiles(String fakeName, Long countConvertFile) throws Exception {
        String saveName =  fakeName + "(" + (countConvertFile + 1) + ")";

        File deleteVcf = new File("C:/convertFile/" + saveName + ".hg19_multianno.vcf");
        File deleteAvinput = new File("C:/convertFile/" + saveName + ".avinput");

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

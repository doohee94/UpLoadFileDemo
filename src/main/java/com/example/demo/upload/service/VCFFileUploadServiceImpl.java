package com.example.demo.upload.service;

import com.example.demo.upload.common.PaginationUtil;
import com.example.demo.upload.entity.ConvertFileEntity;
import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.dto.Cols;
import com.example.demo.upload.entity.dto.Filter;
import com.example.demo.upload.entity.dto.VcfLine;
import com.example.demo.upload.entity.dto.VcfLines;
import com.example.demo.upload.repository.ConvertFileRepository;
import com.example.demo.upload.repository.FileUploadRepository;
import com.example.demo.upload.repository.FileUploadRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VCFFileUploadServiceImpl implements VCFFileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final ConvertFileRepository convertFileRepository;

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
        Page<FileEntity> fileList = fileUploadRepository.findByPersonId(personId, pageable);
        return fileList;
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
        Page<ConvertFileEntity> list = convertFileRepository.findAllByOriginFileidx(VCFFileIdx, pageable);
        return list;
    }

    @Override
    public Page<?> getConvertFile(int fileIdx, Pageable pageable) throws Exception {

        ConvertFileEntity convertFileEntity = convertFileRepository.findByFileIdx(fileIdx);

        Path path = Paths.get(convertFileEntity.getAttachmentUrl());
        List<String> fileContentList = Files.readAllLines(path);

        VcfLines list = getContent(fileContentList);

        Page<?> pageList = list.convertPagination(pageable);

        return pageList;
    }

    @Override
    public Page<?> getFilteredList(List<Filter> filters, int fileIdx, Pageable pageable) throws Exception {

        ConvertFileEntity convertFileEntity = convertFileRepository.findByFileIdx(fileIdx);

        Path path = Paths.get(convertFileEntity.getAttachmentUrl());
        List<String> fileContentList = Files.readAllLines(path);

        VcfLines list = getContent(fileContentList);

        List<VcfLine> filteredList = null;

        for (Filter filter : filters) {
            filteredList = list.filter(filter);
            VcfLines temp = new VcfLines(filteredList);
            list = temp;
        }

        Page<?> pageList = PaginationUtil.convertListToPage(filteredList, pageable);
        return pageList;
    }


    /**
     * methods
     */
    //table_annvar실행
    private void tableAnnovar(String vcfName, Long countConvertFile) {
        String saveName = "";
        if (countConvertFile > 0) {
            saveName = vcfName + "(" + (countConvertFile + 1) + ")";
        }

        List<String> list = new ArrayList<>();
        list.add("perl ");
        list.add("C:/annovar/table_annovar.pl ");
        list.add("C:/uploadedFile/" + vcfName + " ");
        list.add("C:/annovar/humandb/ ");
        list.add("-buildver ");
        list.add("hg19 ");
        list.add("-outfile ");
        list.add("C:/convertFile/" + saveName + " ");
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

    private void tableAnnovarDBSelect(List<String> dbList, String vcfName, Long countConvertFile) {
        String saveName = "";
        if (countConvertFile > 0) {
            saveName = vcfName + "(" + (countConvertFile + 1) + ")";
        }

        List<String> list = new ArrayList<>();
        list.add("perl ");
        list.add("C:/annovar/table_annovar.pl ");
        list.add("C:/uploadedFile/" + vcfName + " ");
        list.add("C:/annovar/humandb/ ");
        list.add("-buildver ");
        list.add("hg19 ");
        list.add("-outfile ");
        list.add("C:/convertFile/" + saveName + " ");
        list.add("-remove ");
        list.add(" ");
        list.add("-nastring ");
        list.add(". ");
        list.add("-vcfinput ");
        list.add("-polish ");
        list.add("-xref ");
        list.add("C:/annovar/example/gene_xref.txt ");
        list.add("-protocol ");
        for (int i = 0; i < dbList.size(); i++) {
            list.add(dbList.get(i));
            if (i != dbList.size() - 1) {
                list.add(",");
            }
        }
        list.add(" ");
        list.add("-operation ");
        for (int i = 0; i < dbList.size(); i++) {
            list.add(dbList.get(i).equals("refGene") ? "g" : "f");
            if (i != dbList.size() - 1) {
                list.add(",");
            }
        }

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

    private VcfLines getContent(List<String> fileContentList) {

        VcfLines list = new VcfLines();
        List<String> headers = Arrays.asList(fileContentList.get(0).split("\t"));

        for (int i = 1; i < fileContentList.size(); i++) {

            List<String> temp = Arrays.asList(fileContentList.get(i).split("\t"));
            VcfLine vcfLine = new VcfLine(headers, temp);

            list.add(vcfLine);
        }//end for
        return list;
    }

    //생성된 파일 삭제
    private void deleteFiles(String fakeName, Long countConvertFile) throws Exception {

        String saveName = "";
        if (countConvertFile > 0) {
            saveName = fakeName + "(" + (countConvertFile + 1) + ")";
        }


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
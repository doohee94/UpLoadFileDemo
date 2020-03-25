package com.example.demo.upload.service;

import com.example.demo.upload.entity.ConvertFileEntity;
import com.example.demo.upload.entity.FileEntity;

import com.example.demo.upload.entity.dto.Filter;
import com.example.demo.upload.entity.dto.VcfLines;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface VCFFileUploadService {
    void fileUpload(MultipartFile file) throws Exception;
    Page<FileEntity> VCFfileList(int personId, Pageable pageable) throws Exception;
    void VCFFileConvert(int VCFFileIdx, String inputFileName) throws Exception;
    void VCFFileConvertSelectDB(List<String> dbList,int VCFFileIdx, String inputFileName ) throws Exception;
    Page<ConvertFileEntity> convertFileList(int VCFFileIdx,Pageable pageable) throws Exception;
    Page<?> getConvertFile(int getConvertFile,Pageable pageable) throws Exception;
    Page<?> getFilteredList(List<Filter> filters, int fileIdx, Pageable pageable) throws Exception;

}

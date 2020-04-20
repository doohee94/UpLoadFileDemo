package com.example.demo.upload.service;
import com.example.demo.upload.entity.AnnovarDBHeader;
import com.example.demo.upload.entity.AnnovarDBInformation;
import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PresetEntity;
import com.example.demo.upload.entity.dto.FilterList;
import com.example.demo.upload.entity.dto.Header;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface VCFFileUploadService {
    void fileUpload(MultipartFile file) throws Exception;
    Page<FileEntity> vcfFileList(int personId, Pageable pageable) throws Exception;
    void analyze(List<String> dbList,int vcfFileIdx) throws Exception;
    Page<?> getAnalyzedList(FilterList filters, List<String>selectedHeaders, int fileIdx, Pageable pageable) throws Exception;
    List<AnnovarDBInformation> getAnnovarInformation(int vcfFileIdx, List<String> selectedHeaders, Pageable pageable) throws Exception;
    List<PresetEntity> getPresetList(int personId);

}

package com.shop.service;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileDate) throws Exception {
        //UUID 서로 다른 객체를 구별하기 위해 이름 부여할 때 사용
        //파일명 중복 해결 가능
        UUID uuid = UUID.randomUUID();

        //test.jpg 확장자 읽기
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        log.info("extension : " + extension);

        //확장자 조합해 저장될 파일 이름 만들기
        String saveFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + saveFileName; //C:/shop/item/1422c025-3386-4c93-aa00-e16905996ed3.jpg

        //생성자로 파일이 저장될 위치와 파일 이름을 넘겨 파일에 쓸 파일 출력 스트림을 만듥
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileDate); //fileDate를 파일 출력 스트림에 입력
        fos.close();
        return saveFileName;    //업로드 된 파일 이름 반환

    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);   //파일 저잘 경로 이용해 파일 객체 생성

        if(deleteFile.exists()){ //파일이 존재하면 삭제
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        }else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

}

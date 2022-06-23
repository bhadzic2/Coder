package ba.fancy.coder.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CoderService {

    public String compress(MultipartFile file) throws IOException, Exception;
    public String decompress(MultipartFile file) throws Exception;

}

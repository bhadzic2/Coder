package ba.fancy.coder.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class DownloadController {

    @GetMapping("/download/{path}")
    @ResponseBody
    /**
     * Downloads the file by name from the temp directory
     * TODO: handle file path validation, file exists checks, etc
     */
    public FileSystemResource download(@PathVariable String path, HttpServletResponse response) throws IOException {

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename="+path+".txt");
        String tmpdir = System.getProperty("java.io.tmpdir");
        return new FileSystemResource(new File(tmpdir,path));
    }
}

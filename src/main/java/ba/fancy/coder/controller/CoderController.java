package ba.fancy.coder.controller;

import ba.fancy.coder.exception.ServiceException;
import ba.fancy.coder.service.CoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CoderController {

    @Autowired
    CoderService coderService;

    @PostMapping("/compress")
    /**
     * Compresses the provided file
     * TODO: handle input validations (size, empty, etc)
     * @returns the id of the compressed file - file can be downloaded using /download/:id endpoint
     */
    public ResponseEntity<String> compress(@RequestParam("file") MultipartFile file){

        try {
            return ResponseEntity.status(HttpStatus.OK).body(coderService.compress(file));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/decompress")
    /**
     * Decompresses the provided file
     * TODO: handle input validations (size, empty, etc)
     * @returns the id of the decompressed file - file can be downloaded using /download/:id endpoint
     */
    public ResponseEntity<String> decompress(@RequestParam("file") MultipartFile file){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(coderService.decompress(file));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

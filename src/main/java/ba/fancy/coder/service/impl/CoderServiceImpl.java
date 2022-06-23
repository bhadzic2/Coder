package ba.fancy.coder.service.impl;

import ba.fancy.coder.exception.InternalServiceException;
import ba.fancy.coder.exception.ServiceException;
import ba.fancy.coder.service.CoderService;
import ba.fancy.coder.tool.ArithmeticCoding;
import ba.fancy.coder.tool.Freq;
import ba.fancy.coder.tool.Triple;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class CoderServiceImpl implements CoderService {

    @Override
    /**
     * Compresses the file content using arithmetic coding and stores the output in a temp file
     * Compressed file will have four sections, separated by '.'
     * Section 1 - the arithmetic coding frequency table - list of "charCode:frequency" separated by ','
     * Section 2 - the arithmetic coding power (integer)
     * Section 3 - the compressed content (big integer)
     * Section 4 - the sha1 hex code of the original file content
     * @returns compressed file name
     */
    public String compress(MultipartFile file) throws Exception {

        try {
            String content = new String(file.getBytes());

            String fileName = UUID.randomUUID().toString();
            String tmpdir = System.getProperty("java.io.tmpdir");
            FileWriter writer = new FileWriter(new File(tmpdir,fileName));

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            var compressFuture = executorService.submit(() -> {
                return ArithmeticCoding.encode(content, 10L);
            });
            var digestFuture = executorService.submit(() -> {
                return DigestUtils.sha1Hex(content);
            });
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            Triple<BigInteger, Integer, Freq> encoded = compressFuture.get();
            String digest = digestFuture.get();


            int cnt=0;
            for (char c : encoded.c.keySet()){
                if (cnt!=0){
                    writer.write(",");
                }
                writer.write(((int)c+""));
                writer.write(":");
                writer.write(encoded.c.get(c).toString());
                cnt++;
            }
            writer.write(".");

            writer.write(((int)encoded.b)+"");
            writer.write(".");

            writer.write(encoded.a.toString());
            writer.write(".");
            writer.write(digest);
            writer.close();
            return fileName;
        } catch (InterruptedException | ExecutionException e) {
            throw e;
        }
    }

    @Override
    /**
     * Decompressed a file produced by the compress method and stores the output in a temp file
     * @throws ServiceException for any file validation errors
     * @throws InternalServiceException for any other unexpected errors
     * @returns decompressed file name
     */
    public String decompress(MultipartFile file) throws Exception {

        try {
            String content = new String(file.getBytes());
            var parts = content.split("\\.");
            if (parts.length!=4){
                throw new ServiceException("Invalid file input");
            }
            String freqList = parts[0];
            Integer pwr = Integer.parseInt(parts[1]);
            String encodedContent = parts[2];
            String originalDigest = parts[3];
            BigInteger contentInt = new BigInteger(encodedContent);

            Freq freq = new Freq();
            for (String f : freqList.split(",")) {
                var freqParts = f.split(":");
                freq.put((char) Integer.parseInt(freqParts[0]), Long.parseLong(freqParts[1]));
            }

            String decodedContent = ArithmeticCoding.decode(contentInt, 10L, pwr, freq);
            String digest = DigestUtils.sha1Hex(decodedContent);
            if (!originalDigest.equals(digest)) {
                throw new ServiceException("Decoded content does not match the digest signature");
            }
            String fileName = UUID.randomUUID().toString();
            String tmpdir = System.getProperty("java.io.tmpdir");
            FileWriter writer = new FileWriter(new File(tmpdir, fileName));
            writer.write(decodedContent);
            writer.close();
            return fileName;
        } catch (ServiceException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new ServiceException("Invalid file content");
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServiceException(e.getMessage());
        }
    }
}

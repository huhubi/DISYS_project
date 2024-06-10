package com.fhtechnikum.project.project;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;
@RestController
@RequestMapping("/invoices")
public class RESTAPI {
    SendMessage s1 = new SendMessage();

    @PostMapping("/{id}")
    public String post(@RequestBody String id2, @PathVariable String id) {
        s1.send(id);
        return id;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> get(@PathVariable String id) throws FileNotFoundException {
        File pdfFile = new File("Files\\" + id + ".pdf");
        if (!pdfFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + id + ".pdf");
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(pdfFile));

            long creationDate = pdfFile.lastModified();
            headers.add("X-Creation-Date", String.valueOf(creationDate));

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(inputStreamResource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
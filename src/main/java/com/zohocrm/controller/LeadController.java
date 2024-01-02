package com.zohocrm.controller;

import com.zohocrm.entity.Lead;
import com.zohocrm.payload.EmailDto;
import com.zohocrm.payload.LeadDto;
import com.zohocrm.service.LeadService;
import com.zohocrm.service.impl.ExcelHelperService;
import com.zohocrm.service.impl.PDFHelperService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ResponseEntity<LeadDto> createLead(@RequestBody LeadDto leadDto){
        LeadDto dto = leadService.createLead(leadDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{lid}")
    public ResponseEntity<String> deleteLeadbyId(@PathVariable String lid){
        leadService.deleteLeadById(lid);
        return new ResponseEntity<>("Lead is Deleted",HttpStatus.OK);
    }

    @GetMapping
    public List<LeadDto> getAllLeads(){
        List<LeadDto> leads = leadService.getAllLeads();
        return leads;
    }

    //http://localhost:8080/api/leads/excelReports
    @GetMapping("/excelReports")
    public  ResponseEntity<Resource> getLeadsExcelReports(){
        List<Lead> leads = leadService.getLeadsExcelReports();
        ByteArrayInputStream leadReports = ExcelHelperService.leadsToExcel(leads);
        String filename = "leads.xlsx";
        InputStreamResource file = new InputStreamResource(leadReports);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping(value = "/leadPDFReports", produces =
            MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> employeeReport()
            throws IOException {
        List<Lead> leads =  leadService.getLeadsExcelReports();


        ByteArrayInputStream pdfReport = PDFHelperService.employeePDFReport(leads);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=leads.pdf");

        return ResponseEntity.ok().headers(headers).contentType
                        (MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfReport));
    }
}




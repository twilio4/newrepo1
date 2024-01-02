package com.zohocrm.service.impl;

import com.zohocrm.entity.Lead;
import com.zohocrm.exception.LeadExist;
import com.zohocrm.payload.LeadDto;
import com.zohocrm.repository.LeadRepository;
import com.zohocrm.service.LeadService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeadServiceImpl implements LeadService {

    private LeadRepository leadrepo;

    private ModelMapper modelMapper;

    public LeadServiceImpl(LeadRepository leadrepo, ModelMapper modelMapper) {
        this.leadrepo = leadrepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public LeadDto createLead(LeadDto leadDto) {

        boolean emailExists = leadrepo.existsByEmail(leadDto.getEmail());
        boolean mobileExists = leadrepo.existsByMobile(leadDto.getMobile());

        if(emailExists){
            throw new LeadExist("email exist - "+leadDto.getEmail());
        }
        if(mobileExists){
            throw new LeadExist("mobile exist - "+leadDto.getMobile());
        }

        Lead lead = mapToEntity(leadDto);
        String lid = UUID.randomUUID().toString();
        lead.setLid(lid);

        Lead savedlead = leadrepo.save(lead);
        LeadDto dto = mapToDto(savedlead);
        return dto;
    }

    @Override
    public void deleteLeadById(String lid) {
        leadrepo.findById(lid).orElseThrow(
                () -> new LeadExist("Lead with this id not present  - " + lid)
        );
        leadrepo.deleteById(lid);
    }

    @Override
    public List<LeadDto> getAllLeads() {
        List<Lead> leads = leadrepo.findAll();
        return leads.stream().map(lead->mapToDto(lead)).collect(Collectors.toList());

    }

    @Override
    public List<Lead> getLeadsExcelReports() {
        return leadrepo.findAll();
    }

    Lead mapToEntity(LeadDto leadDto){
        Lead lead = modelMapper.map(leadDto, Lead.class);
        return lead;
    }

    LeadDto mapToDto(Lead lead){
        LeadDto leadDto = modelMapper.map(lead, LeadDto.class);
        return leadDto;

    }
}

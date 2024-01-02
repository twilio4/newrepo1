package com.zohocrm.service.impl;

import com.zohocrm.entity.Contact;
import com.zohocrm.entity.Email;
import com.zohocrm.entity.Lead;
import com.zohocrm.exception.ContactExist;
import com.zohocrm.exception.LeadExist;
import com.zohocrm.payload.EmailDto;
import com.zohocrm.repository.ContactRepository;
import com.zohocrm.repository.EmailRepository;
import com.zohocrm.repository.LeadRepository;
import com.zohocrm.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService {


    private JavaMailSender javaMailSender;


    private ModelMapper modelMapper;

    private EmailRepository emailRepo;

    private LeadRepository leadRepo;

    private ContactRepository contactRepo;

    public EmailServiceImpl(JavaMailSender javaMailSender, ModelMapper modelMapper, EmailRepository emailRepo, LeadRepository leadRepo, ContactRepository contactRepo) {
        this.javaMailSender = javaMailSender;
        this.modelMapper = modelMapper;
        this.emailRepo = emailRepo;
        this.leadRepo = leadRepo;
        this.contactRepo = contactRepo;
    }

    @Override
    public EmailDto sendEmail(EmailDto emailDto) {
        Lead lead = leadRepo.findByEmail(emailDto.getTo()).orElseThrow(
                () -> new LeadExist("Email Id Not registered - " + emailDto.getTo())
        );

//       Contact contact = contactRepo.findByEmail(emailDto.getTo()).orElseThrow(
//                () -> new ContactExist("Email Id Not registered - " + emailDto.getTo())
//        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.getTo());
        message.setSubject(emailDto.getSubject());
        message.setText(emailDto.getMessage());

        javaMailSender.send(message);

        Email email = mapToEntity(emailDto);
        String eid = UUID.randomUUID().toString();
        email.setEid(eid);

        Email sentEmail = emailRepo.save(email);
        return mapToDto(sentEmail);

    }

    Email mapToEntity(EmailDto emailDto){
        Email email = modelMapper.map(emailDto, Email.class);
        return email;
    }

    EmailDto mapToDto(Email email){
        EmailDto dto = modelMapper.map(email, EmailDto.class);
        return dto;
    }
}

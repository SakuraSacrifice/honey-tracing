package com.honey.tracing.example.controller;

import com.honey.tracing.example.entity.People;
import com.honey.tracing.example.mapper.PeopleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyBatisController {

    @Autowired
    private PeopleMapper peopleMapper;

    @GetMapping("/mybatis/select")
    public People selectOne(@RequestParam("peopleName") String peopleName,
                            @RequestParam("peopleAge") int peopleAge) {
        return peopleMapper.selectOne(peopleName, peopleAge);
    }

}
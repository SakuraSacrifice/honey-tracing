package com.honey.tracing.example.mapper;

import com.honey.tracing.example.entity.People;
import org.apache.ibatis.annotations.Param;

public interface PeopleMapper {

    People selectOne(@Param("peopleName") String peopleName,
                     @Param("peopleAge") int peopleAge);

}
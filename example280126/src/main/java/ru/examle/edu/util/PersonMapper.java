package ru.examle.edu.util;

import lombok.experimental.UtilityClass;
import ru.examle.edu.dto.PersonDTO;
import ru.examle.edu.entity.Person;

@UtilityClass
public class PersonMapper {
    public PersonDTO convertToDto(Person person) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(person.getId());
        personDTO.setName(person.getName());
        personDTO.setEmail(person.getEmail());
        personDTO.setPhotoUrl(person.getPhotoUrl());
        personDTO.setDepartmentName(person.getDepartment().getName());
        return personDTO;
    }
}

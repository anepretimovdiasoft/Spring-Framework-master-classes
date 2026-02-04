package ru.examle.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.examle.edu.dto.PersonDTO;
import ru.examle.edu.dto.PersonRegisterDto;
import ru.examle.edu.entity.Authority;
import ru.examle.edu.entity.Department;
import ru.examle.edu.entity.Person;
import ru.examle.edu.exception.DepartmentNotFoundException;
import ru.examle.edu.exception.PersonAlreadyExistsException;
import ru.examle.edu.exception.PersonNotFoundException;
import ru.examle.edu.repository.AuthorityRepository;
import ru.examle.edu.repository.DepartmentRepository;
import ru.examle.edu.repository.PersonRepository;
import ru.examle.edu.service.PersonService;
import ru.examle.edu.util.PersonMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<PersonDTO> getAllPersons() {

        return personRepository.findAll().stream()
                .map(PersonMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PersonDTO getPersonById(Long id) {
        return personRepository.findById(id).map(PersonMapper::convertToDto)
                .orElseThrow(() -> new PersonNotFoundException("Person not found!!!"));
    }

    @Override
    public PersonDTO createPerson(PersonRegisterDto dto) {

        if (personRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new PersonAlreadyExistsException("Username already exists");
        }

        Optional<Department> optionalDepartment = departmentRepository.findByName(dto.getDepartmentName());
        if (optionalDepartment.isEmpty()) {
            throw new DepartmentNotFoundException("Department not found!!!");
        }

        Optional<Authority> roleUser = authorityRepository.findByAuthority("ROLE_USER");
        if (roleUser.isEmpty()) {
            throw new RuntimeException("Authority not found");
        }

        Person person = new Person();
        person.setName(dto.getName());
        person.setUsername(dto.getUsername());
        person.setEmail(dto.getEmail());
        person.setDepartment(optionalDepartment.get());
        person.setPassword(passwordEncoder.encode(dto.getPassword()));
        person.setAuthorities(Set.of(roleUser.get()));

        return PersonMapper.convertToDto(personRepository.save(person));
    }

    @Override
    public PersonDTO updatePerson(Long id, PersonDTO dto) {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException("Person not found!"));

        if (personRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new PersonAlreadyExistsException("Username already exists");
        }

        person.setName(dto.getName());
        person.setUsername(dto.getUsername());
        person.setEmail(dto.getEmail());
        person.setPhotoUrl(dto.getPhotoUrl());

        Optional<Department> department = departmentRepository.findByName(dto.getDepartmentName());
        department.ifPresent(person::setDepartment);

        return PersonMapper.convertToDto(personRepository.save(person));
    }

    @Override
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    @Override
    public PersonDTO getPersonByUsername(String username) {
        Optional<Person> optionalPerson = personRepository.findByUsername(username);

        if (optionalPerson.isEmpty()) {
            throw new PersonNotFoundException("Person with username " + username + " not found!");
        }

        return PersonMapper.convertToDto(optionalPerson.get());
    }

    @Override
    public Page<PersonDTO> getAllPersonPaginated(Pageable pageable) {
        return personRepository.findAll(pageable).map(PersonMapper::convertToDto);
    }
}

package com.chris.service;

import com.chris.dao.MemberDaoImpl;
import com.chris.dto.GymMemberDto;
import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static com.chris.util.AppBeanConstant.APP_SERVICE_BEAN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/test.properties")
@SpringBootTest
class MemberCrudServiceImplTest {
    @MockBean
    private MemberDaoImpl _daoImpl;

    @Autowired
    @Qualifier(value = APP_SERVICE_BEAN)
    private MemberCrudService _service;


    @Order(1)
    @Test
    public void testFindAllMembers() {
        GymMemberEntity entity1 = new GymMemberEntity(1, "chris", "yang", "kyang3@lakeheadu.ca");
        GymMemberEntity entity2 = new GymMemberEntity(2, "kuo", "yang", "ykuo2014@outlook.com");

        when(_daoImpl.findAllMembers()).thenReturn(
                Arrays.asList(entity1, entity2)
        );

        List<GymMemberDto> dtoList = _service.findAllMembers();

        assertTrue(dtoList.get(0).getFirstName().equals("chris"));
        assertTrue(dtoList.get(0).getLastName().equals("yang"));
        assertTrue(dtoList.get(0).getEmail().equals("kyang3@lakeheadu.ca"));

        assertTrue(dtoList.get(1).getFirstName().equals("kuo"));
        assertTrue(dtoList.get(1).getLastName().equals("yang"));
        assertTrue(dtoList.get(1).getEmail().equals("ykuo2014@outlook.com"));
    }

    @Order(2)
    @Test
    public void testFindMemberById() {
        when(_daoImpl.findMemberById(100)).thenReturn
                (new GymMemberEntity(100, "chris", "yang", "kyang3@lakeheadu.ca"));

        GymMemberDto dto = _service.findMemberById(100);

        assertTrue(dto.getFirstName().equals("chris"));
        assertTrue(dto.getLastName().equals("yang"));
        assertTrue(dto.getEmail().equals("kyang3@lakeheadu.ca"));
    }


    @Order(3)
    @Test
    public void testFindMemberByIdException() {
        doThrow(new AppServiceException("invalid id")).when(_daoImpl).findMemberById(100);

        assertThrows(AppServiceException.class, () -> {
            _service.findMemberById(100);
        });
    }

    @Order(4)
    @Test
    public void testFindMemberByEmail() {
        String email = "ykuo2014@outlook.com";

        when(_daoImpl.findMemberByEmail(email))
                .thenReturn(new GymMemberEntity(100, "chris", "yang", email));

        GymMemberDto dto = _service.findMemberByEmail(email);
        System.out.println("dto: " + dto.toString());

        assertTrue(dto.getEmail().equals(email));
    }

    @Order(5)
    @Test
    public void testSaveMember() {
        GymMemberDto dto = new GymMemberDto("chris", "yang", "kyang3@lakeheadu.ca");

        doCallRealMethod().when(_service).saveMember(dto);

        ???
    }
}
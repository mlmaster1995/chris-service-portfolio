package com.chris.service;

import com.chris.dao.MemberDaoImpl;
import com.chris.dto.GymMemberDto;
import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static com.chris.util.AppBeanConstant.MEMBER_SERVICE_BEAN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/test.properties")
@SpringBootTest
class MemberCrudServiceImplTest {
    @Autowired
    private JdbcTemplate _template;

    @SpyBean
    private MemberDaoImpl _daoImplSpy;

    @Autowired
    @Qualifier(value = MEMBER_SERVICE_BEAN)
    private MemberCrudService _service;

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from member");
    }


    @Order(1)
    @Test
    public void testDeleteMember() {
        GymMemberDto dto = new GymMemberDto("chris", "yang", "kyang3@lakeheadu.ca");
        doCallRealMethod().when(_daoImplSpy).saveMember(dto.toEntity());
        doCallRealMethod().when(_daoImplSpy).deleteMemberById(1);

        _service.saveMember(dto);

        _service.findAllMembers().stream().forEach(x -> {
            System.out.println("dto: " + x.toString());
        });

        _service.deleteMemberById(1);

        assertTrue(_service.findAllMembers().size() == 0);
    }

    @Order(1)
    @Test
    public void testFindAllMembers() {
        GymMemberEntity entity1 = new GymMemberEntity(1, "chris", "yang", "kyang3@lakeheadu.ca");
        GymMemberEntity entity2 = new GymMemberEntity(2, "kuo", "yang", "ykuo2014@outlook.com");


        when(_daoImplSpy.findAllMembers()).thenReturn(Arrays.asList(entity1, entity2));

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
        when(_daoImplSpy.findMemberById(100)).thenReturn
                (new GymMemberEntity(100, "chris", "yang", "kyang3@lakeheadu.ca"));

        GymMemberDto dto = _service.findMemberById(100);

        assertTrue(dto.getFirstName().equals("chris"));
        assertTrue(dto.getLastName().equals("yang"));
        assertTrue(dto.getEmail().equals("kyang3@lakeheadu.ca"));
    }


    @Order(3)
    @Test
    public void testFindMemberByIdException() {
        doThrow(new AppServiceException("invalid id")).when(_daoImplSpy).findMemberById(100);

        assertThrows(AppServiceException.class, () -> {
            _service.findMemberById(100);
        });
    }

    @Order(4)
    @Test
    public void testFindMemberByEmail() {
        String email = "ykuo2014@outlook.com";

        doReturn(new GymMemberEntity(100, "chris", "yang", email)).when(_daoImplSpy).findMemberByEmail(email);

        GymMemberDto dto = _service.findMemberByEmail(email);
        System.out.println("dto: " + dto.toString());

        assertTrue(dto.getEmail().equals(email));
    }

    @Order(5)
    @Test
    public void testSaveMember() {
        MemberDaoImpl _daoImplMock = mock(MemberDaoImpl.class);
        GymMemberDto dto = new GymMemberDto("chris", "yang", "kyang3@lakeheadu.ca");
        doCallRealMethod().when(_daoImplMock).saveMember(dto.toEntity());
        doCallRealMethod().when(_daoImplMock).findAllMembers();
        _service.saveMember(dto);

        List<GymMemberDto> dtos = _service.findAllMembers();
        dtos.stream().forEach(x -> {
            System.out.println("dto: " + x.toString());
            assertTrue(x.getFirstName().equals("chris"));
            assertTrue(x.getLastName().equals("yang"));
            assertTrue(x.getEmail().equals("kyang3@lakeheadu.ca"));
        });
    }

    @Order(6)
    @Test
    public void testUpdateMember() {
        GymMemberDto dto = new GymMemberDto("chris", "yang", "kyang3@lakeheadu.ca");
        doCallRealMethod().when(_daoImplSpy).saveMember(dto.toEntity());
        doCallRealMethod().when(_daoImplSpy).updateMember(dto.toEntity());
        doCallRealMethod().when(_daoImplSpy).findAllMembers();

        _service.saveMember(dto);
        _service.findAllMembers().stream().forEach(x -> {
            System.out.println("old dto: " + x.toString());
            assertTrue(x.getFirstName().equals("chris"));
            assertTrue(x.getLastName().equals("yang"));
            assertTrue(x.getEmail().equals("kyang3@lakeheadu.ca"));
        });

        dto = _service.findMemberByEmail("kyang3@lakeheadu.ca");
        dto.setFirstName("kuo");
        dto.setLastName("yang");
        dto.setEmail("ykuo2014@outlook.com");
        _service.updateMember(dto);
        _service.findAllMembers().stream().forEach(x -> {
            System.out.println("new dto: " + x.toString());
            assertTrue(x.getFirstName().equals("kuo"));
            assertTrue(x.getLastName().equals("yang"));
            assertTrue(x.getEmail().equals("ykuo2014@outlook.com"));
        });
    }


}
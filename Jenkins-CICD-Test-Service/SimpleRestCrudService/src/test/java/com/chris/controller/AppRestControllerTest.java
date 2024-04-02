package com.chris.controller;

import com.chris.dto.GymMemberDto;
import com.chris.exception.AppServiceException;
import com.chris.service.MemberCrudServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static com.chris.util.AppBeanConstant.APP_CONTROLLER_BEAN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class AppRestControllerTest {
    @Autowired
    private MockMvc _mockMvc;

    @Autowired
    private JdbcTemplate _template;

    @Autowired
    private ObjectMapper _mapper;

    @SpyBean
    private MemberCrudServiceImpl _memberService;

    @Autowired
    @Qualifier(value = APP_CONTROLLER_BEAN)
    private AppRestController _appController;

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from member");
    }

    @Order(1)
    @Test
    public void testFindMemberById() throws Exception {
        _template.execute("INSERT INTO `member` (`first_name`,`last_name`,`email`) VALUES ('Neil','Hamilton','neil@chrismember.ca')");

        Integer memberId = 1;
        doCallRealMethod().when(_memberService).findMemberById(memberId);

        //from controller
        ResponseEntity<GymMemberDto> entity = _appController.findMemberById(memberId);
        assertTrue(entity.getStatusCode().value() == 200);
        assertTrue(entity.getBody().getFirstName().equals("Neil"));
        assertTrue(entity.getBody().getLastName().equals("Hamilton"));
        assertTrue(entity.getBody().getEmail().equals("neil@chrismember.ca"));

        //from client
        _mockMvc.perform(get("/api/v1/members/{memberId}", memberId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("neil@chrismember.ca")));
    }

    @Order(2)
    @Test
    @Sql("/insert-member-data-part.sql")
    public void testFindAllMembersEp() throws Exception {
        //from client
        _mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)));

        //from controller
        doCallRealMethod().when(_memberService).findAllMembers();
        ResponseEntity<List<GymMemberDto>> ans = _appController.findAllMembers();
        ans.getBody().stream().forEach(x -> System.out.println("body data: " + x.toString()));
        System.out.println(ans.toString());
        assertTrue(ans.getStatusCode().value() == 200);
        assertTrue(ans.getBody().size() == 5);
    }


    @Order(3)
    @Test
    @Sql("/insert-member-data-part.sql")
    public void testFindAllMembersException() throws Exception {
        doThrow(new AppServiceException("mocked error at service layer")).when(_memberService).findAllMembers();

        //from controller layer
        ResponseEntity<List<GymMemberDto>> ans = _appController.findAllMembers();
        assertTrue(ans.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        assertNull(ans.getBody());

        //from client
        _mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members")).andExpect(status().isBadRequest());
    }

    @Order(4)
    @Test
    public void testFindMemberByEmail() throws Exception {
        _template.execute("INSERT INTO `member` (`first_name`,`last_name`,`email`) VALUES ('Neil','Hamilton','neil@chrismember.ca')");

        String bodyInStr = "{\"email\":\"neil@chrismember.ca\"}";
        _mockMvc.perform(post("/api/v1/member/email")
                        .contentType(MediaType.APPLICATION_JSON).content(bodyInStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Neil")))
                .andExpect(jsonPath("$.lastName", is("Hamilton")))
                .andExpect(jsonPath("$.email", is("neil@chrismember.ca")));
    }

    @Order(5)
    @Test
    public void testSaveMember() throws Exception {
        doCallRealMethod().when(_memberService).findMemberByEmail("neil@chrismember.ca");

        GymMemberDto dto = GymMemberDto.builder()
                .firstName("chris")
                .lastName("yang")
                .email("kyang3@lakeheadu.ca")
                .build();

        _mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")));

        dto = _memberService.findMemberByEmail("kyang3@lakeheadu.ca");

        assertTrue(dto.getFirstName().equals("chris"));
        assertTrue(dto.getLastName().equals("yang"));
        assertTrue(dto.getEmail().equals("kyang3@lakeheadu.ca"));
    }

    @Order(6)
    @Test
    public void testUpdateMember() throws Exception {
        _template.execute("INSERT INTO `member` (`first_name`,`last_name`,`email`) VALUES ('Neil','Hamilton','neil@chrismember.ca')");

        doCallRealMethod().when(_memberService).findMemberByEmail("neil@chrismember.ca");
        GymMemberDto dto = _memberService.findMemberByEmail("neil@chrismember.ca");

        dto.setFirstName("kuo");
        dto.setLastName("yang");
        dto.setEmail("ykuo2014@outlook.com");

        _mockMvc.perform(put("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(_mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")));

        dto = _memberService.findMemberByEmail("ykuo2014@outlook.com");
        assertTrue(dto.getFirstName().equals("kuo"));
        assertTrue(dto.getLastName().equals("yang"));
        assertTrue(dto.getEmail().equals("ykuo2014@outlook.com"));
    }


    @Order(7)
    @Test
    public void testDeleteMemberById() throws Exception {
        doCallRealMethod().when(_memberService).findAllMembers();

        _template.execute("INSERT INTO `member` (`first_name`,`last_name`,`email`) VALUES ('Neil','Hamilton','neil@chrismember.ca')");

        List<GymMemberDto> dtos = _memberService.findAllMembers();

        dtos = dtos.stream().filter(x -> x.getEmail().equals("neil@chrismember.ca")).collect(Collectors.toList());
        assertTrue(dtos.get(0).getEmail().equals("neil@chrismember.ca"));

        int id = dtos.get(0).getId();

        _mockMvc.perform(delete("/api/v1/members/{memberId}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")));

        dtos = _memberService.findAllMembers();
        assertTrue(dtos.isEmpty());
    }


}
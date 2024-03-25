package com.chris.controller;

import com.chris.dto.GymMemberDto;
import com.chris.service.MemberCrudServiceImpl;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.chris.util.AppBeanConstant.APP_CONTROLLER_BEAN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/test.properties")
@SpringBootTest
class AppRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private MemberCrudServiceImpl _memberService;

    @Autowired
    @Qualifier(value = APP_CONTROLLER_BEAN)
    private AppRestController _appController;

    //ToDo: add json path
    @Test
    @Sql("/insert-data.sql")
    public void getFindAllMembersEp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @Sql("/insert-data.sql")
    public void getFindAllMembersController() {
        ResponseEntity<List<GymMemberDto>> ans = _appController.findAllMembers();
        assertTrue(ans.getStatusCode().value() == 200);
    }

    //ToDo: test exception handler
}
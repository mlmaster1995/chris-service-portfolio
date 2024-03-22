package com.chris.dao;

import com.chris.entity.GymMemberEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.chris.util.AppBeanConstant.MEMBER_DAO_IMPL_BEAN;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource("/test.properties")
@SpringBootTest
class MemberDaoImplTest {
    @Autowired
    private JdbcTemplate _template;

    @Autowired
    @Qualifier(value = MEMBER_DAO_IMPL_BEAN)
    private MemberDao _memberDaoImpl;

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from member");
    }

    @Sql("/insert-data.sql")
    @Test
    public void testFindAll() {
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        entities.forEach(member -> System.out.println(member.toString()));
        assertTrue(entities.size() == 6);
    }

    @Test
    public void testAddMember() {
        GymMemberEntity entity = new GymMemberEntity("chris", "yang", "kyang3@lakeheadu.ca");
        _memberDaoImpl.saveMember(entity);
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        assertTrue(entities.size() == 1);
    }

    //ToDo: mock the exceptions for all methods

}
































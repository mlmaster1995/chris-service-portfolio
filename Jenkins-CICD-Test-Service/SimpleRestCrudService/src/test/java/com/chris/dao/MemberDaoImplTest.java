/**
 * MIT License
 *
 * Copyright (c) 2024 Chris Yang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.chris.dao;

import com.chris.entity.GymMemberEntity;
import com.chris.exception.AppServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;

import static com.chris.util.AppBeanConstant.MEMBER_DAO_IMPL_BEAN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("/application.properties")
@SpringBootTest
class MemberDaoImplTest {
    @Autowired
    private JdbcTemplate _template;

    @Autowired
    @Qualifier(value = MEMBER_DAO_IMPL_BEAN)
    private MemberDao _memberDaoImpl;

    @BeforeEach
    public void beforeEachTest() {
        _template.execute("INSERT INTO `member` (`first_name`,`last_name`,`email`) " +
                "VALUES  ('chris'	,'yang', 'kyang3@lakeheadu.ca')");
    }

    @AfterEach
    public void afterEachTest() {
        _template.execute("delete from member");
    }

    @Order(1)
    @Sql("/insert-member-data-part.sql")
    @Test
    public void testFindMemberById() {
        GymMemberEntity entity = _memberDaoImpl.findMemberById(4);
        assertTrue(entity.getId() == 4);
    }


    @Order(2)
    @Sql("/insert-member-data-page.sql")
    @Test
    public void testFindAll() {
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        entities.forEach(member -> System.out.println(member.toString()));
        assertTrue(entities.size() == 5);
    }

    @Order(2)
    @Sql("/insert-member-data-page.sql")
    @Test
    public void testFindAllPage(){
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers(1, 2);
        System.out.println("size: " + entities.size());
        entities.stream().forEach(x -> System.out.println(x.toString()));

        assertTrue(entities.size() == 10);
    }


    @Order(3)
    @Test
    public void testSaveMember() {
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        assertTrue(entities.size() == 1);
    }

    @Order(3)
    @Test
    public void testUpdateMember1() {
        //save a tmp one
        GymMemberEntity entity = new GymMemberEntity();
        entity.setFirstName("kuo");
        entity.setLastName("yang");
        entity.setEmail("ykuo2014@outlook.com");
        _memberDaoImpl.updateMember(entity);

        //get the entity
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        List<GymMemberEntity> target = entities.stream().filter(x -> x.getFirstName().equals("kuo") &&
                x.getLastName().equals("yang")).collect(Collectors.toList());

        assertTrue(target.get(0).getFirstName().equals("kuo"));
        assertTrue(target.get(0).getLastName().equals("yang"));
        assertTrue(target.get(0).getEmail().equals("ykuo2014@outlook.com"));
    }

    @Order(3)
    @Test
    public void testUpdateMember2() {
        //save a tmp one
        GymMemberEntity entity = new GymMemberEntity();
        entity.setFirstName("json");
        entity.setLastName("test");
        entity.setEmail("jtest@outlook.com");
        _memberDaoImpl.updateMember(entity);

        //get the entity
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        List<GymMemberEntity> target = entities.stream().filter(x -> x.getFirstName().equals("json") &&
                x.getLastName().equals("test")).collect(Collectors.toList());

        assertTrue(target.get(0).getFirstName().equals("json"));
        assertTrue(target.get(0).getLastName().equals("test"));
        assertTrue(target.get(0).getEmail().equals("jtest@outlook.com"));
    }

    @Order(4)
    @Test
    public void testDeleteMemberById() {
        List<GymMemberEntity> entities = _memberDaoImpl.findAllMembers();
        GymMemberEntity entity = entities.get(0);
        System.out.println("entity: " + entity.toString());

        Integer id = entity.getId();
        _memberDaoImpl.deleteMemberById(id);

        entities = _memberDaoImpl.findAllMembers();
        assertTrue(entities.isEmpty());
    }

    @Order(4)
    @Test
    public void testDeleteMemberById2() {
        assertThrows(AppServiceException.class, () -> {
            _memberDaoImpl.deleteMemberById(1000);
        });
    }

    @Order(5)
    @Test
    public void testFindMemberByEmail() {
        GymMemberEntity entity = _memberDaoImpl.findMemberByEmail("kyang3@lakeheadu.ca");
        System.out.println("entity: " + entity.toString());

        assertTrue(entity.getEmail().equals("kyang3@lakeheadu.ca"));
    }
}
































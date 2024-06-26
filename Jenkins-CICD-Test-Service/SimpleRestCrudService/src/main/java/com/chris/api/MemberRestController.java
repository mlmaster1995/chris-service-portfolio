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

package com.chris.controller;

import com.chris.dto.GymMemberDto;
import com.chris.service.MemberCrudService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.chris.util.AppBeanConstant.APP_CONTROLLER_BEAN;
import static com.chris.util.AppBeanConstant.MEMBER_SERVICE_BEAN;

/**
 * rest controller for member crud operation, and return to client rest calls
 */
@RestController(value = APP_CONTROLLER_BEAN)
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class MemberRestController extends BaseController<ResponseEntity> {
    private Logger _LOG = LoggerFactory.getLogger(MemberRestController.class);

    private final MemberCrudService _memberCrudService;

    private final ObjectMapper _mapper;

    @Autowired
    public MemberRestController(
            @Qualifier(value = MEMBER_SERVICE_BEAN) MemberCrudService appService, ObjectMapper mapper) {
        super();

        this._memberCrudService = appService;
        this._mapper = mapper;
    }

    @GetMapping("/members")
    public ResponseEntity<List<GymMemberDto>> findAllMembers() {
        List<GymMemberDto> list = new ArrayList<>();

        try {
            list = _memberCrudService.findAllMembers();
        } catch (Exception exp) {
            _LOG.error("fails to find all members at the controller layer...: " + exp);
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<GymMemberDto> findMemberById(@PathVariable Integer memberId) {
        GymMemberDto dto = null;

        try {
            dto = _memberCrudService.findMemberById(memberId);
        } catch (Exception exp) {
            _LOG.error("fails to find the member by id({}) at the controller layer...", memberId);
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/member/email")
    public ResponseEntity<GymMemberDto> findMemberByEmail(@RequestBody String emailJson) {
        GymMemberDto dto = null;

        try {
            JsonNode node = _mapper.readTree(emailJson);
            String email = node.get("email").asText();

            dto = _memberCrudService.findMemberByEmail(email);
        } catch (Exception exp) {
            _LOG.error("fails to find the member by email({}) at the controller layer...", emailJson);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/member")
    public ResponseEntity<String> saveMember(@RequestBody GymMemberDto member) {
        try {
            _memberCrudService.saveMember(member);
        } catch (Exception exp) {
            _LOG.error("fails to persist member({}) at the controller layer...", member.toString());
            return new ResponseEntity<>(exp.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(String.format("member(%s) is persisted...", member.toString()), HttpStatus.OK);
    }

    /**
     * body must have id, or it performs as saving
     *
     * @param member
     * @return
     */
    @PutMapping("/member")
    public ResponseEntity<String> updateMember(@RequestBody GymMemberDto member) {
        try {
            _memberCrudService.updateMember(member);
        } catch (Exception exp) {
            _LOG.error("fails to update member({}) at the controller layer...", member.toString());
            return new ResponseEntity<>(exp.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(String.format("member(%s) is updated...", member.toString()), HttpStatus.OK);
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<String> deleteMemberById(@PathVariable Integer memberId) {
        try {
            _memberCrudService.deleteMemberById(memberId);
        } catch (Exception exp) {
            _LOG.error("fails to delete member with id ({}) at the controller layer...", memberId);
            return new ResponseEntity<>(exp.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(String.format("member with id (%s) is deleted...", memberId), HttpStatus.OK);
    }
}

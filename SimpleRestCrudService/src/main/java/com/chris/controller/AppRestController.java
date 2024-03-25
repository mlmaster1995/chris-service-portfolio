package com.chris.controller;

import com.chris.dto.GymMemberDto;
import com.chris.service.MemberCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api")
public class AppRestController extends BaseController<ResponseEntity> {
    private Logger _LOG = LoggerFactory.getLogger(AppRestController.class);

    private final MemberCrudService _memberCrudService;

    @Autowired
    public AppRestController(
            @Qualifier(value = MEMBER_SERVICE_BEAN) MemberCrudService appService) {
        super();
        this._memberCrudService = appService;
    }

    @GetMapping("/members")
    public ResponseEntity<List<GymMemberDto>> findAllMembers() {
        List<GymMemberDto> list = new ArrayList<>();
        try {
            list = _memberCrudService.findAllMembers();
        } catch (Exception exp) {
            _LOG.error("fails to find all members at controller layer...: " + exp);
            return ResponseEntity.badRequest().body(list);
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<GymMemberDto> findMemberById(@PathVariable Integer memberId) {
        return null;
    }

    @PostMapping("/member/email")
    public ResponseEntity<String> findMemberByEmail(@RequestBody String email) {
        return null;
    }

    @PostMapping("/member")
    public ResponseEntity<String> saveMember(@RequestBody GymMemberDto member) {
        return null;
    }

    /**
     * body must have id, or it performs as save
     *
     * @param member
     * @return
     */
    @PutMapping("/member")
    public ResponseEntity<GymMemberDto> updateMember(@RequestBody GymMemberDto member) {
        return null;
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<String> deleteMemberById(@PathVariable Integer employeeId) {
        return null;
    }
}

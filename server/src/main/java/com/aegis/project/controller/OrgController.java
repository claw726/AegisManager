package com.aegis.project.controller;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.UserModel;
import com.aegis.project.service.OrgService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/orgs")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @GetMapping("/{orgID}/members")
    public ResponseEntity<Set<UserDTO>> getOrgMembers(@PathVariable int orgID) {
        try {
            return ResponseEntity.ok(orgService.getOrgMembers(orgID));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Org not found with ID: " + orgID)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }
}

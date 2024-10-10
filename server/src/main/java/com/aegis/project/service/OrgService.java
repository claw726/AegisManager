package com.aegis.project.service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.aegis.project.dto.UserDTO;
import com.aegis.project.model.OrgModel;
import com.aegis.project.model.UserModel;
import com.aegis.project.repository.OrgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;

@Service
public class OrgService {

    @Autowired
    private OrgRepository orgRepository;

    public Set<UserDTO> getOrgMembers(int orgID) {
        OrgModel org = orgRepository.findById(orgID)
                .orElseThrow(() -> new RuntimeException("Org not found with ID: " + orgID));

        Set<UserModel> members = org.getUsers();
        return members.stream()
                .map(user -> new UserDTO(user.getUserID(), user.getUserName(), user.getEmail(), user.getProfilePicture()))
                .collect(Collectors.toSet());
    }

    public boolean createOrg(String name, String description, int ownerID) {
        OrgModel org = new OrgModel();
        org.setOrgName(name);
        org.setOrgDescription(description);
        org.setOrgOwnerID(ownerID);
        orgRepository.save(org);
        return true;
    }
}

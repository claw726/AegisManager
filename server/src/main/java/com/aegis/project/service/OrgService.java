package com.aegis.project.service;

import java.util.Date;

import com.aegis.project.model.OrgModel;
import com.aegis.project.repository.OrgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgService {

    @Autowired
    private OrgRepository orgRepository;

    public boolean createOrg(String name, String description, int ownerID) {
        OrgModel org = new OrgModel();
        org.setOrgName(name);
        org.setOrgDescription(description);
        org.setOrgOwnerID(ownerID);
        orgRepository.save(org);
        return true;
    }
}

package za.co.entelect.services.security.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.entelect.domain.entities.project.Project;
import za.co.entelect.domain.entities.user.AppUser;
import za.co.entelect.services.security.ProjectSecurityService;

@Service("projectSecurity")
public class ProjectSecurityServiceImpl implements ProjectSecurityService {

    @Override
    @Transactional
    public boolean isOwner(Project project, AppUser appUser) { // TODO: Replace stub
        return false;
    }

}

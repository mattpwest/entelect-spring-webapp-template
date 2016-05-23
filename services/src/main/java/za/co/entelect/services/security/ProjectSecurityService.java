package za.co.entelect.services.security;

import za.co.entelect.domain.entities.project.Project;
import za.co.entelect.domain.entities.user.AppUser;

/**
 * This service is intended to be used from Spring EL expressions in pages or security annotations, to check if the user
 * has the specified role for a given project.
 *
 * For example:
 *      &lt;p th:if="${@projectSecurity.isOwner(project, currentUser)}&gt;User is owner.&lt;/p&gt;
 *
 * Or on a method:
 *      @PreAuthorize("@projectSecurity.isOwner(#project, #currentUser)")
 *      public void someMethod(Project project, AppUser currentUser) {}
 */
public interface ProjectSecurityService {

    /**
     * Checks if the user is the owner of the specified project.
     * @param project project to check for
     * @param appUser user to check for
     * @return true if user is owner, else false
     */
    boolean isOwner(Project project, AppUser appUser);
}

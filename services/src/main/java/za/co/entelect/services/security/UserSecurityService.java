package za.co.entelect.services.security;

import za.co.entelect.domain.entities.user.AppUser;

/**
 * This service is intended to be used from Spring EL expression in pages or security annotations, to check if the user
 * has sufficient access to edit profile pages or make api calls.
 *
 * For example:
 *      &lt;p th:if="${@userSecuirty.isUserLoggedInUser(user)}&gt; This is the logged in user's page.&lt;/p&gt;
 *
 * Or on a method:
 *      @PreAuthorize("@userSecuirty.isUserLoggedInUser(user)")
 *      public void someMethod(Team team, CustomUser customUser) {}
 */

public interface UserSecurityService {

    /**
     * Checks to see if the user trying to edit or access the page the logged in user
     * @param user user's page that needs to be edited
     * @return true if user is logged in user, else false
     */
    boolean isUserLoggedInUser(AppUser user);
}

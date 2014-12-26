package seedproject.services;

//inspired by https://github.com/spring-projects/spring-security/blob/master/core/src/main/java/org/springframework/security/provisioning/JdbcUserDetailsManager.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import seedproject.domain.auth.User;
import seedproject.domain.repositories.auth.UserRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Service("userDetailsManager")
public class UserDetailsManagerImpl implements UserDetailsManager {
    protected final Log logger = LogFactory.getLog(getClass());

    private AuthenticationManager authenticationManager;
    private UserCache userCache = new NullUserCache();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Assembler assembler;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {

        UserDetails userDetails = null;
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("user not found");

        return  assembler.buildUserFromUserEntity(user);
    }

    /**
     * Create a new user with the supplied details.
     */
    public void createUser(UserDetails userDetails){
        User user = assembler.buildUserEntityFromUserDetails(userDetails);
        userRepository.saveAndFlush(user);
    }

    /**
     * Update the specified user.
     */
    public void updateUser(UserDetails userDetails){
        User user = assembler.buildUserEntityFromUserDetails(userDetails);
        userRepository.saveAndFlush(user);
    }

    /**
     * Remove the user with the given login name from the system.
     */
    public void deleteUser(String username){
        UserDetails userDetails = loadUserByUsername(username);
        User user = assembler.buildUserEntityFromUserDetails(userDetails);
        userRepository.delete(user);
    }

    /**
     * Modify the current user's password. This should change the user's password in
     * the persistent user repository (datbase, LDAP etc).
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException("Can't change password as no Authentication object found in context " +
                    "for current user.");
        }

        String username = currentUser.getName();

        // If an authentication manager has been set, re-authenticate the user with the supplied password.
        if (authenticationManager != null) {
            logger.debug("Reauthenticating user '"+ username + "' for password change request.");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        } else {
            logger.debug("No authentication manager set. Password won't be re-checked.");
        }

        logger.debug("Changing password for user '"+ username + "'");

        UserDetails userDetails = loadUserByUsername(username);
        User user = assembler.buildUserEntityFromUserDetails(userDetails);

        user.setPassword(newPassword);

        userRepository.saveAndFlush(user);

        SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(currentUser, newPassword));

        userCache.removeUserFromCache(username);

    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken newAuthentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }

    /**
     * Check if a user with the supplied login name exists in the system.
     */
    public boolean userExists(String username){

        UserDetails userDetails = null;
        try {
            userDetails = loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            logger.info("Username not found:" + username);
            e.printStackTrace();
            return false;
        } catch (DataAccessException e) {
            logger.info("Data Access Exception:" + username);
            e.printStackTrace();
            return false;
        } finally {

        }

        if(userDetails!=null){
            return true;
        } else {
            return false;
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Optionally sets the UserCache if one is in use in the application.
     * This allows the user to be removed from the cache after updates have taken place to avoid stale data.
     *
     * @param userCache the cache used by the AuthenticationManager.
     */
    public void setUserCache(UserCache userCache) {
        Assert.notNull(userCache, "userCache cannot be null");
        this.userCache = userCache;
    }
}

package org.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

@Slf4j
@SpringBootApplication
@ComponentScan("org.ldap")
public class LdapTest {

    public static void main(String[] cla) {
        // LDAP server connection parameters
        String ldapURL = cla[0];
        String ldapUser = cla[1];
        String ldapPassword = cla[2];
        String searchBase = cla[3]; // Adjust to your LDAP structure

        log.info("ldapURL: {}, ldapUser: {}, ldapPassword: {}, searchBase: {}",ldapURL,ldapUser,ldapPassword,searchBase);

        // Set up environment properties
        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=" + ldapUser + "," + searchBase);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put("com.sun.jndi.ldap.connect.timeout", "5000"); // Adjust timeout as needed

        log.info("env : {}", env);
        log.info("principal val : {}",env.get(Context.SECURITY_PRINCIPAL));

        try {
            // Create the initial context
            DirContext context = new InitialDirContext(env);

            // Specify search constraints
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // Perform the LDAP search
            NamingEnumeration<SearchResult> results = context.search(searchBase, "(objectClass=person)", controls);

            // Iterate through the search results
            while (results.hasMore()) {
                SearchResult result = results.next();
                // Retrieve and print attributes
                log.info("DN: {}" , result.getNameInNamespace());
                log.info("CN: {}" , result.getAttributes().get("cn").get());
                log.info("Email: {}" , result.getAttributes().get("mail").get());
                log.info("=====================================");
            }

            // Close the context
            context.close();

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }
}
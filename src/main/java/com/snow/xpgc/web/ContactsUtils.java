package com.snow.xpgc.web;


import com.britesnow.snow.web.binding.ApplicationProperties;
import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.contacts.ContactQuery;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.*;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Singleton
public class ContactsUtils {
    private String baseContactsUrl = "https://www.google.com/m8/feeds/contacts/default/full";

    private final ContactsService contactsService;

    @Inject
    public ContactsUtils(@ApplicationProperties Map appCfg) throws OAuthException {
        contactsService = new ContactsService("sample contacts");
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey((String)appCfg.get("google.client_id"));
        oauthParameters.setOAuthConsumerSecret((String) appCfg.get("google.secret"));
        oauthParameters.setScope((String)appCfg.get("google.scope"));
        oauthParameters.setOAuthType(OAuthParameters.OAuthType.TWO_LEGGED_OAUTH);

        // oauthParameters.setOAuthToken(token);
        OAuthSigner signer = new OAuthHmacSha1Signer();
        contactsService.setOAuthCredentials(oauthParameters, signer);
    }

    public ContactsUtils setToken(String token) throws Exception {
        contactsService.setHeader("Authorization", "Bearer " + token);
        contactsService.setUserToken(token);
        return this;
    }


    public ContactGroupEntry createContactGroupEntry(String name) throws Exception

    {
        ContactGroupEntry group = new ContactGroupEntry();
        group.setTitle(new PlainTextConstruct(name));
        URL postUrl = new URL("https://www.google.com/m8/feeds/groups/default/full");
        return contactsService.insert(postUrl, group);
    }


    public List<ContactEntry> getAGroupContactResults(String groupId) throws ServiceException, IOException {
        URL feedUrl = new URL(baseContactsUrl);
        ContactQuery myQuery = new ContactQuery(feedUrl);
        myQuery.setStringCustomParameter("group", groupId);
        ContactFeed resultFeed = contactsService.query(myQuery, ContactFeed.class);
        return resultFeed.getEntries();
    }

    public List<ContactEntry>  getContactResults() throws IOException, ServiceException {
        URL feedUrl = new URL(baseContactsUrl);
        ContactFeed resultFeed = contactsService.getFeed(feedUrl, ContactFeed.class);
        return resultFeed.getEntries();
    }


    /**
     * Create A contact into a group by groupId useing the following params
     *
     * @param fullName
     * @param givenName
     * @param familyName
     * @param phone
     * @param bir
     * @param groupId
     * @param email
     * @param notes
     * @return ContactEntry
     * @throws ServiceException
     * @throws IOException
     * @author shining
     */

    public ContactEntry createAContact(

            String fullName, String givenName, String familyName, String phone, String bir,

            String groupId, String email, String notes)

            throws ServiceException, IOException {


        ContactEntry contact = new ContactEntry();
        Name name = new Name();
        name.setFullName(new FullName(fullName, null));
        name.setGivenName(new GivenName(givenName, null));
        name.setFamilyName(new FamilyName(familyName,""));

        //set other Inf, Chinese Called Beizhu
        contact.setContent(new PlainTextConstruct(notes));
        //set email
        Email primaryMail = new Email();
        primaryMail.setAddress(email);
        primaryMail.setRel("http://schemas.google.com/g/2005#home");
        primaryMail.setPrimary(true);
        contact.addEmailAddress(primaryMail);


        PhoneNumber pn = new PhoneNumber();
        pn.setPhoneNumber(phone);
        pn.setPrimary(true);
        pn.setRel("http://schemas.google.com/g/2005#work");
        contact.addPhoneNumber(pn);
        //Add to a Group

        GroupMembershipInfo gm = new GroupMembershipInfo();
        gm.setHref(groupId);
        contact.addGroupMembershipInfo(gm);
        Birthday b = new Birthday(bir);

        contact.setBirthday(b);
        //Add process
        URL postUrl = new URL(baseContactsUrl);
        return contactsService.insert(postUrl, contact);

    }


}

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Singleton
public class ContactsUtils {
    private final String BASE_CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/default/full";

    private final ContactsService contactsService;

    private final String BASE_GROUP_URL = "https://www.google.com/m8/feeds/groups/default/full";

    @Inject
    public ContactsUtils(@ApplicationProperties Map appCfg) throws OAuthException {
        contactsService = new ContactsService("sample contacts");
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey((String) appCfg.get("google.client_id"));
        oauthParameters.setOAuthConsumerSecret((String) appCfg.get("google.secret"));
        oauthParameters.setScope((String) appCfg.get("google.scope"));
        oauthParameters.setOAuthType(OAuthParameters.OAuthType.TWO_LEGGED_OAUTH);

        // oauthParameters.setOAuthToken(token);
        OAuthSigner signer = new OAuthHmacSha1Signer();
        contactsService.setOAuthCredentials(oauthParameters, signer);
    }

    public ContactsUtils setToken(String token) {
        contactsService.setHeader("Authorization", "Bearer " + token);
        //contactsService.setUserToken(token);
        return this;
    }


    public ContactGroupEntry createContactGroupEntry(String name) throws Exception {
        ContactGroupEntry group = new ContactGroupEntry();
        group.setTitle(new PlainTextConstruct(name));
        URL postUrl = new URL(BASE_GROUP_URL);
        return contactsService.insert(postUrl, group);
    }

    public ContactGroupEntry getContactGroupEntry(String groupId) throws IOException, ServiceException {
        URL url = new URL(BASE_GROUP_URL + "/" + groupId);
        return contactsService.getEntry(url, ContactGroupEntry.class);
    }
    public ContactEntry getContactEntry(String contactId) throws IOException, ServiceException {
        URL url = new URL(BASE_CONTACTS_URL + "/" + contactId);
        return contactsService.getEntry(url, ContactEntry.class);
    }


    public List<ContactEntry> getGroupContactResults(String groupId) throws ServiceException, IOException {
        URL feedUrl = new URL(BASE_GROUP_URL);
        ContactQuery myQuery = new ContactQuery(feedUrl);
//        myQuery.setStringCustomParameter("group", groupId);
        myQuery.setGroup(groupId);
        ContactFeed resultFeed = contactsService.query(myQuery, ContactFeed.class);
        return resultFeed.getEntries();
    }

    public List<ContactEntry> getContactResults() throws IOException, ServiceException {
        URL feedUrl = new URL(BASE_CONTACTS_URL);
        ContactFeed resultFeed = contactsService.getFeed(feedUrl, ContactFeed.class);
        return resultFeed.getEntries();
    }

    public List<ContactGroupEntry> getGroupResults() throws IOException, ServiceException {
        URL feedurUrl = new URL(BASE_GROUP_URL);
        ContactGroupFeed contactGroupFeed = contactsService.getFeed(feedurUrl, ContactGroupFeed.class);
        return contactGroupFeed.getEntries();
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

    public ContactEntry createContact(
            String fullName, String givenName, String familyName, String phone, String bir,
            String groupId, String email, String notes)
            throws ServiceException, IOException {
        ContactEntry contact = new ContactEntry();
        Name name = new Name();
        name.setFullName(new FullName(fullName, null));
        name.setGivenName(new GivenName(givenName, null));
        name.setFamilyName(new FamilyName(familyName, ""));

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
        URL postUrl = new URL(BASE_CONTACTS_URL);
        return contactsService.insert(postUrl, contact);

    }

    public ContactEntry createContact(ContactInfo contact)
            throws ServiceException, IOException {
        ContactEntry contactEntry = new ContactEntry();
        Name name = new Name();
        if (contact.getFullName() != null) {
            name.setFullName(new FullName(contact.getFullName(), null));
        }
        if (contact.getGivenName() != null) {
            name.setGivenName(new GivenName(contact.getGivenName(), null));
        }
        if (contact.getFamilyName() != null) {
            name.setFamilyName(new FamilyName(contact.getFamilyName(), ""));
        }
        if (contact.getNotes() != null) {
            contactEntry.setContent(new PlainTextConstruct(contact.getNotes()));
        }
        //set email
        if (contact.getEmail() != null) {
            Email primaryMail = new Email();
            primaryMail.setAddress(contact.getEmail());
            primaryMail.setRel("http://schemas.google.com/g/2005#home");
            primaryMail.setPrimary(true);
            contactEntry.addEmailAddress(primaryMail);
        }

        if (contact.getPhone() != null) {
            PhoneNumber pn = new PhoneNumber();
            pn.setPhoneNumber(contact.getPhone());
            pn.setPrimary(true);
            pn.setRel("http://schemas.google.com/g/2005#work");
            contactEntry.addPhoneNumber(pn);
        }
        //Add to a Group
        if (contact.getGroupId() != null) {
            GroupMembershipInfo gm = new GroupMembershipInfo();
            gm.setHref(contact.getGroupId());
            contactEntry.addGroupMembershipInfo(gm);
        }
        if (contact.getBir() != null) {
            Birthday b = new Birthday(contact.getBir());
            contactEntry.setBirthday(b);
        }


        //Add process
        URL postUrl = new URL(BASE_CONTACTS_URL);
        return contactsService.insert(postUrl, contactEntry);

    }


}

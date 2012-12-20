package com.snow.xpgc.web;


import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.util.ServiceException;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleWebHandlers {
    private static Logger log = LoggerFactory.getLogger(GoogleWebHandlers.class);
    @Inject
    private ContactsUtils contactsUtils;

    @WebModelHandler(startsWith = "/googleContacts")
    public void getContacts(@WebModel Map m, @WebParam("groupId") String groupId, @WebUser String token, RequestContext rc) throws Exception {
        List<ContactEntry> list;
        if (token == null) {
            return;
        } else {
            contactsUtils.setToken(token);
        }
        if (groupId == null) {
            list = contactsUtils.getContactResults();
        } else {
            list = contactsUtils.getGroupContactResults(groupId);
        }
        List<ContactInfo> infos = new ArrayList<ContactInfo>();
        for (ContactEntry contact : list) {
            infos.add(ContactInfo.from(contact));
        }

        m.put("result", infos);

    }

    @WebModelHandler(startsWith = "/googleGroups")
    public void getGroups(@WebModel Map m, @WebUser String token, RequestContext rc) throws Exception {
        List<ContactGroupEntry> list;
        if (token == null) {
            return;
        } else {
            contactsUtils.setToken(token);
        }

        list = contactsUtils.getGroupResults();


        m.put("result", list);

    }

    @WebModelHandler(startsWith = "/")
    public void pageIndex(@WebModel Map m, @WebUser String token, RequestContext rc) {
        // gameTestManager.init();
        m.put("token", token);
    }

    @WebActionHandler(name = "createContact")
    public Map createContact(@WebUser String token, @WebObject ContactInfo contact) {
        Map map = new HashMap();
        boolean result = true;
        if (token == null) {
            result = false;
        } else {
            contactsUtils.setToken(token);
            try {
                if (contact.getId() == null) {
                    contactsUtils.createContact(contact);
                }else{
                    contactsUtils.updateContactEntry(contact);
                }

            } catch (Exception e) {
                log.warn("create contact fail", e);
                result = false;
            }
        }

        map.put("result", result);
        return map;
    }
    @WebActionHandler(name = "createGroup")
    public Map createGroup(@WebUser String token,@WebParam("groupId") String groupId,
                           @WebParam("groupName") String groupName, @WebParam("etag") String etag) {
        Map map = new HashMap();
        boolean result = true;
        if (token == null) {
            result = false;
        } else {
            contactsUtils.setToken(token);
            try {
                if (groupId == null) {
                    //create group
                    contactsUtils.createContactGroupEntry(groupName);
                }else {
                    //update group
                    contactsUtils.updateContactGroupEntry(groupId, etag,groupName);
                }

            } catch (Exception e) {
                log.warn(String.format("create Group %s fail", groupName), e);
                result = false;
            }
        }

        map.put("result", result);
        return map;
    }

    @WebActionHandler(name="deleteGroup")
    public Map deleteGroup(@WebUser String token, @WebParam("groupId") String groupId, @WebParam("etag") String etag)  {
        boolean result = false;
        if (token != null) {
            try {
                contactsUtils.setToken(token);
                contactsUtils.deleteGroup(groupId, etag);
                result = true;
            } catch (Exception e) {
                log.warn(String.format("delete group %s fail", groupId), e);
            }
        }
        Map map = new HashMap();
        map.put("result", result);
        return map;
    }
    @WebActionHandler(name="deleteContact")
    public Map deleteContact(@WebUser String token, @WebParam("contactId") String contactId, @WebParam("etag") String etag)  {
        boolean result = false;
        if (token != null) {
            try {
                contactsUtils.setToken(token);
                contactsUtils.deleteContact(contactId, etag);
                result = true;
            } catch (Exception e) {
                log.warn(String.format("delete contact %s fail", contactId), e);
            }
        }
        Map map = new HashMap();
        map.put("result", result);
        return map;
    }

    @WebModelHandler(startsWith = "/getContact")
    public void getContact(@WebUser String token, @WebParam("contactId") String contactId,
                           @WebParam("etag") String etag, @WebModel Map m) {
        if (token != null && contactId !=null) {
            try {
                contactsUtils.setToken(token);
                ContactEntry entry = contactsUtils.getContactEntry(contactId);
                m.put("result", ContactInfo.from(entry));
            } catch (Exception e) {
                log.warn(String.format("get contact %s fail", contactId), e);
                m.put("result", false);
            }
        }
    }
}

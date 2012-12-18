package com.snow.xpgc.web;


import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebActionHandler;
import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @WebActionHandler(name = "createGroup")
    public Map createGroup(@WebUser String token, @WebParam("groupName") String groupName) {
        Map map = new HashMap();
        boolean result = true;
        if (token == null) {
            result = false;
        } else {
            contactsUtils.setToken(token);
            try {
                contactsUtils.createContactGroupEntry(groupName);
            } catch (Exception e) {
                log.warn(String.format("create Group %s fail", groupName), e);
                result = false;
            }
        }

        map.put("result", result);
        return map;
    }
}

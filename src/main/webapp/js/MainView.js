(function ($) {

    brite.registerView("MainView", {loadTmpl:true}, {
        create:function (data, config) {
            return $("#tmpl-MainView").render();
        },

        postDisplay:function (data, config) {
             var view = this;
             var $e = view.$el;
             console.log($e.find("li.active"));
             $e.find("li.active a").trigger("btap");
        },

        events:{
            "btap; .showContacts":function (event) {
                $(event.currentTarget).closest("ul").find("li").removeClass("active");
                $(event.currentTarget).closest("li").addClass("active");
                var contacts = app.getContacts();
                brite.display("DataTable", ".MainView-content", {
                    gridData:contacts,
                    columnDef:[
                        {
                            text:"#",
                            render: function(obj, idx){return idx + 1},
                            attrs:"style='width: 10%'"
                        },
                        {
                            text:"Emails",
                            render:function(obj){return obj.email},
                            attrs:"style='width: 20%'"

                        },
                        {
                            text:"Full Name",
                            render:function(obj){return obj.fullName},
                            attrs:"style='width: 25%'"
                        },
                        {
                            text:"Group",
                            render:function(obj){return getGroupId(obj.groupId)}
                        }
                    ],
                    opts:{
                        htmlIfEmpty: "Not contacts found",
                        withPaging: false,
                        cmdEdit: "EDIT_CONTACT",
                        cmdDelete: "DELETE_CONTACT"
                    }
                });
            },
            "btap; .showGroups":function (event) {
                var view = this;
                $(event.currentTarget).closest("ul").find("li").removeClass("active");
                $(event.currentTarget).closest("li").addClass("active");
                var groups = app.getGroups();
                brite.display("DataTable", ".MainView-content", {
                    rowAttrs: function(obj){return " data-groupId='" + obj.id + "'"},
                    gridData: groups,
                    columnDef:[
                        {
                            text:"#",
                            render: function(obj, idx){return idx + 1},
                            attrs:"style='width: 20%'"
                        },
                        {
                            text:"Title",
                            render:function(obj){return obj.title.text}

                        }
                    ],
                    opts:{
                        htmlIfEmpty: "Not Groups found",
                        withPaging: false,
                        cmdEdit: "EDIT_GROUP",
                        cmdDelete: "DELETE_GROUP"
                    }
                });
            },
            "btap; .createGroup": function() {
                brite.display("CreateGroup");
            },
            "btap; .createContact": function() {
                brite.display("CreateContact");
            }
        },

        docEvents:{
            "SHOW_GROUPS": function() {
                var view = this;
                var $e = view.$el;
                $e.find(".showGroups").trigger("btap");
            },
            "SHOW_CONTACTS": function(){
                var view = this;
                view.$el.find(".showContacts").trigger("btap");
            },
            "EDIT_GROUP":function(event, extraData){
                console.log("edit group");
            },
            "DELETE_GROUP": function(event, extraData){
                console.log("DELETE GROUP");
            },
            "DELETE_CONTACT": function(event, extraData) {
                console.log("DELETE CONTACT");
            },
            "EDIT_CONTACT": function(event, extraData){
                console.log("EDIT CONTACT");
            }
        },

        daoEvents:{
        }
    });

    function getGroupId(url) {
        var myregexp = /http:\/\/www.google.com\/m8\/feeds\/groups\/(.+)\/base\/(.+)/;
        var match = myregexp.exec(url);
        if (match != null) {
            result = match[2];
        } else {
            result = "";
        }
        return result;
    }
})(jQuery);
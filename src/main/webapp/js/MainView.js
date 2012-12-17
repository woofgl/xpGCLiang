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
            "btap; .showEmails":function (event) {
                $(event.currentTarget).closest("ul").find("li").removeClass("active");
                $(event.currentTarget).closest("li").addClass("active");
                var emails = app.getEmails();
                brite.display("DataTable", ".MainView-content", {
                    gridData:emails,
                    columnDef:[
                        {
                            text:"#",
                            render: function(obj, idx){return idx + 1},
                            attrs:"style='width: 20%'"
                        },
                        {
                            text:"Emails",
                            render:function(obj){return obj}

                        }
                    ],
                    opts:{
                        htmlIfEmpty: "GitHub not support get emails from api, don't know why"
                    }
                });
            },
            "btap; .showRepositories":function (event) {
                var view = this;
                $(event.currentTarget).closest("ul").find("li").removeClass("active");
                $(event.currentTarget).closest("li").addClass("active");
                showRepositories.call(this);

            },
            "btap; .MainView-content td.repoName": function(event) {
                 var view = this;
                 var $e = view.$el;
                var repoName = $(event.currentTarget).closest("tr").attr("data-repoName");
                var owner = $(event.currentTarget).closest("tr").attr("data-owner");
                $e.find(".MainView-content").empty();
                brite.display("RepositoryView", ".MainView-content", {repoName: repoName, owner: owner});
            },
            "btap; .showOrganizations":function (event) {
                $(event.currentTarget).closest("ul").find("li").removeClass("active");
                $(event.currentTarget).closest("li").addClass("active");

                var repos = app.getOrganizations();
                brite.display("DataTable", ".MainView-content", {
                    gridData:repos,
                    rowAttrs: function(obj, idx){
                        if(obj!==true) {
                            return " data-orgName='" + obj.login  + "'";
                        }else{
                            return "";
                        }
                    },
                    columnDef:[
                        {
                            text:"#",
                            render: function(obj, idx){return idx + 1},
                            attrs:"style='width: 10%'"
                        },
                        {
                            text:"Name",
                            propName:"login",
                            attrs:"class='orgName' style='width: 20%;cursor:pointer'"
                        },
                        {
                            text:"Location",
                            propName:"location",
                            attrs:"style='width: 20%'"
                        },
                        {
                            text:"Url",
                            propName:"url"
                        }
                    ],
                    opts:{
                        htmlIfEmpty: "Not Organizations found",
                        withCmdDelete: false,
                        withPaging: false
                    }
                });
            },
            "btap; .MainView-content td.orgName": function(event) {
                var view = this;
                var $e = view.$el;
                var orgName = $(event.currentTarget).closest("tr").attr("data-orgName");
                $e.find(".MainView-content").empty();
                showRepositories({
                   dataProvider: {list: function(opts){
                       return app.getJsonData(contextPath + "/getOrgRepositories.json", $.extend({
                           method: "get",
                           orgName: orgName
                       }, opts));
                   }}
                });
            }
        },

        docEvents:{
        },

        daoEvents:{
        }
    });

    function showRepositories(data) {
        var tableDefine = {
            dataProvider: {list: app.getRepositories},
            rowAttrs: function(obj, idx){
                if(obj!==true) {
                    return " data-repoName='" + obj.name + "' data-owner='" + obj.owner.login + "'";
                }else{
                    return "";
                }
            },
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){return idx + 1},
                    attrs:"style='width: 10%'"
                },
                {
                    text:"Name",
                    propName:"name",
                    attrs:"class='repoName' style='width: 20%;cursor:pointer'"
                },,
                {
                    text:"Owner",
                    propName:"owner.login",
                    attrs:" style='width: 20%;'"
                },
                {
                    text:"Url",
                    propName:"url",
                    attrs:"style='width: 30%'"
                },
                {
                    text:"Desc",
                    propName:"description"
                }
            ],
            opts:{
                htmlIfEmpty: "Not repository found",
                withCmdDelete: false
            }
        };
        brite.display("DataTable", ".MainView-content", $.extend(tableDefine, data||{}));
    }
})(jQuery);
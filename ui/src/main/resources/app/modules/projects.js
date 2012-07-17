define([
  "genesis",
  "modules/status",
  "use!backbone",
  "jquery",
  "use!showLoading",
  "use!jvalidate"
],

function(genesis, status, Backbone, $) {
  var DEFAULT_TIMEOUT = 4000;

  var Projects = genesis.module();

  Projects.Model = Backbone.Model.extend({
    parse: function(json){
      if (json.result) {
        return json.result
      } else {
        return json
      }
    },

    url: function() {
      if (!this.id) {
        return "/rest/projects";
      } else {
        return "/rest/projects/" + this.id;
      }
    }
  });

  Projects.Collection = Backbone.Collection.extend({
    model: Projects.Model,
    url: "/rest/projects"
  });

  Projects.Views.ProjectsOverview = Backbone.View.extend({
    template: "app/templates/projects_list.html",

    events: {
      "click .delete-link": "onDeleteProject"
    },

    initialize: function(options) {
      this.collection = options.collection;

      var self = this;
      this.collection.fetch().done(function() {
        self.render();
      });
    },

    render: function(done) {
      var self = this;
      $.when(genesis.fetchTemplate(this.template)).done(function(tmpl) {
        self.$el.html(
          tmpl({
            "projects" : self.collection.toJSON(),
            "currentUser": genesis.app.currentUser
          })
        );
        if (_.isFunction(done)) {
          done(self.el);
        }
      });
    },

    onDeleteProject: function(event) {
      var self = this;
      var projectId = event.currentTarget.getAttribute("data-index");
      $.ajax({
        url: "/rest/projects/" + projectId,
        type: "DELETE",
        timeout: DEFAULT_TIMEOUT,
        success: function (data, textStatus, jqXHR) {
          self.collection.fetch().done(function () {
            self.render();
          });
        },
        error: function (jqXHR, textStatus, errorThrown) {
          status.StatusPanel.error("Error: " + errorThrown);
          self.render();
        }
      });
    }
  });

  Projects.Views.CreateProject = Backbone.View.extend({
    template: "app/templates/create_project.html",

    events: {
      "click a.save" : "onSave"
    },

    initialize: function(options) {
      this.project = options.project;
      this.project.fetch().done(_.bind(this.render, this));
    },

    onSave: function() {
      if(!this.$("#project-attributes").valid()) {
        return;
      }
      var toSave = this.project.clone();
      toSave.set({name: $("input[name='name']").val(),
                        description: $("textarea[name='description']").val(),
                        projectManager: $("input[name='projectManager']").val()});
      var isNew = toSave.isNew(),
          self = this;
      $.when(toSave.save())
        .done(function (savedProject) {
          self.project.fetch().done(function(){
              genesis.app.trigger("projects-changed");
              genesis.app.router.navigate(isNew ?  "/" : ("/project/" + savedProject.result.id), {trigger: true});
          });
        })
        .fail(function (jqXHR) {
          self.status.error(jqXHR)
        });
    },

    render: function(){
      var self = this;
      $.when(genesis.fetchTemplate(this.template)).done(function(tmpl) {
        self.$el.html( tmpl({"project" : self.project.toJSON()}));
        self.delegateEvents(self.events);
        self.status = new status.LocalStatus({el: self.$(".notification")})

      });
    }
  });

  return Projects;
});
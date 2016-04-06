package org.jenkinsci.plugins.badge.BadgeAction

def l = namespace(lib.LayoutTagLib)
def st = namespace("jelly:stapler")

l.layout {
    l.main_panel {
        h2(_("Embeddable Badges"))
        p(raw(_("blurb")))
        raw("""
<p>
</p>
<script>
    Behaviour.register({
        "INPUT.select-all" : function(e) {
            e.onclick = function () {
                e.focus();
                e.select();
            }
        }
    });
</script>
<style>
    INPUT.select-all {
        width:100%;
    }
    IMG#badge {
        margin-left:2em;
    }
</style>
""")

        def fullJobName = h.escape(my.project.fullName);
        def jobUrlWithoutView =  "${app.rootUrl}job/${fullJobName}";
        def badgeUrlWithoutView = jobUrlWithoutView + "/badge/icon"
        def publicBadge = "${app.rootUrl}buildStatus/icon?job=${fullJobName}";


        h3(_("Markdown"))
        input(type:"text",value:"[![Build Status](${publicBadge})](${jobUrlWithoutView})",class:"select-all")

        h3(_("Confluence"))
        input(type:"text",value:"[!${publicBadge}!|${jobUrlWithoutView}]",class:"select-all")
    }
}

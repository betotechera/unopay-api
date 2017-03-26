package br.com.unopay.api.notification;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.notification.model.Email;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.model.Notification;

import java.util.HashMap;

public class NotificationTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(Notification.class).addTemplate("valid", new Rule() {{
            add("email", one(Email.class, "valid"));
            add("content", "<h1> ola {{name}}</h1>");
            add("eventType", uniqueRandom(EventType.class));
            add("payload", new HashMap(){{  put("name", "jose"); }});
        }});

        Fixture.of(Email.class).addTemplate("valid", new Rule() {{
            add("to", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("subject", random("User create", "Create password"));
            add("from", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("personalFrom", "Unopay");
        }});

        Fixture.of(Email.class).addTemplate("invalid-email", new Rule() {{
            add("to", uniqueRandom("nerdgmail.com", "news@.gmail", "ze@asdf.ze"));
            add("subject", random("User create", "Create password"));
            add("from", uniqueRandom("nerd@gmail.com", "news@gmail.com"));
            add("personalFrom", "Unopay");
        }});
    }
}

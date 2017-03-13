package br.com.unopay.api.uaa;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.unopay.api.uaa.model.UserDetail;

public class UaaTemplateLoader implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(UserDetail.class).addTemplate("valid", new Rule(){{
            add("id", random(String.class, "5464", "8979897", "65469"));
            add("email", random("nerd@gmail.com", "luck@gmail.com"));
            add("password", regex("\\d{3,5}"));
        }});

    }
}

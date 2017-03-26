package br.com.unopay.api.notification.engine

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.notification.model.Notification
import br.com.unopay.api.uaa.model.UserDetail
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired

class TemplateProcessorTest extends SpockApplicationTests{


    @Autowired
    TemplateProcessor processor

    TemplateLoader  templateLoader = Mock(TemplateLoader)


    def content = """
                <html>
                    <h1>Olá {{user.name}}</h1>
                    <h1>email: {{user.email}}</h1>
                </html>
            """

    def setup(){
        templateLoader.getTemplate(_) >> content
        processor.templateLoader = templateLoader
    }


    def 'given blank template when processed then return same template'() {
        given:
            def templateHTMLAsString = '<html></html>'
        when:
            def processedHTML = processor.process(templateHTMLAsString, null)
        then:
            assert equalsHTML(templateHTMLAsString, processedHTML)
    }

    def 'given a simple html template when variable value is filled then return template with the variable value'() {
        given:
            def templateHTMLAsString =
                '''
                <html>
                    <body>
                        <p>{{home.welcome}}</p><br>
                        {{home.testValue}}
                        <img src="{{home.testValue}}">
                    </body>
                </html>
                '''
        when:
            def processedHTML = processor.process(templateHTMLAsString, ['home' : new Home(welcome: 'simple message', testValue: 'blablabla')])
        then:
            def expectedHTML =
                '''
                <html>
                    <body>
                        <p>simple message</p><br>
                        blablabla
                        <img src="blablabla">
                    </body>
                </html>
                '''

        assert equalsHTML(processedHTML, expectedHTML)
    }

    def 'given a template when there is iterable value then create html elements list'() {
        given:
            def templateHTMLAsString =
                '''
                <html>
                    <body>
                        <ul>
                            {%for textValue in home.tempValues %}
                                <li>{{textValue}}</li>
                            {% endfor %}
                        </ul>
                    </body>
                </html>
                '''
        when:
            def processedHTML = processor.process(templateHTMLAsString, ['home' : new Home(tempValues: ['first', 'second', 'third'])])
        then:
            def expectedHTML = '''
                <html>
                    <body>
                        <ul>
                            <li>first</li>
                            <li>second</li>
                            <li>third</li>
                        </ul>
                    </body>
                </html>
                '''
        assert equalsHTML(processedHTML, expectedHTML)
    }


    def "given a notification with headers without information when try get html should return error"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid")
        notification.eventType = null
        when:
        processor.renderHtml(notification)
        then:
        thrown IllegalArgumentException
    }


    def "given a valid notification with template when get html should return html with information"(){
        given:
        UserDetail user = Fixture.from(UserDetail.class).gimme("without-group")
        Notification notification = Fixture.from(Notification.class).gimme("valid")
        notification.with { payload = [ user: user]}
        when:
        def htmlResult = processor.renderHtml(notification)
        then:
        def expectedHtml = """
                <html>
                    <h1>Olá $user.name</h1>
                    <h1>email: $user.email</h1>
                </html>
            """
        equalsHTML(expectedHtml, htmlResult)
    }


    def "given a notification without payload when try get html should return error"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid")
        notification.with { payload = null }
        when:
        processor.renderHtml(notification)

        then:
        thrown IllegalArgumentException
    }

    def "given a template without cache when get html should call template loader"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid")
        when:
        processor.cache.remove(notification.eventType.toString())
        processor.renderHtml(notification)

        then:
        1 * templateLoader.getTemplate(_) >> content
    }

    def "given a template with cache when get html should not call resource"(){
        given:
        Notification notification = Fixture.from(Notification.class).gimme("valid")
        processor.cache.put(notification.eventType.toString(), content)
        when:
        processor.renderHtml(notification)

        then:
        0 * templateLoader.getTemplate(_) >> content
    }

    boolean equalsHTML(String html, String otherHtml) {
        new Jsoup().parse(html).html() == new Jsoup().parse(otherHtml).html()
    }

    static class Home {
        String welcome
        String testValue
        List tempValues
    }
}
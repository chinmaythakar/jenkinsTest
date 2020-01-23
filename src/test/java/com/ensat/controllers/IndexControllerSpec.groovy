import com.ensat.controllers.IndexController
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

@SpringBootTest
class IndexControllerSpec extends Specification {

    IndexController indexController = new IndexController()
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indexController).alwaysDo(MockMvcResultHandlers.print()).build()

    def "validate index returns homepage"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/"))

        then:
        result.andExpect(MockMvcResultMatchers.status().isOk())
        result.andExpect(MockMvcResultMatchers.forwardedUrl("index"))
    }
}
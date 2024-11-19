package dev.nateweisz.bytestore.project.maven

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for Maven related endpoints. This is entirely compliant with
 * the spec located <a href="https://maven.apache.org/repository/layout.html">here</a>.
 */
@RestController
@RequestMapping("/maven")
class MavenController {

    @GetMapping("archetype-catalog.xml")
    fun archetypeCatalog() = """
        <?xml version="1.0" encoding="UTF-8"?>
        <archetype-catalog xmlns="https://maven.apache.org/plugins/maven-archetype-plugin/archetype-catalog/1.0.0"
                           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="https://maven.apache.org/plugins/maven-archetype-plugin/archetype-catalog/1.0.0 http://maven.apache.org/xsd/archetype-catalog-1.0.0.xsd">
            <archetypes>
            // TODO: Serve here 
            </archetypes>
        </archetype-catalog>
    """.trimIndent()
}
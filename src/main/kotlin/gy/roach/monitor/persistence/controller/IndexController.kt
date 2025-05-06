package gy.roach.monitor.persistence.controller

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.ext.gfm.tables.TablesExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Controller
class IndexController @Autowired constructor(private val resourceLoader: ResourceLoader) {

    @GetMapping("/")
    @ResponseBody
    fun index(): String {
        // Try to load README.md from classpath first, then from file system
        val markdownContent = try {
            val resource = resourceLoader.getResource("classpath:README.md")
            
            if (resource.exists()) {
                resource.inputStream.use { 
                    InputStreamReader(it, StandardCharsets.UTF_8).readText() 
                }
            } else {
                // Try from file system (project root)
                val fileResource = resourceLoader.getResource("file:README.md")
                if (!fileResource.exists()) {
                    return "<h1>README.md not found</h1>"
                }
                fileResource.inputStream.use { 
                    InputStreamReader(it, StandardCharsets.UTF_8).readText() 
                }
            }
        } catch (e: Exception) {
            return "<h1>Error reading README.md: ${e.message}</h1>"
        }
        
        // Set up parser with extensions
        val extensions = listOf(TablesExtension.create())
        val parser = Parser.builder().extensions(extensions).build()
        val document = parser.parse(markdownContent)
        
        // Render to HTML
        val renderer = HtmlRenderer.builder().extensions(extensions).build()
        val htmlContent = renderer.render(document)
        
        // Wrap HTML content in a basic HTML document with some styling
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Monitor Service Persistence</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 900px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    pre, code {
                        background-color: #f6f8fa;
                        border-radius: 3px;
                        font-family: SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace;
                    }
                    pre {
                        padding: 16px;
                        overflow: auto;
                    }
                    code {
                        padding: 0.2em 0.4em;
                    }
                    pre code {
                        padding: 0;
                    }
                    table {
                        border-collapse: collapse;
                        width: 100%;
                    }
                    table, th, td {
                        border: 1px solid #ddd;
                    }
                    th, td {
                        padding: 8px;
                        text-align: left;
                    }
                    th {
                        background-color: #f6f8fa;
                    }
                    a {
                        color: #0366d6;
                        text-decoration: none;
                    }
                    a:hover {
                        text-decoration: underline;
                    }
                    h1, h2, h3, h4, h5, h6 {
                        margin-top: 24px;
                        margin-bottom: 16px;
                        font-weight: 600;
                        line-height: 1.25;
                    }
                    h1 {
                        padding-bottom: 0.3em;
                        font-size: 2em;
                        border-bottom: 1px solid #eaecef;
                    }
                    h2 {
                        padding-bottom: 0.3em;
                        font-size: 1.5em;
                        border-bottom: 1px solid #eaecef;
                    }
                </style>
            </head>
            <body>
                $htmlContent
            </body>
            </html>
        """.trimIndent()
    }
}
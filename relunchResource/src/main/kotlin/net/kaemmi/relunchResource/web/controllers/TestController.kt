package net.kaemmi.relunchResource.web.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ArticlesController {
    @GetMapping("/articles")
    fun getArticles() = arrayOf("Article 1", "Article 2", "Article 3")
}
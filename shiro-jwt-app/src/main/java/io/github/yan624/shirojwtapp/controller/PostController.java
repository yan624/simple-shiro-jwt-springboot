package io.github.yan624.shirojwtapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-27
 */
@RestController
public class PostController {

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable String id){
        return "a post";
    }
}

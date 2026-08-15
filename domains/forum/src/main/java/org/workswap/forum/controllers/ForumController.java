package org.workswap.forum.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.salavion.security.dto.UserAuthData;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumCommentDTO;
import org.workswap.forum.dto.ForumPostDTO;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.services.ForumCommandService;
import org.workswap.forum.services.ForumQueryService;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumCommandService forumCommandService;
    private final ForumQueryService forumQueryService;

    @GetMapping("/recent-topics")
    @PermitAll
    public List<ForumTopicDTO> getForumPage(
        @RequestParam int count,
        @RequestParam String locale,
        @RequestParam boolean translationsFilter,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return forumQueryService.getForumPage(locale, translationsFilter, count);
    }

    @GetMapping("/topic")
    @PermitAll
    public ForumTopicDTO getTopic(
        @RequestParam String topicOpenId
    ) {
        return forumQueryService.getTopic(topicOpenId);
    }

    
    @PostMapping("/topic")
    @PreAuthorize("hasAuthority('CREATE_FORUM_TOPIC')")
    public String createTopic(
        @RequestBody ForumTopicDTO topicDTO,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return forumCommandService.createTopic(authData, topicDTO).getOpenId();
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('CREATE_FORUM_POST')")
    public String createPost(
        @RequestBody ForumPostDTO postDTO,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return forumCommandService.createPost(authData, postDTO.topicOpenId(), postDTO.content()).getOpenId();
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('CREATE_FORUM_COMMENT')")
    public Long createComment(
        @RequestBody ForumCommentDTO ForumCommentDTO,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return forumCommandService.createComment(authData, ForumCommentDTO.postOpenId(), ForumCommentDTO.content()).getId();
    }

    @DeleteMapping("/topic")
    @PreAuthorize("hasAuthority('DELETE_FORUM_TOPIC')")
    public void deleteTopic(
        @RequestParam String topicOpenId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        forumCommandService.deleteTopic(authData, topicOpenId);
    }

    @DeleteMapping("/post")
    @PreAuthorize("hasAuthority('DELETE_FORUM_POST')")
    public void deletePost(
        @RequestParam String postOpenId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        forumCommandService.deletePost(authData, postOpenId);
    }

    @DeleteMapping("/comment")
    @PreAuthorize("hasAuthority('DELETE_FORUM_COMMENT')")
    public void deleteComment(
        @RequestParam Long commentId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        forumCommandService.deleteComment(authData, commentId);
    }

    @GetMapping("/tags")
    @PermitAll
    public List<CategoryDTO> getTopic() {
        return forumQueryService.getForumtags();
    }

    @GetMapping("/activity")
    @PermitAll
    public List<ForumActivityItemDTO> getForumActivity() {
        return forumQueryService.getForumActivity();
    }
}

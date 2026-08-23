package org.workswap.forum.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTag;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumCommentDTO;
import org.workswap.forum.dto.ForumPostDTO;
import org.workswap.forum.dto.ForumPostRequest;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.enums.ForumActivityType;
import org.workswap.forum.services.ForumMappingService;
import org.workswap.user.datasource.model.User;
import org.workswap.user.services.UserMappingService;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server"})
@RequiredArgsConstructor
public class ForumMappingServiceImpl implements ForumMappingService {

    private final UserMappingService userMappingService;
    
    public ForumTopicDTO toRequest(ForumTopic topic) {

        ForumTag tag = topic.getTag();
        User author = topic.getAuthor();
        
        return new ForumTopicDTO(
            topic.getOpenId(),
            topic.getTitle(),
            topic.getContent(),
            tag != null ? tag.getName() : null,
            topic.getLanguage(),
            topic.getCreatedAt(),
            author != null ? userMappingService.toShortDTO(author) : null,
            topic.getPosts().stream().map(post -> toRequest(post)).toList(),
            topic.getPosts().size()
        );
    }

    public ForumTopicDTO toDTO(ForumTopic topic) {
        ForumTag tag = topic.getTag();
        User author = topic.getAuthor();

        return new ForumTopicDTO(
            topic.getOpenId(),
            topic.getTitle(),
            topic.getContent(),
            tag != null ? tag.getName() : null,
            topic.getLanguage(),
            topic.getCreatedAt(),
            author != null ? userMappingService.toShortDTO(author) : null,
            null,
            topic.getPosts().size()
        );
    }

    public ForumPostDTO toDTO(ForumPost post) {
        User author = post.getAuthor();
        return new ForumPostDTO(
            null,
            post.getOpenId(),
            post.getContent(), 
            post.getCreatedAt(),
            author != null ? userMappingService.toShortDTO(author) : null
        );
    }

    public ForumPostRequest toRequest(ForumPost post) {
        User author = post.getAuthor();
        return new ForumPostRequest(
            post.getOpenId(), 
            post.getContent(), 
            post.getCreatedAt(),
            author != null ? userMappingService.toShortDTO(author) : null,
            post.getComments().stream().map(c -> toDTO(c)).toList()
        );
    }

    public ForumCommentDTO toDTO(ForumComment comment) {
        User author = comment.getAuthor();

        return new ForumCommentDTO(
            comment.getId(),
            comment.getContent(),
            comment.getPost().getOpenId(),
            comment.getCreatedAt(),
            author != null ? userMappingService.toShortDTO(author) : null
        );
    }

    public ForumActivityItemDTO toActivityItem(ForumTopic topic) {
        User author = topic.getAuthor();

        return new ForumActivityItemDTO(
            topic.getTitle(),
            author != null ? userMappingService.toShortDTO(author) : null,
            "/forum/topic/" + topic.getOpenId(),
            topic.getLanguage(),
            topic.getCreatedAt(),
            ForumActivityType.NEW_TOPIC.toString()
        );
    }

    public ForumActivityItemDTO toActivityItem(ForumPost post) {

        ForumTopic topic = post.getTopic();
        User author = post.getAuthor();

        return new ForumActivityItemDTO(
            post.getContent(),
            author != null ? userMappingService.toShortDTO(author) : null,
            "/forum/topic/" + topic.getOpenId(),
            topic.getLanguage(),
            post.getCreatedAt(),
            ForumActivityType.NEW_POST.toString()
        );
    }

    public ForumActivityItemDTO toActivityItem(ForumComment comment) {

        ForumPost post = comment.getPost();
        ForumTopic topic = post.getTopic();
        User author = comment.getAuthor();

        return new ForumActivityItemDTO(
            comment.getContent(),
            author != null ? userMappingService.toShortDTO(author) : null,
            "/forum/topic/" + topic.getOpenId(),
            topic.getLanguage(),
            comment.getCreatedAt(),
            ForumActivityType.NEW_COMMENT.toString()
        );
    }
}

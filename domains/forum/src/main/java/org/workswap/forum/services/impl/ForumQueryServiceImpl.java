package org.workswap.forum.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.category.services.CategoryMappingService;
import org.workswap.forum.datasource.model.ForumComment;
import org.workswap.forum.datasource.model.ForumPost;
import org.workswap.forum.datasource.model.ForumTag;
import org.workswap.forum.datasource.model.ForumTopic;
import org.workswap.forum.datasource.repository.ForumCommentRepository;
import org.workswap.forum.datasource.repository.ForumPostRepository;
import org.workswap.forum.datasource.repository.ForumTagRepository;
import org.workswap.forum.datasource.repository.ForumTopicRepository;
import org.workswap.forum.dto.ForumActivityItemDTO;
import org.workswap.forum.dto.ForumTopicDTO;
import org.workswap.forum.dto.UserForumContent;
import org.workswap.forum.services.ForumMappingService;
import org.workswap.forum.services.ForumQueryService;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server"})
@RequiredArgsConstructor
public class ForumQueryServiceImpl implements ForumQueryService {
    
    private final ForumTopicRepository topicRepository;
    private final ForumPostRepository postRepository;
    private final ForumMappingService forumMappingService;
    private final ForumTagRepository tagRepository;
    private final ForumCommentRepository commentRepository;
    private final CategoryMappingService categoryMappingService;
    
    public ForumTopicDTO getTopic(String topicOpenId) {
        ForumTopic topic = topicRepository.findTopicWithPosts(topicOpenId);
        postRepository.fetchCommentsForPosts(topic.getPosts());
        return forumMappingService.toRequest(topic);
    }

    public List<ForumTopicDTO> getForumPage(
        String lang,
        boolean translationsFilter,
        int count
    ) {
        //простейшая защита от перегрузки, потому что количество будет контролироваться на фронте
        if (count > 30) count = 30;

        List<String> languages = new ArrayList<>();

        if (translationsFilter) {
            if (!languages.contains(lang)) {
                languages.add(lang);
            }
        } else {
            languages.addAll(LanguageUtils.SUPPORTED_LANGUAGES);
        }

        List<ForumTopic> topics = topicRepository.findByLanguagesWithAuthor(
            languages, 
            PageRequest.of(0, 20, Sort.by("createdAt").descending())
        );

        List<ForumTopicDTO> forumPage = topics.stream().map(t -> forumMappingService.toDTO(t)).toList();

        return forumPage;
    }

    public List<CategoryDTO> getForumtags() {
        List<ForumTag> tags = tagRepository.findAll();
        List<CategoryDTO> dtos = categoryMappingService.toDTOList(tags);
        return dtos;
    }

    public UserForumContent getUserForumContent(Long userId) {
        List<ForumTopic> topics = topicRepository.findByAuthorId(userId);
        List<ForumPost> posts = postRepository.findByAuthorId(userId);
        List<ForumComment> comments = commentRepository.findByAuthorId(userId);

        return new UserForumContent(
            topics.stream().map(t -> forumMappingService.toDTO(t)).toList(), 
            posts.stream().map(p -> forumMappingService.toDTO(p)).toList(), 
            comments.stream().map(c -> forumMappingService.toDTO(c)).toList()
        );
    }

    public List<ForumActivityItemDTO> getForumActivity() {
        Pageable pageable = PageRequest.of(0, 15);
        List<ForumActivityItemDTO> activity = new ArrayList<>();
        activity.addAll(
            topicRepository.findAllByOrderByCreatedAtDesc(pageable)
                .getContent().stream().map(i -> forumMappingService.toActivityItem(i)).toList());
        activity.addAll(
            postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .getContent().stream().map(i -> forumMappingService.toActivityItem(i)).toList());
        activity.addAll(
            commentRepository.findAllByOrderByCreatedAtDesc(pageable)
                .getContent().stream().map(i -> forumMappingService.toActivityItem(i)).toList());
        
        return activity;
    }
}

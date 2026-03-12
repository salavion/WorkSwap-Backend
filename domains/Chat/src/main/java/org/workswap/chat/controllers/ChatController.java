package org.workswap.chat.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.chat.services.ChatCommandService;
import org.workswap.chat.services.ChatQueryService;
import org.salavion.security.dto.UserAuthData;

import jakarta.annotation.security.PermitAll;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatQueryService chatQueryService;
    private final ChatCommandService chatCommandService;

    @GetMapping("/listing-discussion")
    @PermitAll
    public Long getOrCreateListingDiscussion(
        @RequestParam Long listingId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return chatQueryService.getOrCreateListingDiscussion(authData, listingId).getId();
    }

    @GetMapping("/private-chat")
    @PermitAll
    public Long getOrCreatePrivateChat(
        @RequestParam Long interlocutorId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return chatQueryService.getOrCreatePrivateChat(authData, interlocutorId).getId();
    }

    @GetMapping("/event-chat")
    @PermitAll
    public Long getOrCreateEventChat(
        @RequestParam Long eventId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return chatQueryService.getOrCreateEventChat(authData, eventId).getId();
    }

    @PatchMapping("/{chatid}/chat-terms")
    @PreAuthorize("hasAuthority('CHAT_ACCEPT_TERMS')")
    public boolean getTermsState(
        @PathVariable Long chatId, 
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return chatQueryService.isChatTermsAccepted(chatId, authData);
    }

    @PatchMapping("/{chatId}/accept-terms")
    @PreAuthorize("hasAuthority('CHAT_ACCEPT_TERMS')")
    public void acceptTerms(@PathVariable Long chatId, @AuthenticationPrincipal UserAuthData authData) {
        chatCommandService.acceptChatTerms(chatId, authData);
    }

    @DeleteMapping("/temporary")
    @PreAuthorize("hasAuthority('CLEAR_TEMPORARY_CHATS')")
    public void deleteTemporaryChat(@AuthenticationPrincipal UserAuthData authData) {
        chatCommandService.deleteTemporaryChats(authData);
    }
}


package org.workswap.listing.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.workswap.listing.dto.CatalogFilterDTO;
import org.workswap.listing.dto.CatalogRequest;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.services.ListingCommandService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.ListingStorageService;
import org.salavion.security.dto.UserAuthData;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/listing")
public class ListingsController {

    private final ListingQueryService listingQueryService;
    private final ListingCommandService listingCommandService;
    private final ListingStorageService listingStorageService;

    @GetMapping("/{listingId}")
    @PreAuthorize("hasAuthority('GET_LISTING_BY_ID')")
    public ListingDTO.Full getListing(
        @AuthenticationPrincipal UserAuthData authData, 
        @PathVariable Long listingId, 
        @RequestParam(required = false) String token,
        @RequestParam String locale) {

        return listingQueryService.getListingDTO(listingId, authData, locale);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_LISTING')")
    public Long createListing(
        @RequestParam String type,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return listingCommandService.create(authData, type).getId();
    }

    @DeleteMapping("/{listingId}")
    @PreAuthorize("hasAuthority('DELETE_LISTING')")
    public void deleteListing(@PathVariable Long listingId, @AuthenticationPrincipal UserAuthData authData ) {
        listingCommandService.delete(authData, listingId);
    }

    @GetMapping("/{listingId}/page")
    @PreAuthorize("hasAuthority('GET_LISTING_BY_ID')")
    public ListingDTO.Page getListingPage(
        @AuthenticationPrincipal UserAuthData authData, 
        @PathVariable Long listingId, 
        @RequestParam(required = false) String token,
        @RequestParam String locale) {
        return listingQueryService.getListingPage(authData, token, listingId, locale);
    }

    @PostMapping("/catalog") 
    @PreAuthorize("hasAuthority('LOAD_CATALOG')")
    public CatalogRequest getSortedCatalog(
            @RequestBody CatalogFilterDTO filters,
            @RequestParam String locale,
            @AuthenticationPrincipal UserAuthData authData
    ) {
        return listingQueryService.getSortedCatalog(authData, filters, locale);
    }

    @GetMapping("/drafts")
    @PreAuthorize("hasAuthority('VIEW_LISTINGS_DRAFTS')")
    public List<ListingDTO.Full> getDraftListings(
            @AuthenticationPrincipal UserAuthData authData, 
            @RequestParam String locale
    ) {
        return listingQueryService.getDrafts(authData, locale);
    }

    @PostMapping("/{listingId}/favorite")
    @PreAuthorize("hasAuthority('FAVORITE_LISTING')")
    public void addFavorite(@PathVariable Long listingId, @AuthenticationPrincipal UserAuthData authData) {
        listingCommandService.addListingToFavorite(authData, listingId);
    }

    @DeleteMapping("/{listingId}/favorite")
    @PreAuthorize("hasAuthority('FAVORITE_LISTING')")
    public void removeFavorite(@PathVariable Long listingId, @AuthenticationPrincipal UserAuthData authData) {
        listingCommandService.removeListingFromFavorite(authData, listingId);
    }

    @GetMapping("/{listingId}/favorite")
    @PreAuthorize("hasAuthority('CHECK_FAVORITE_LISTING')")
    public boolean isFavorite(@PathVariable Long listingId, @AuthenticationPrincipal UserAuthData authData) {
        return listingQueryService.isFavorite(authData, listingId);
    }

    @PatchMapping("/{listingId}/publish")
    @PreAuthorize("hasAuthority('PUBLISH_LISTING')")
    public void publishListing(@PathVariable Long listingId, @AuthenticationPrincipal UserAuthData authData) {
        listingCommandService.publish(authData, listingId);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('GET_LISTINGS_LIST')")
    public Page<ListingDTO.Full> getListingsPage(
        @RequestParam int page, 
        @RequestParam int amount, 
        @RequestParam String sortParam,
        @RequestParam String locale
    ) {
        return listingQueryService.getListingsPage(page, amount, sortParam, locale);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('GET_RECENT_LISTINGS')")
    public List<ListingDTO.Full> getRecentListings(
        @RequestParam int amount,
        @RequestParam String locale
    ) {
        return listingQueryService.getRecentListings(amount, locale);
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasAuthority('GET_OWN_LISTINGS')")
    public List<ListingDTO.Full> getMyListings(@AuthenticationPrincipal UserAuthData authData, @RequestParam String locale) {
        return listingQueryService.getOwnListingsByUser(authData, locale);
    }

    @GetMapping("/by-user")
    @PermitAll
    public List<ListingDTO.Full> getListingsByUser(@RequestParam Long userId, @RequestParam String locale) {
        return listingQueryService.getListingDtosByUser(userId, locale);
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAuthority('GET_FAVORITES_LISTINGS')")
    public List<ShortListingDTO> getFavorites(@AuthenticationPrincipal UserAuthData authData, @RequestParam String locale) {
        return listingQueryService.getFavorites(authData, locale);
    }

    @GetMapping("/{listingId}/images")
    @PermitAll
    public List<ImageDTO> getImages(@PathVariable Long listingId) {
        return listingQueryService.getImages(listingId);
    }

    @GetMapping("/{listingId}/translations")
    @PermitAll
    public Map<String, ListingTranslationDTO> getTranslations(@PathVariable Long listingId) {
        return listingQueryService.getTranslations(listingId);
    }

    @GetMapping("/{listingId}/token")
    @PermitAll
    public String getToken(@AuthenticationPrincipal UserAuthData authData, @PathVariable Long listingId) {
        return listingQueryService.getListingToken(authData, listingId);
    }

    @PatchMapping("/{listingId}/modify")
    @PreAuthorize("hasAuthority('UPDATE_LISTING')")
    public void modifyListing(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable Long listingId,
        @RequestBody Map<String, Object> updates
    ) throws AccessDeniedException {
        listingCommandService.modifyListingParam(authData, listingId, updates);
    }

    @PatchMapping("/{listingId}/modify/translations")
    @PreAuthorize("hasAuthority('UPDATE_LISTING')")
    public Set<String> updateListingTranslations(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable Long listingId,
        @RequestBody Map<String, ListingTranslationDTO> translations
    ) throws AccessDeniedException {
        return listingCommandService.updateListingTranslations(authData, listingId, translations);
    }

    @PostMapping("/{listingId}/image")
    public ImageDTO uploadListingImage(
        @RequestParam MultipartFile image,
        @RequestParam Long listingId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        return listingStorageService.uploadListingImage(image, listingId, authData);
    }

    @DeleteMapping("/{imageId}/image")
    public void deleteListingImage(
        @RequestParam Long imageId,
        @AuthenticationPrincipal UserAuthData authData
    ) {
        listingStorageService.deleteListingImage(imageId, authData);
    }
}

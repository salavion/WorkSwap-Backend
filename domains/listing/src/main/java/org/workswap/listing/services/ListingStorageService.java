package org.workswap.listing.services;

import org.workswap.security.dto.UserAuthData;
import org.springframework.web.multipart.MultipartFile;
import org.workswap.listing.dto.ImageDTO;

public interface ListingStorageService {
    
    ImageDTO uploadListingImage(MultipartFile file, Long listingId, UserAuthData authData);
    void deleteListingImage(Long imageId, UserAuthData authData);
}

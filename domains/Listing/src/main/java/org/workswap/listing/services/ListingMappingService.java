package org.workswap.listing.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.EventSettingsDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ShortListingDTO;

public interface ListingMappingService {

    ListingDTO toDTO(Listing listing, Locale locale);
    ShortListingDTO toShortDTO(Listing listing, Locale locale);
    EventSettingsDTO toEventSettingsDTO(Listing listing);

    void setListingCategoryMeta(Listing listing);

    List<ListingDTO> toDTOList(Collection<Listing> listings, Locale locale);
    List<ShortListingDTO> toShortDTOList(Collection<Listing> listings, Locale locale);
}

package org.workswap.listing.services;

import java.util.Collection;
import java.util.List;

import org.workswap.listing.datasource.model.Image;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.EventDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ShortListingDTO;

public interface ListingMappingService {

    ListingDTO.Full toDTO(Listing listing, String locale);
    ShortListingDTO toShortDTO(Listing listing, String locale);
    EventDTO.Settings toEventSettingsDTO(Listing listing);
    String getImageLink(Image image);

    void setListingCategoryMeta(Listing listing);

    List<ListingDTO.Full> toDTOList(Collection<Listing> listings, String locale);
    List<ShortListingDTO> toShortDTOList(Collection<Listing> listings, String locale);
}

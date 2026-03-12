package org.workswap.listing.datasource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class ListingTranslation {

    public ListingTranslation(String language,
                              String title,
                              String description,
                              Listing listing) {
        this.language = language;
        this.title = title;
        this.description = description;
        this.listing = listing;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;

    @Setter
    private String title;

    @Setter
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;
}
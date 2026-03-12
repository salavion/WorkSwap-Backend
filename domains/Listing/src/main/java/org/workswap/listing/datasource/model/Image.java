package org.workswap.listing.datasource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Image {

    public Image(String path,
                 Listing listing) {
        this.path = path;
        this.listing = listing;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}
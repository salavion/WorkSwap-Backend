package org.workswap.listing.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class ListingLocalizationService {

    public List<Listing> localizeList(List<Listing> listings, Locale locale) {
        for (Listing listing : listings) {
            localizeListing(listing, locale);
        }

        return listings;
    }

    public void localizeListing(Listing listing, Locale locale) {
        String lang = locale.getLanguage();
        Map<String, ListingTranslation> translations = listing.getTranslations();

        ListingTranslation selected = translations.get(lang);

        // fallback, если нужного языка нет
        if (selected == null || isBlank(selected.getTitle()) || isBlank(selected.getDescription())) {
            // Приоритет: fi > ru > en
            for (String fallbackLang : LanguageUtils.SUPPORTED_LANGUAGES) {
                selected = translations.get(fallbackLang);
                if (selected != null && !isBlank(selected.getTitle()) && !isBlank(selected.getDescription())) {
                    break;
                }
            }
        }

        if (selected != null) {
            listing.setLocalizedTitle(safe(selected.getTitle()));
            listing.setLocalizedDescription(safe(selected.getDescription()));
        } else {
            listing.setLocalizedTitle(null);
            listing.setLocalizedDescription(null);
        }
    }

    private String safe(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

package com.ahmed.customarsenal.item;

import com.ahmed.customarsenal.ability.CustomAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CustomItemSearch {

    public static List<CustomItem> filter(CustomItemCategory category, String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim().toLowerCase(Locale.ROOT);
        List<CustomItem> results = new ArrayList<>();

        for (CustomItem item : CustomItemRegistry.getAll()) {
            CustomItemData data = item.getCustomData();
            if (category != CustomItemCategory.ALL && data.category() != category) {
                continue;
            }
            if (query.isEmpty() || matchesQuery(data, query)) {
                results.add(item);
            }
        }
        return results;
    }

    private static boolean matchesQuery(CustomItemData data, String q) {
        if (data.id().toLowerCase(Locale.ROOT).contains(q)) return true;
        if (data.displayName().toLowerCase(Locale.ROOT).contains(q)) return true;
        if (data.element().displayName().toLowerCase(Locale.ROOT).contains(q)) return true;
        for (CustomAbility ability : data.abilities()) {
            if (ability.displayName().toLowerCase(Locale.ROOT).contains(q)) return true;
        }
        return false;
    }
}

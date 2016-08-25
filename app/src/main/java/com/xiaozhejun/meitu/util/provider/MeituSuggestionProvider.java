package com.xiaozhejun.meitu.util.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by yangzhe on 16-8-25.
 */
public class MeituSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.xiaozhejun.meitu.util.provider.MeituSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MeituSuggestionProvider(){
        setupSuggestions(AUTHORITY,MODE);
    }
}

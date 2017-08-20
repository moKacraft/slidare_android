package epitech.eip.slidare.util;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by julienathomas on 20/08/2017.
 */

public class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
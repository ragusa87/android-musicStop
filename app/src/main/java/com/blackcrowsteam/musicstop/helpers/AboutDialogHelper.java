/*
 * Copyright 2015 Laurent Constantin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.blackcrowsteam.musicstop.helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackcrowsteam.musicstop.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AboutDialogHelper {
    private final Context mContext;
    private final Bundle mBundle;

    public AboutDialogHelper(Context context) {
        this.mContext = context;
        this.mBundle = new Bundle();
    }

    /**
     * Open website
     *
     * @param context context
     */
    private static void openWebsite(Context context, String url) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(urlIntent);
    }

    /**
     * Load a file and return is content as string
     *
     * @return File content
     */
    private String loadLicencesHtml() {
        try {
            StringBuilder response = new StringBuilder();
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open("licenses.html")));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get application's version as string
     *
     * @return Application version
     */
    private String getVersionString() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            if (packageInfo != null) {
                return packageInfo.versionName;
            }
        } catch (NameNotFoundException e) {
            Debug.Log.e("Error", e);
        }
        return "";
    }

    /**
     * Affiche la boite de dialogue "A Propos".
     *
     */
    public void showAbout() {

        // Alert
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.action_about));
        builder.setCancelable(true);

        // Layout
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.about_box, null);
        builder.setView(layout);

        // Font
        TextView title = (TextView) layout.findViewById(R.id.about_title);
        Typeface light = Typeface.create((String) null, Typeface.BOLD);
        title.setTypeface(light);

        // Load title + version
        title.setText(mContext.getString(R.string.about_title, mContext.getString(R.string.app_name), getVersionString()));
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mBundle.putBoolean("about", false);
            }
        });
        // Add buttons + listeners
        builder.setPositiveButton(mContext.getText(R.string.about_site), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openWebsite();
            }

        });
        builder.setNegativeButton(mContext.getString(R.string.about_licence), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mBundle.putBoolean("about", false);
                showLicence();
            }
        });
        mBundle.putBoolean("about", true);
        builder.create().show();
    }

    /**
     * Show licence
     */
    private void showLicence() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //builder.setTitle(context.getString(R.string.about_licence));
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mBundle.putBoolean("licence", false);
            }
        });
        // layout
        LinearLayout linearLayout = new LinearLayout(mContext);

        final ProgressDialog progress = new ProgressDialog(mContext);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.setIndeterminate(true);
        progress.show();

        WebView webview = new WebView(mContext);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                openWebsite(mContext, url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.dismiss();
            }
        });
        linearLayout.addView(webview);
        builder.setView(linearLayout);

        // Listeners
        builder.setNeutralButton(mContext.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBundle.putBoolean("licence", false);
                dialog.dismiss();
            }
        });

        builder.create().show();
        mBundle.putBoolean("licence", true);
        // Load html view
        webview.loadDataWithBaseURL("file:///android_asset/",
                loadLicencesHtml(), "text/html", "utf-8",
                null);
    }

    /**
     * Open website
     *
     */
    private void openWebsite() {
        final String url = mContext.getString(R.string.about_site_url);
        openWebsite(mContext, url);
    }

    /**
     * Restore dialog status
     * @param prefs bundle
     */
    public void load(Bundle prefs) {
        if (prefs.getBoolean("about", false)) {
            this.showAbout();
        }
        if (prefs.getBoolean("licence", false)) {
            this.showLicence();
        }
    }

    /**
     * Save dialog status
     * @param savedInstanceState bundle
     */
    public void save(Bundle savedInstanceState) {
        savedInstanceState.putAll(mBundle);
    }
}
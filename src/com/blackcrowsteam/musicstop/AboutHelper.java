/*
 * Copyright 2012 Laurent Constantin <android@blackcrowsteam.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blackcrowsteam.musicstop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Aide pour les boites de dialogue "A Propos" et "Licence"
 * 
 * @author Laurent Constantin
 */
public class AboutHelper {
	/**
	 * Charge un fichier et renvoie son contenu
	 * 
	 * @param mContext Context
	 * @param mFilename Nom du fichier
	 * @return Contenu du fichier
	 */
	private static String loadFileText(final Context mContext,
			final String mFilename) {
		try {
			StringBuffer mFileData = new StringBuffer();
			final BufferedReader mReader = new BufferedReader(
					new InputStreamReader(mContext.getAssets().open(mFilename)));
			String line;
			while ((line = mReader.readLine()) != null) {
				mFileData.append(line);
			}
			return mFileData.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Renvoie la version de l'application
	 * 
	 * @param activity Activite
	 * @return Version de l'application
	 */
	public static String getVersionString(final Context mContext) {
		String version = "";
		try {
			PackageInfo pi = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			if (pi != null) {
				version = pi.versionName;
			}
		} catch (NameNotFoundException e) {
			return "";
		}
		return version;
	}

	/**
	 * Affiche la boite de dialogue "A Propos".
	 * 
	 * @param mContext Context
	 */
	public static void showAbout(final Context mContext) {
		final String mWebBoutton = mContext.getString(R.string.about_site);
		final String mLicenceButton = mContext
				.getString(R.string.about_licence);
		final String mAboutTitle = mContext.getString(R.string.about_label);

		// Cree une AlertBox
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(mAboutTitle);
		builder.setCancelable(true);

		// Charge le layout
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_box, null);
		builder.setView(layout);

		// Change la typo du titre
		TextView title = (TextView) layout.findViewById(R.id.about_title);
		Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
		title.setTypeface(light);

		// Change le titre (nom + version)
		title.setText(mContext.getString(R.string.app_name) + " "
				+ getVersionString(mContext));

		// Ajoute des boutons pour "A Propos"
		// Bouton pour ouvrir la page web
		builder.setPositiveButton(mWebBoutton, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				AboutHelper.openWebsite(mContext);
			}

		});
		// Bouton pour ouvrir la licence
		builder.setNegativeButton(mLicenceButton, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showLicence(mContext);
			}
		});

		// Affiche la boite de dialgue cree
		builder.create().show();
	}

	/**
	 * Affiche la boite de dialogue "Licence"
	 * 
	 * @param mContext
	 */

	public static void showLicence(final Context mContext) {
		final String LICENCE_TITLE = mContext.getString(R.string.about_licence);
		final String BOUTTON_OK = mContext.getString(android.R.string.ok);
		final String LICENCE_FILE = "licenses.html";

		// Cree une AlertBox
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(LICENCE_TITLE);
		builder.setCancelable(true);

		// Cree le layout (sans XML)
		LinearLayout linearLayout = new LinearLayout(mContext);
		WebView webview = new WebView(mContext);
		// Charge la page web (locale)
		webview.loadDataWithBaseURL("file:///android_asset/",
				loadFileText(mContext, LICENCE_FILE), "text/html", "utf-8",
				null);
		linearLayout.addView(webview);
		builder.setView(linearLayout);

		// Pour la licence, bouton back
		builder.setNeutralButton(BOUTTON_OK, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		// Affiche la boite de dialgue cree
		builder.create().show();
	}

	/**
	 * Ouvre le site web
	 * 
	 * @param mContext Contexte de l'application
	 */
	public static void openWebsite(Context mContext) {
		final String WEB_URL = mContext.getString(R.string.about_site_url);
		Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL));
		mContext.startActivity(urlIntent);
	}
}

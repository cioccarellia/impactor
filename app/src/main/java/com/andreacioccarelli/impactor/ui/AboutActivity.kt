package com.andreacioccarelli.impactor.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.BuildConfig
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.tools.ClickListener
import com.andreacioccarelli.impactor.tools.LicensesTouchListener
import com.andreacioccarelli.impactor.tools.PreferenceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList

import java.util.ArrayList

import es.dmoral.toasty.Toasty

class AboutActivity : MaterialAboutActivity() {

    private val prefs: PreferenceBuilder by lazy { PreferenceBuilder(this@AboutActivity, PreferenceBuilder.DefaultFilename) }

    private var GITHUB = "https://github.com/AndreaCioccarelli"
    private var GOOGLE_PLUS = "https://plus.google.com/+AndreaCioccarelli"
    private var TWITTER = "https://twitter.com/ACioccarelli"
    private var RATE_ON_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=com.andreacioccarelli.impactor"

    private var libList: MutableList<Library>? = null

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        val appCardBuilder = MaterialAboutCard.Builder()

        appCardBuilder.addItem(MaterialAboutTitleItem.Builder()
                .text("Impactor")
                .icon(R.mipmap.ic_launcher)
                .build())

        appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Version")
                .subText(BuildConfig.VERSION_NAME + (if (BuildConfig.DEBUG) " Debug" else " Official") + " (Build " + BuildConfig.VERSION_CODE.toString() + ")")
                .icon(R.drawable.about_version)
                .build())

        appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Package name")
                .subText(packageName)
                .icon(R.drawable.about_packagename)
                .build())

        appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Licenses")
                .icon(R.drawable.about_license)
                .setOnClickAction { this.showLicenseDialog() }
                .build())


        val appActionsBuilder = MaterialAboutCard.Builder()

        appActionsBuilder.title("Quick Actions")

        appActionsBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Rate this app")
                .subText("On the Google Play Store")
                .icon(R.drawable.about_rate)
                .setOnClickAction { openUrl(RATE_ON_GOOGLE_PLAY) }
                .build())

        /*appActionsBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Join in the G+ Community")
                .icon(R.drawable.about_community_google_plus)
                .setOnClickAction(() -> openUrl(GOOGLE_PLUS_COMMUNITY))
                .build());*/

        /*appActionsBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Check for Updates")
                .icon(R.drawable.about_updates)
                .setOnClickAction(this::checkForUpdates)
                .build());*/

        appActionsBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("App Details")
                .subText("Jump in the Detailed app page")
                .setOnClickAction { this.openAppDetails() }
                .icon(R.drawable.about_settings)
                .build())


        val appAuthorBuilder = MaterialAboutCard.Builder()

        appAuthorBuilder.title("Author")

        appAuthorBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Andrea Cioccarelli")
                .subText("Italy, Lombardia")
                .icon(R.drawable.about_me)
                .build())

        appAuthorBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Send me a mail")
                .icon(R.drawable.about_mail)
                .setOnClickAction { this.sendMail() }
                .build())

        appAuthorBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Follow on Twitter")
                .icon(R.drawable.about_twitter)
                .setOnClickAction { openUrl(TWITTER) }
                .build())

        appAuthorBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Follow on GitHub")
                .icon(R.drawable.about_github)
                .setOnClickAction { openUrl(GITHUB) }
                .build())

        appAuthorBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Add to Google Plus")
                .icon(R.drawable.about_google_plus)
                .setOnClickAction { openUrl(GOOGLE_PLUS) }
                .build())

        if (prefs.getBoolean("show_website", false)) {
            appAuthorBuilder.addItem(MaterialAboutActionItem.Builder()
                    .text("View Website")
                    .icon(R.drawable.about_open_in_browser)
                    .setOnClickAction { openUrl(prefs.getString("website_url", "")) }
                    .build())
        }


        return MaterialAboutList(appCardBuilder.build(), appActionsBuilder.build(), appAuthorBuilder.build())
    }

    override fun getActivityTitle(): CharSequence? {
        return "About"
    }

    private fun sendMail() {
        try {
            val email = Intent()
            email.action = Intent.ACTION_SENDTO
            email.data = Uri.parse("mailto:")
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("cioccarelliandrea01@gmail.com"))
            email.putExtra(Intent.EXTRA_SUBJECT, "IMPACTOR UNROOT")
            startActivity(email)
        } catch (e: ActivityNotFoundException) {
            Toasty.error(baseContext, "No mail app found. Please install one from play store").show()
        }

    }

    private fun openAppDetails() {
        val packageName = packageName
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            startActivity(intent)
        }

    }

    private fun showLicenseDialog() {
        val licenseDialog = MaterialDialog.Builder(this@AboutActivity)
                .customView(R.layout.dialog_licenses, true)
                .cancelable(true)
                .autoDismiss(true)
                .title("Licenses")
                .positiveText("CLOSE")
                .build()

        val RecyclerView = licenseDialog.customView!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.LicensesRecyclerView)

        val LayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@AboutActivity)
        RecyclerView.layoutManager = LayoutManager
        RecyclerView.setHasFixedSize(true)
        RecyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        RecyclerView.addOnItemTouchListener(LicensesTouchListener(applicationContext, RecyclerView, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                openUrl(libList!![position].URL)
            }

            override fun onLongClick(view: View, position: Int) {

            }
        }))
        initializeData()

        val adapter = RVAdapter(libList!!.toList())
        RecyclerView.adapter = adapter

        licenseDialog.show()
    }

    fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this@AboutActivity, R.color.colorPrimaryDark))
        val customTabsIntent = builder.build()
        try {
            customTabsIntent.launchUrl(this@AboutActivity, Uri.parse(url))
        } catch (error: RuntimeException) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    class Library internal constructor(internal var title: String, internal var author: String, internal var license: String, internal var URL: String)

    private fun initializeData() {
        libList = ArrayList()
        libList!!.add(Library("Material Dialogs", "Aidan follestad", LICENSE_APACHE2, "https://github.com/afollestad/material-dialogs"))
        libList!!.add(Library("Assent", "Aidan follestad", LICENSE_APACHE2, "https://github.com/afollestad/assent"))
        libList!!.add(Library("Toasty", "GrenderG", LICENSE_GNU, "https://github.com/GrenderG/Toasty"))
        libList!!.add(Library("Root Shell", "Jrummy Apps Inc.", LICENSE_APACHE2, "https://github.com/jrummyapps/android-shell/"))
        libList!!.add(Library("Device Names", "Jrummy Apps Inc.", LICENSE_APACHE2, "https://github.com/jaredrummler/AndroidDeviceNames"))
        libList!!.add(Library("Chrome Custom Tabs", "Google Inc.", LICENSE_CCBY3, "https://developer.chrome.com/multidevice/android/customtabs"))
        libList!!.add(Library("Material Icons", "Google Inc.", LICENSE_APACHE2, "https://material.io/icons/"))
    }

    internal class RVAdapter(private val libs: List<Library>) : androidx.recyclerview.widget.RecyclerView.Adapter<RVAdapter.LibraryViewHolder>() {

        override fun getItemCount(): Int {
            return libs.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LibraryViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.license_item, viewGroup, false)
            return LibraryViewHolder(v)
        }

        override fun onBindViewHolder(LibraryViewHolder: LibraryViewHolder, i: Int) {
            LibraryViewHolder.libraryTitle.text = libs[i].title
            LibraryViewHolder.libraryContent.text = libs[i].author + " | " + libs[i].license
        }

        internal class LibraryViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

            private var cardView: androidx.cardview.widget.CardView
            var libraryTitle: TextView
            var libraryContent: TextView

            init {
                cardView = itemView.findViewById(R.id.LibraryCard)
                libraryTitle = itemView.findViewById(R.id.LibraryTitle)
                libraryContent = itemView.findViewById(R.id.LibraryContent)

            }
        }
    }

    companion object {
        internal var LICENSE_APACHE2 = "Apache License 2.0"
        internal var LICENSE_GNU = "GNU general Public License"
        internal var LICENSE_CCBY3 = "CC-By 3.0 License"
    }
}

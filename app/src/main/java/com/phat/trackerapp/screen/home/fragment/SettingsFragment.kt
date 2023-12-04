package com.phat.trackerapp.screen.home.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.phat.trackerapp.BuildConfig
import com.phat.trackerapp.R
import com.phat.trackerapp.data.database.TrackerDatabase
import com.phat.trackerapp.screen.home.HomeActivity
import com.phat.trackerapp.screen.language.activity.LanguageActivity
import com.phat.trackerapp.screen.yourdata.YourDataActivity
import com.phat.trackerapp.utils.Constants
import kotlinx.android.synthetic.main.fragment_settings.btnExportAsFile
import kotlinx.android.synthetic.main.fragment_settings.btnFeedback
import kotlinx.android.synthetic.main.fragment_settings.btnLanguageOptions
import kotlinx.android.synthetic.main.fragment_settings.btnPrivacy
import kotlinx.android.synthetic.main.fragment_settings.btnRateUs
import kotlinx.android.synthetic.main.fragment_settings.btnShare
import kotlinx.android.synthetic.main.fragment_settings.btnYourData
import java.io.File


class SettingsFragment : Fragment() {

    private lateinit var mDir: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        handleEvents()
    }

    private fun initData() {

        try {
            mDir =
                File(requireContext().getExternalFilesDir(null)?.absoluteFile?.absolutePath.toString() + "/" + Constants.DB_NAME)
            mDir.mkdir()
            csvWriter().open(mDir.absolutePath + "/blood_tracker.csv") {
                // Header
                writeRow(
                    listOf(
                        "Index",
                        "Time",
                        "Date",
                        "Systolic",
                        "Diastolic",
                        "Pulse",
                        "State",
                        "Tag"
                    )
                )

                TrackerDatabase.getInstance(requireContext()).trackerDao().getAll()
                    .forEachIndexed { index, director ->
                        writeRow(
                            listOf(
                                director.id,
                                director.time,
                                director.date,
                                director.systolic,
                                director.diastolic,
                                director.pulse,
                                director.systolic,
                                director.tag
                            )
                        )
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun handleEvents() {
        btnYourData.setOnClickListener {
            val intent = Intent(requireContext(),YourDataActivity::class.java)
            if(context is HomeActivity){
                (context as HomeActivity).showScreen(intent)
            }
        }

        btnLanguageOptions.setOnClickListener {
            val intent = Intent(requireContext(),LanguageActivity::class.java)
            if(context is HomeActivity){
                (context as HomeActivity).showScreen(intent)
            }
        }

        btnExportAsFile.setOnClickListener {
            try {

                val file = File(mDir.absolutePath + "/blood_tracker.csv")
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.type = "*/*"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    startActivity(intent)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }

        btnRateUs.setOnClickListener {
            ratingApp()
        }

        btnShare.setOnClickListener {

            shareApp()
        }

        btnFeedback.setOnClickListener {
            feedback()
        }

        btnPrivacy.setOnClickListener {
            policy()
        }

    }

    private fun policy() {
        try {
            val url = Constants.LINK_PRIVACY
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun shareApp() {
        try {
            ShareCompat.IntentBuilder.from(requireActivity())
                .setType("text/plain")
                .setChooserTitle("Chooser title")
                .setText("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                .startChooser()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun feedback() {
        try {
            val emailIntent =
                Intent(
                    Intent.ACTION_SENDTO,
                    Uri.parse("mailto:" + getString(R.string.txt_email_feedback))
                )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack PDF")
            startActivity(Intent.createChooser(emailIntent, "Send FeedBack"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ratingApp() {
        val uri: Uri = Uri.parse("market://details?id=${requireContext().packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${requireContext().packageName}")
                )
            )
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
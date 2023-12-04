package com.phat.trackerapp.screen.heartrate.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.phat.trackerapp.R
import kotlinx.android.synthetic.main.fragment_animation.gifGuiline


class AnimationFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            AnimationFragment().apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        Log.d("11111222","onResume")
        Glide.with(requireContext()).load("https://alloffice.app/nghia/BloodPressure/bloodpressure.gif").into(gifGuiline)
    }

}
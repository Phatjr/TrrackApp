package com.phat.trackerapp.screen.heartrate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.phat.trackerapp.R
import kotlinx.android.synthetic.main.fragment_instruction.tvInstructOne


class InstructionFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            InstructionFragment().apply {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instruction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextRed()
    }


    private fun setTextRed(){
        val red = "fully covers the lens"
        tvInstructOne.text = HtmlCompat.fromHtml(getString(R.string.txt_intruct_1).replace(red, "<font color='#EE403F'>${red}</font>"),HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}
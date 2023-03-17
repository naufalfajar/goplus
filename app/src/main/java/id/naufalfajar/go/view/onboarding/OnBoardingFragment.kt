package id.naufalfajar.go.view.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import id.naufalfajar.go.R
import id.naufalfajar.go.databinding.FragmentOnBoardingBinding
import id.naufalfajar.go.view.onboarding.screen.FirstScreen
import id.naufalfajar.go.view.onboarding.screen.SecondScreen

class OnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_on_boarding, container, false)
        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.vp2_onboarding)
        viewPager.adapter = adapter

        return view
    }
}
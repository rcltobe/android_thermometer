package jp.aoyama.mki.thermometer.view.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import jp.aoyama.mki.thermometer.view.attendance.fragments.AttendanceFragment
import jp.aoyama.mki.thermometer.view.temperature.fragments.BodyTemperatureHistoryFragment
import jp.aoyama.mki.thermometer.view.user.fragments.SelectNameFragment

class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SelectNameFragment.newInstance()
            1 -> BodyTemperatureHistoryFragment.newInstance()
            2 -> AttendanceFragment.newInstance()
            else -> throw RuntimeException("position must be within ${itemCount - 1}")
        }
    }

    fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> "体温を記録"
            1 -> "履歴"
            2 -> "出席記録"
            else -> throw RuntimeException("position must be within ${itemCount - 1}")
        }
    }
}
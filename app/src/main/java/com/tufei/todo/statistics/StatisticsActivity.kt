package com.tufei.todo.statistics

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import com.tufei.todo.R
import com.tufei.todo.data.Injection
import com.tufei.todo.util.replaceFragmentInActivity
import com.tufei.todo.util.setupActionBar
import kotlinx.android.synthetic.main.statistics_act.*

/**
 * @author tufei
 * @date 2018/1/7.
 */
class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_act)

        setupActionBar(toolbar) {
            setTitle(R.string.statistics_title)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        drawer_layout.setStatusBarBackground(R.color.colorPrimaryDark)
        nav_view?.let { setupDrawerContent(it) }

        val statisticsFragment = (supportFragmentManager.findFragmentById(R.id.contentFrame)
                ?: StatisticsFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }) as StatisticsFragment

        StatisticsPresenter(
                Injection.provideTasksRepository(applicationContext),
                statisticsFragment)

    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.list_navigation_menu_item -> NavUtils.navigateUpFromSameTask(this@StatisticsActivity)
            }
            item.setChecked(true)
            drawer_layout.closeDrawers()
            true
        }
    }
}
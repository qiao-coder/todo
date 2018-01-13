package com.tufei.todo.tasks

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tufei.todo.R
import com.tufei.todo.data.Injection
import com.tufei.todo.statistics.StatisticsActivity
import com.tufei.todo.util.replaceFragmentInActivity
import com.tufei.todo.util.setupActionBar
import kotlinx.android.synthetic.main.tasks_act.*


/**
 * @author tufei
 * @date 2018/1/4.
 */
class TasksActivity : AppCompatActivity() {

    //少了static  final换成了val  String也没有(会自动推断？)
    //private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    //如果没有声明lateinit var,必须赋值   lateinit不能搭配val使用
    // lateinit 延迟加载 该属性或变量必须为非空类型，并且不能是原生类型。
    //private DrawerLayout mDrawerLayout;
    //插件自动转换的  val  不能再赋值，没有意义，不可取
    //private val mDrawerLayout: DrawerLayout? = null
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mTasksPresenter: TasksContract.Presenter
    /*
    java:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);
    }
    */
    //定义变量时，可在类型后面加一个问号?，表示该变量是Nullable，不加表示该变量不可为null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)
        // Set up the toolbar.
        /*
        java:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
         */

        //java转换kotlin
        //(效果一样)val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        /*val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        //getSupportActionBar()变成了supportActionBar
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab.setDisplayHomeAsUpEnabled(true)
        */

        //也可以直接导入布局,直接通过名字得到控件
        /*setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionBar.setDisplayHomeAsUpEnabled(true)
        */

        //使用扩展函数
        //下面几种写法都是等价的
        //setupActionBar((toolbar),{...})
        //setupActionBar(toolbar,{...})
        //setupActionBar(toolbar){...}
        setupActionBar(toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = drawer_layout.apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }

        setupDrawerContent(nav_view)

        /*
        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
         */
        val tasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as?TasksFragment ?: TasksFragment.newInstance().also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }

        mTasksPresenter = TasksPresenter(Injection.provideTasksRepository(applicationContext), tasksFragment)

        // Load previously saved state, if available.
        savedInstanceState?.apply {
            val currentFiltering = getSerializable(CURRENT_FILTERING_KEY)
            mTasksPresenter.currentFiltering = currentFiltering as TasksFilterType
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(CURRENT_FILTERING_KEY,mTasksPresenter.currentFiltering)
        super.onSaveInstanceState(outState)
    }

    fun setupDrawerContent(navigationView: NavigationView) {

        /*
        java:
        navigationView.setNavigationItemSelectedListener(menuItem ->{
            ......
            return true;
        });
         */

        //其实"menuItem ->"都可以省略  然后menuItem用it代替
        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.list_navigation_menu_item) {
                val intent = Intent(this@TasksActivity, StatisticsActivity::class.java)
                startActivity(intent)
            }
            //menuItem.setChecked(true)
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            //lambda中似乎不能显式写return
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Open the navigation drawer when the home icon is selected from the toolbar.
            mDrawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
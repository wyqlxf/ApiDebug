package com.wyq.api.debug.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import com.wyq.api.debug.R
import com.wyq.api.debug.adapter.MainFragmentPagerAdapter
import com.wyq.api.debug.base.BaseAppCompatActivity
import com.wyq.api.debug.fragment.DefaultFragment
import com.wyq.api.debug.fragment.FavoriteFragment
import com.wyq.api.debug.fragment.MoreFragment
import com.wyq.api.debug.fragment.RealTimeFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * 项目名称：ApiDebug
 * 创建人：WangYongQi
 * 创建时间：2018/12/27 11:14
 * 类描述：主界面
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 */
class MainActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 定义Fragment集合
        val list = ArrayList<Fragment>()
        // 添加默认界面
        list.add(DefaultFragment.getInstance())
        // 添加实时界面
        list.add(RealTimeFragment.getInstance())
        // 添加收藏界面
        list.add(FavoriteFragment.getInstance())
        // 添加更多界面
        list.add(MoreFragment.getInstance())
        // 定义一个适配器
        val adapter = MainFragmentPagerAdapter(supportFragmentManager, list)
        // 不允许左右滑动
        viewPager.isScroll = false
        // 设置页面适配器
        viewPager.adapter = adapter

        // 默认按钮点击事件
        btnDefault.setOnClickListener {
            selectTabItem(0)
        }

        // 实时按钮点击事件
        btnRealTime.setOnClickListener {
            selectTabItem(1)
        }

        // 收藏按钮点击事件
        btnFav.setOnClickListener {
            selectTabItem(2)
        }

        // 更多按钮点击事件
        btnMore.setOnClickListener {
            selectTabItem(3)
        }

        // 默认选中第一个
        selectTabItem(0)
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/3 19:06
     * 方法描述：设置当前选项卡页面
     **/
    private fun selectTabItem(position: Int) {
        viewPager.currentItem = position
        btnDefault.setTextColor(Color.parseColor("#a0a0a0"))
        btnRealTime.setTextColor(Color.parseColor("#a0a0a0"))
        btnFav.setTextColor(Color.parseColor("#a0a0a0"))
        btnMore.setTextColor(Color.parseColor("#a0a0a0"))
        btnDefault.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                R.drawable.ic_domain_black_24dp), null, null)
        btnRealTime.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                R.drawable.ic_whatshot_black_24dp), null, null)
        btnFav.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                R.drawable.ic_favorite_black_24dp), null, null)
        btnMore.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                R.drawable.ic_more_horiz_black_24dp), null, null)
        when (position) {
            0 -> {
                btnDefault.setTextColor(Color.parseColor("#2196f3"))
                btnDefault.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                        R.drawable.ic_domain_orange_700_24dp), null, null)
            }
            1 -> {
                btnRealTime.setTextColor(Color.parseColor("#2196f3"))
                btnRealTime.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                        R.drawable.ic_whatshot_orange_700_24dp), null, null)
            }
            2 -> {
                btnFav.setTextColor(Color.parseColor("#2196f3"))
                btnFav.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                        R.drawable.ic_favorite_orange_700_24dp), null, null)
            }
            3 -> {
                btnMore.setTextColor(Color.parseColor("#2196f3"))
                btnMore.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(
                        R.drawable.ic_more_horiz_orange_700_24dp), null, null)
            }
        }
    }

    /**
     * 创建人：WangYongQi
     * 创建时间：2019/1/9 10:21
     * 方法描述：退出对话框
     **/
    private fun quitConfirmDialog() {
//        AlertDialog.Builder(this)
//                .setMessage(R.string.sys_ask_quit_message)
//                .setTitle(R.string.sys_ask_quit_title)
//                .setPositiveButton(R.string.sure) { _, _ ->
//                    finish()
//                }
//                .setNegativeButton(R.string.cancel) { _, _ -> }.show()
        // 如果用户点击了系统返回键，则模拟HOME键
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                quitConfirmDialog()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

}

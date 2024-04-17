package com.hyphenate.easeui.menu

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyphenate.easeui.common.extensions.mainScope
import com.hyphenate.easeui.interfaces.OnMenuDismissListener
import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.model.EaseMenuItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * It is the helper to manage the menus in UIKit.
 */
open class EaseMenuHelper {
    private val menuItems by lazy { mutableListOf<EaseMenuItem>() }
    private var itemClickListener: OnMenuItemClickListener? = null
    private var menuView: IMenu? = null
    protected var view: View? = null
    private var context: Context? = null

    open fun initMenu(view: View?) {
        this.view = view
        view?.let {
            context = it.context
            if (menuView == null) {
                menuView = EaseMenuDialog()
            }
        }
    }

    private fun addMenuItem() {
        if (menuItems.isEmpty()) {
            return
        }
        menuView?.registerMenus(menuItems)
    }

    /**
     * Add menu item.
     */
    fun addItemMenu(item: EaseMenuItem): EaseMenuHelper {
        if (!menuItems.contains(item)) {
            menuItems.add(item)
        }
        return this
    }

    /**
     * Add menu item.
     */
    fun addItemMenu(
        menuId: Int,
        order: Int,
        name: String,
        groupId: Int = 0,
        isVisible: Boolean = true,
        @DrawableRes resourceId: Int = -1,
        @ColorInt titleColor : Int = -1
    ): EaseMenuHelper {
        return addItemMenu(EaseMenuItem(menuId = menuId,
            order = order,
            title = name,
            groupId = groupId,
            isVisible = isVisible,
            resourceId = resourceId,
            titleColor = titleColor
        ))
    }

    /**
     * Clear menu items.
     */
    fun clear() {
        menuItems.clear()
        menuView?.clear()
    }

    /**
     * Dismiss menu.
     */
    fun dismiss() {
        menuView?.dismissMenu()
    }

    /**
     * Find the target menu.
     */
    fun findItem(menuId: Int): EaseMenuItem? {
        return menuItems.firstOrNull { it.menuId == menuId }
    }

    /**
     * Find the target menu item and set the visibility.
     */
    fun findItemVisible(menuId: Int, visible: Boolean) {
        menuItems.forEach {
            if (it.menuId == menuId) {
                it.isVisible = visible
            }
        }
    }

    /**
     * Set all menu items visibility.
     */
    fun setAllItemsVisible(visible: Boolean) {
        menuItems.forEach {
            it.isVisible = visible
        }
    }

    fun show() {
        addMenuItem()
        menuView?.let { menu ->
            if (menu is BottomSheetDialogFragment) {
                view?.run {
                    if (context is Activity) {
                        menu.show((context as AppCompatActivity).supportFragmentManager, "EaseConvMenuHelper")
                    } else {
                        throw IllegalArgumentException("Context must be Activity")
                    }
                } ?: throw IllegalArgumentException("View is null")
            }
        }
    }

    /**
     * Set menu cancelable.
     */
    fun setDialogCancelable(cancelable: Boolean) {
        if (menuView is DialogFragment) {
            (menuView as DialogFragment).isCancelable = cancelable
        }
    }

    /**
     * Set menu item click listener.
     */
    open fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        itemClickListener = listener
        menuView?.setOnMenuItemClickListener(listener)
    }

    /**
     * Set menu dismiss listener.
     */
    open fun setOnMenuDismissListener(listener: OnMenuDismissListener?) {
        menuView?.setOnMenuDismissListener(listener)
    }

    fun getContext(): Context? {
        return context
    }

    /**
     * Set the orientation of the menu.
     * After setting the orientation, you need to call [notifyDataSetChanged] to take effect.
     * @param orientation [EaseMenuItemView.MenuOrientation]
     */
    fun setMenuOrientation(orientation: EaseMenuItemView.MenuOrientation) {
        menuView?.let {
            (it as? EaseMenuDialog)?.getMenuAdapter()?.let { adapter->
                adapter.setMenuOrientation(orientation)
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Set the gravity of the menu.
     * After setting the gravity, you need to call [notifyDataSetChanged] to take effect.
     * @param gravity [EaseMenuItemView.MenuGravity]
     */
    fun setMenuGravity(gravity: EaseMenuItemView.MenuGravity) {
        menuView?.let {
            (it as? EaseMenuDialog)?.getMenuAdapter()?.let { adapter->
                adapter.setMenuGravity(gravity)
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Set whether to show cancel button.
     */
    fun showCancel(show: Boolean) {
        menuView?.let {
            (it as? EaseMenuDialog)?.showCancel(show)
        }
    }

    /**
     * Add top view for EaseMenuDialog.
     */
    fun addTopView(view: View) {
        this.view?.context?.mainScope()?.launch {
            delay(100)
            menuView?.let {
                (it as? EaseMenuDialog)?.addTopView(view)
            }
        }
    }

    /**
     * Clear top view for EaseMenuDialog.
     */
    fun clearTopView(){
        this.view?.context?.mainScope()?.launch {
            delay(100)
            menuView?.let {
                (it as? EaseMenuDialog)?.clearTopView()
            }
        }
    }

}
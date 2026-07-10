package com.easemob.quickstart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.easemob.quickstart.databinding.ActivityMainBinding
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatOptions
import com.hyphenate.easeui.common.extensions.showToast
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.chat.activities.UIKitChatActivity
import com.hyphenate.easeui.interfaces.ChatUIKitConnectionListener
import com.hyphenate.easeui.model.ChatUIKitProfile

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // The chat token used to log in to the Chat server.
    // In a real app you should obtain this from your own app server (the app server
    // exchanges the user's credentials for a chat token). Replace the value below with
    // a real, pre-provisioned chat token for the user.
    private val chatToken: String = ""

    private val connectListener by lazy {
        object : ChatUIKitConnectionListener() {
            override fun onConnected() {}

            override fun onDisconnected(errorCode: Int) {}

            override fun onLogout(errorCode: Int, info: String?) {
                super.onLogout(errorCode, info)
                showToast("You have been logged out, please log in again!")
                ChatLog.e(TAG, "")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSDK()
        initListener()
    }

    private fun initSDK() {
        val appkey = getString(R.string.app_key)
        if (appkey.isEmpty()) {
            applicationContext.showToast("You should set your AppKey first!")
            ChatLog.e(TAG, "You should set your AppKey first!")
            return
        }
        ChatOptions().apply {
            // Set your own appkey here
            this.appKey = appkey
            // Set not to log in automatically
            this.autoLogin = false
            // Set whether confirmation of delivery is required by the recipient. Default: false
            this.requireDeliveryAck = true
        }.let {
            ChatUIKitClient.init(applicationContext, it)
        }
    }

    private fun initListener() {
        ChatUIKitClient.addConnectionListener(connectListener)
    }

    fun login(view: View) {
        val username = binding.etUserId.text.toString().trim()
        if (username.isEmpty()) {
            showToast("Username cannot be empty!")
            ChatLog.e(TAG, "Username cannot be empty!")
            return
        }
        // Obtain the chat token before logging in. In a real app this comes from your
        // app server; here we use the pre-provisioned token field.
        if (chatToken.isEmpty()) {
            showToast("Chat token cannot be empty!")
            ChatLog.e(TAG, "Chat token cannot be empty!")
            return
        }
        if (!ChatUIKitClient.isInited()) {
            showToast("Please init first!")
            ChatLog.e(TAG, "Please init first!")
            return
        }
        ChatUIKitClient.login(ChatUIKitProfile(username), chatToken
            , onSuccess = {
                showToast("Login successfully!")
                ChatLog.e(TAG, "Login successfully!")
            }, onError = { code, message ->
                showToast("Login failed: $message")
                ChatLog.e(TAG, "Login failed: $message")
            }
        )
    }

    fun logout(view: View) {
        if (!ChatUIKitClient.isInited()) {
            showToast("Please init first!")
            ChatLog.e(TAG, "Please init first!")
            return
        }
        ChatUIKitClient.logout(false
            , onSuccess = {
                showToast("Logout successfully!")
                ChatLog.e(TAG, "Logout successfully!")
            }
        )
    }

    fun startChat(view: View) {
        val username = binding.etPeerId.text.toString().trim()
        if (username.isEmpty()) {
            showToast("Peer id cannot be empty!")
            ChatLog.e(TAG, "Peer id cannot be empty!")
            return
        }
        if (!ChatUIKitClient.isLoggedIn()) {
            showToast("Please login first!")
            ChatLog.e(TAG, "Please login first!")
            return
        }
        UIKitChatActivity.actionStart(this, username, ChatUIKitType.SINGLE_CHAT)
    }

    override fun onDestroy() {
        ChatUIKitClient.removeConnectionListener(connectListener)
        ChatUIKitClient.releaseGlobalListener()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
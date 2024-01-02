package com.demo.beavair.utils

import android.os.Message

class MessageUtil {
    companion object{
        fun getMessage(msgWhat: Int, arg: Int): Message {
            val msg = Message()
            msg.what = msgWhat
            msg.arg1 = arg
            return msg
        }
    }
}
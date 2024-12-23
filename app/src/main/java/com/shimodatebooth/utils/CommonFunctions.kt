package com.shimodatebooth.utils

class CommonFunctions {
    companion object {
        /// Hàm kiểm tra có chứa từ không
        fun hasCommonWord(str1: String, str2: String): Boolean {
            val words1 = str1.split(" ").map { it.lowercase() }
            val words2 = str2.split(" ").map { it.lowercase() }

            return words2.any { it in words1 }
        }
    }
}
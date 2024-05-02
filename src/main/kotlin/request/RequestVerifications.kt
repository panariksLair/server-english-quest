package com.github.panarik.request

import com.github.panarik.log

private const val TAG = "REQUEST_VERIFICATIONS:"

class RequestVerifications {

    /**
     * Common request verifications:
     * - empty username
     * - empty user pass
     * - wrong user pass
     * - Oversize request body
     */
    fun isValidRequest(request: Request): Boolean {
        return true
    }

    private fun isEmptyUserName(request: Request): Boolean {
        val userName = request.headers["User"]?.toString() ?: ""
        log.info("$TAG User='$userName'")
        val isEmpty = userName == ""
        if (isEmpty) {
            log.error("$TAG Empty User field caught.")
        }
        return isEmpty
    }

    private fun isEmptyUserPass(request: Request): Boolean {
        val userPass = request.headers["Pass"]?.toString() ?: ""
        val isEmpty = userPass == ""
        if (isEmpty) log.info("$TAG Empty Pass field caught.")
        return isEmpty
    }

    private fun hasWrongClientPassword(request: Request): Boolean {
        return false
    }

    private fun isNotOversize(request: Request): Boolean {
        val size = request.body.length
        log.info("$TAG Request size=$size")
        return if (size > 10_000_000) {
            log.error("$TAG Wrong client request size detected!")
            false
        } else true
    }

}
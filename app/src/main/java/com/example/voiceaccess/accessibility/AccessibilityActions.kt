package com.example.voiceaccess.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

/** Safe, reusable accessibility operations. Android may reject an action at any time. */
class AccessibilityActions(private val service: AccessibilityService) {
    fun performBack(): Boolean = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)

    fun performHome(): Boolean = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)

    fun scrollUp(): Boolean = scroll(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

    fun scrollDown(): Boolean = scroll(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)

    fun findNodeByText(text: String): AccessibilityNodeInfo? =
        service.rootInActiveWindow?.findAccessibilityNodeInfosByText(text)?.firstOrNull()

    fun findNodeByContentDescription(description: String): AccessibilityNodeInfo? =
        findFirst(service.rootInActiveWindow) { node ->
            node.contentDescription?.toString()?.equals(description, ignoreCase = true) == true
        }

    fun clickNode(node: AccessibilityNodeInfo?): Boolean {
        var current = node
        while (current != null) {
            if (current.isEnabled && current.isClickable) {
                return current.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            current = current.parent
        }
        return false
    }

    /** Works only when the app exposes an editable accessibility node and Android permits it. */
    fun setText(node: AccessibilityNodeInfo?, text: String): Boolean {
        if (node == null || !node.isEnabled || !node.isEditable) return false
        val arguments = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }

    private fun scroll(action: Int): Boolean {
        val root = service.rootInActiveWindow ?: return false
        val scrollableNode = findFirst(root) { it.isScrollable } ?: return false
        return scrollableNode.performAction(action)
    }

    private fun findFirst(
        root: AccessibilityNodeInfo?,
        predicate: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        if (root == null) return null
        if (predicate(root)) return root
        for (index in 0 until root.childCount) {
            val result = findFirst(root.getChild(index), predicate)
            if (result != null) return result
        }
        return null
    }
}
